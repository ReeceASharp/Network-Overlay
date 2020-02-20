package cs455.overlay.node;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPSenderThread;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.util.StatisticsCollectorAndDisplay;
import cs455.overlay.wireformats.*;

public class Registry implements Node {

	public static void main(String[] args) throws IOException {
		Registry node = new Registry();

		int port = Integer.parseInt(args[0]);

		InetAddress ip = InetAddress.getLocalHost();
		String host = ip.getHostName();

		System.out.printf("Host: %s, Port: %s, HostIP: %s%n", host, port, ip.getHostAddress());

		// start the server thread that will listen for clients wanting to connect
		Thread server = new Thread(new TCPServerThread(node, port));
		server.start();

		// start the interactive client
		Thread parser = new Thread(new InteractiveCommandParser(Protocol.REGISTRY, node));
		parser.start();

	}

	// private static StatisticsCollectorAndDisplay statDisplay;
	private NodeList nodeList;
	private String serverIP;					//
	private int serverPort;
	private RoutingTable[] tables;		//keep track of RoutingTables being sent to MessagingNodes
	private StatisticsCollectorAndDisplay display;
	private int[] knownIDs;
	
	private boolean ready;		//flags to help allow only 1 thread inside
	private boolean done;
	

	public Registry() {
		nodeList = new NodeList();
		ready = false;
		done = false;
	}

	@Override
	public String getServerIP() {
		return serverIP;
	}

	@Override
	public int getServerPort() {
		return serverPort;
	}

	private void listNodes() {
		nodeList.sort();
		System.out.println(nodeList);
	}
	
	private void listTables() {
		System.out.println("Routing Tables:");
		for (int i = 0; i < nodeList.size(); i++) {
			System.out.println(nodeList.get(i));
			System.out.println(tables[i] + "\n");
		}
		
	}

	// node wants to deregister
	private void nodeDeregistration(Event e, Socket socket) throws IOException {
		System.out.println("RECEIVED NODE DEREGISTRATION REQUEST");

		OverlayNodeSendsDeregistration deregistration = (OverlayNodeSendsDeregistration) e;
		
		// NOTE: This doesn't work on my local router for some reason, but is completely fine
		// on the school network
		/*
		if (!(ip.equals(socket.getInetAddress().getHostAddress()))) {
			System.out.printf("Socket IP doesn't match: '%s' vs '%s'%n", ip,
					 socket.getInetAddress().getHostAddress());
			return new NodeResponse(false, "Error: Connection IP doesn't match payload IP. ");
		}
		*/
		
		int status = nodeList.removeNode(deregistration.getIP(), deregistration.getPort());
		String message = null;
		if (status > -1) {
			message = "Success: Node was removed from the Registry";
		} else {
			message = "Error: Node wasn't removed";
		}	
		

		
		byte[] marshalledBytes = new RegistryReportsDeregistrationStatus(status, message).getBytes();
		sendMessage(socket, marshalledBytes);
	}

	// node wants to register with the registry
	private void nodeRegistration(Event e, Socket socket) throws IOException {
		//System.out.println("RECEIVED NODE REGISTRATION REQUEST");
		OverlayNodeSendsRegistration registration = (OverlayNodeSendsRegistration) e;
		
		String ip = registration.getIP();
		int port = registration.getPort();
		
		int id = nodeList.insertNode(ip, port, socket);
		String message;
		if (id > -1) {
			message = new String("Success: MessagingNode was added to Registry, ID: " + id);
		} else {
			message = new String("Error: MessagingNode was not added. Registry already contains a node with this IP:Port combination.");
		}
		
		// build response to send across socket
		byte[] marshalledBytes = new RegistryReportsRegistrationStatus(id, message).getBytes();
		
		sendMessage(socket, marshalledBytes);
	}

	private void nodeReportTraffic(Event e) {
		System.out.println("nodeReportTraffic()");
		OverlayNodeReportsTrafficSummary summary = (OverlayNodeReportsTrafficSummary) e;
		int index = nodeList.getIndex(summary.getID());
		
		//pull data from message and put it into the display
		display.setResult(index, summary.getSentPackets(), summary.getReceivedPackets(), summary.getRelayedPackets(),
				summary.getPayloadSentSum(), summary.getPayloadReceivedSum());
		
		synchronized(this) {
			if (!display.missingData()) {
				System.out.println("Printing Display");
				System.out.println(display);
				display.reset();
			}
		}
	}

	// node is reporting its status
	private void nodeSetupStatus(Event e) {
		NodeReportsOverlaySetupStatus status = (NodeReportsOverlaySetupStatus) e;
		
		//update node of list
		nodeList.getByID(status.getStatus()).setReady();
		System.out.println(status.getInfo());
		
		//check that this is the last one
		if (nodeList.readyToStart() && !ready) {
			ready = true;
			System.out.println("Registry now ready to initiate tasks.");
			//setup Statistics of nodeList size to hold the message values
			display = new StatisticsCollectorAndDisplay(nodeList.size(), knownIDs);
		}
	}

	private void nodeTaskFinished(Event e) {
		System.out.println("nodeTaskFinished");
		OverlayNodeReportsTaskFinished task = (OverlayNodeReportsTaskFinished) e;
		
		nodeList.getByID(task.getID()).setDone();
		
			if (nodeList.completelyDone() && !done) {
				done = true;
				int secondsToWait = 5;
				System.out.printf("All nodes have Finished! Waiting %d seconds for message relay to complete.%n", secondsToWait);
				//wait for messages to finish routing
				try {
					Thread.sleep(secondsToWait * 1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				fetchResults();
			}
			
	}
	
	private void fetchResults() {
		System.out.println("Fetching results...");
		byte[] marshalledBytes = new RegistryRequestsTrafficSummary().getBytes();
		
		try {
			for (int i = 0; i< nodeList.size(); i++) 
				sendMessage(nodeList.get(i).getSocket(), marshalledBytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCommand(String[] command) {
		switch (command[0].toLowerCase()) {
		case "list-messaging-nodes":
			listNodes();
			break;
		case "setup-overlay":
			setupOverlay(command);
			break;
		case "list-routing-tables":
			listTables();
			break;
		case "start":
			sendInitiate(command);
			break;
		default:
			System.out.println("Should never reach this");
		}

	}

	@Override
	public void onEvent(Event e, Socket socket)  {
		try {
		switch (e.getType()) {
		case Protocol.OVERLAY_NODE_SENDS_REGISTRATION:
			nodeRegistration(e, socket);
			break;
		case Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION:
			nodeDeregistration(e, socket);
			break;
		case Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS:
			nodeSetupStatus(e);
			break;
		case Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED:
			nodeTaskFinished(e);
			break;
		case Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY:
			nodeReportTraffic(e);
			break;
		default:
			System.err.printf("Registry::onEvent::invalid control message: %d%n", e.getType());
			break;
		}
		} catch (IOException ioe) {
			System.out.println("Error handling Message: ");
			ioe.printStackTrace();
		}
	}

	private void sendMessage(Socket socket, byte[] marshalledBytes) throws IOException {
		new Thread(new TCPSenderThread(socket, marshalledBytes)).start();
	}

	private void setupOverlay(String[] command) {
		//get parameter, and hopefully n <= (2^n)-1
		if (command.length != 2) {
			System.out.println("Error: Invalid # of parameters, only specify a table size, " + command.length);
			return;
		}
		
		//test if the second parameter is valid
		int tableSize = Integer.parseInt(command[1]);
		
		if (Math.pow(tableSize-1, 2) + 1 > nodeList.size() && tableSize > 0) {
			System.out.println("Error: Invalid overlay size: " + tableSize);
			return;
		}
		//ascending order, allows simple indexing to generate the tables
		nodeList.sort();
		
		//setup tables
		setupRoutingTables(tableSize);
		
		//save to pass into display later
		knownIDs = nodeList.generateKnownIDs();
		
		//start the sendout if necessary
		RoutingTable temp;
		byte[] marshalledBytes = null;
		
		
		System.out.println("Sending out Routing Tables to MessagingNodes");
		//send each table off to its respective node
		for (int i = 0; i < nodeList.size(); i++) {
			temp = tables[i];
			
			marshalledBytes = new RegistrySendsNodeManifest(temp.getNodes(), knownIDs).getBytes();
			
			try {
				sendMessage(nodeList.get(i).getSocket(), marshalledBytes);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//bases its information off of the data inside of nodeList
	private void setupRoutingTables(int tableSize) {
		tables = new RoutingTable[nodeList.size()];
		
		for (int i = 0; i < nodeList.size(); i++) {
			tables[i] = RoutingTable.generateTable(nodeList, i, tableSize);
		}
	}

	@Override
	public void updateServerInfo(String ip, int port) {
		serverIP = ip;
		serverPort = port;
	}

	
	private void sendInitiate(String[] command) {
		//check that the system is setup
		if (!ready) {
			System.out.println("Error: Not Ready to send");
			return;
		}
		
		//check message for validity
		if (command.length == 2 && (Integer.parseInt(command[1]) > 0)) {
			System.out.println("Sending Data");
			byte[] marshalledBytes = new RegistryRequestsTaskInitiate(Integer.parseInt(command[1])).getBytes();
			
			try {
				for (int i = 0; i < nodeList.size(); i++) {
					sendMessage(nodeList.get(i).getSocket(), marshalledBytes);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else
			System.out.println("Invalid Start parameters");
		
	}
	
	
	//when each message comes through it checks to see if the other nodes are ready to go
	//
	public void setReady() {
		ready = true;
	}

	@Override
	public void exit() {
		
	}
}

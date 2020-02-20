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

		System.out.printf("Host: %s, HostIP: %s, ", host, ip.getHostAddress());

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
	private boolean printing;

	public Registry() {
		
		nodeList = new NodeList();
		ready = false;
		done = false;
		printing = false;
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
			System.out.println("Node: " + nodeList.get(i));
			System.out.println(tables[i] + "\n");
		}

	}

	//Received a message that a node wants to register, remove if possible, and respond to node on status
	private synchronized void nodeDeregistration(Event e, Socket socket) throws IOException {
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
		int status = nodeList.contains(deregistration.getIP(), deregistration.getPort());

		String message = null;
		if (status > -1) {
			//adjust index to ID
			nodeList.removeNode(deregistration.getIP(), deregistration.getPort());
			message = "Success: Node was removed from the Registry";
		} else {
			message = "Error: Node not found in Registry";
		}	

		byte[] marshalledBytes = new RegistryReportsDeregistrationStatus(status, message).getBytes();
		sendMessage(socket, marshalledBytes);
	}

	// node wants to register with the registry, check if possible, and respond with results to node
	private synchronized void nodeRegistration(Event e, Socket socket) throws IOException {

		OverlayNodeSendsRegistration registration = (OverlayNodeSendsRegistration) e;

		String ip = registration.getIP();
		int port = registration.getPort();

		int id;
		synchronized (this) {
			id = nodeList.insertNode(ip, port, socket);
		}

		String message;
		if (id > -1) {
			message = String.format("Success: MessagingNode added with ID #%d, there are currently (%d) node(s)%n",id, nodeList.size());
		} else {
			message = new String("Error: MessagingNode was not added. Registry already contains a node with this IP:Port combination.");
		}

		// build response to send across socket
		byte[] marshalledBytes = new RegistryReportsRegistrationStatus(id, message).getBytes();

		sendMessage(socket, marshalledBytes);
	}

	//A messaging node has sent in some results on the latest routing run of the overlay
	private void nodeReportTraffic(Event e) {
		OverlayNodeReportsTrafficSummary summary = (OverlayNodeReportsTrafficSummary) e;
		int index = nodeList.getIndex(summary.getID());
		//pull data from message and put it into the display
		display.setResult(index, summary.getSentPackets(), summary.getReceivedPackets(), summary.getRelayedPackets(),
				summary.getPayloadSentSum(), summary.getPayloadReceivedSum());

		synchronized(this) {
			if (!printing && display.isMissingData()) {
				printing = true;
				System.out.println(display);
			}
		}
	}

	// node is reporting its status of setting its connections in the overlay
	// when all of the nodes have finished allow the starting of the overlay
	private void nodeSetupStatus(Event e) {
		NodeReportsOverlaySetupStatus status = (NodeReportsOverlaySetupStatus) e;

		//update node of list
		nodeList.getByID(status.getStatus()).setReady();

		if (nodeList.readyToStart() && !ready) {
			ready = true;
			System.out.println("Registry now ready to initiate tasks.");
			//setup Statistics of nodeList size to hold the message values
			display = new StatisticsCollectorAndDisplay(nodeList.size(), knownIDs);
		}
	}

	// A node is responding that it was able to successfully send out 'x' amount of messages
	// when all of the nodes have sent this in, wait a bit and then send out a request for all of 
	// their results
	private void nodeTaskFinished(Event e) {
		OverlayNodeReportsTaskFinished task = (OverlayNodeReportsTaskFinished) e;

		nodeList.getByID(task.getID()).setDone();

		if (nodeList.completelyDone() && !done) {
			done = true;
			int secondsToWait = 10;
			System.out.printf("All nodes have finished. Waiting %d seconds for message relay to complete.%n", secondsToWait);
			//wait for messages to finish routing
			try {
				Thread.sleep(secondsToWait * 1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			fetchResults();
		}

	}

	// gathering results from each of the nodes
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

	//handling user-input
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

	//message routing
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

	//send the bytes through a specific connection via thread
	private void sendMessage(Socket socket, byte[] marshalledBytes) throws IOException {
		new Thread(new TCPSenderThread(socket, marshalledBytes)).start();
	}

	// Send out a list of specific node IP:port data to seach node so it can set up its connections
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
				e.printStackTrace();
			}
		}
	}

	// creating each routing list to be passed to the nodes
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

	// Sending a message to each of the nodes in the overlay to begin sending out 'x' packages
	private void sendInitiate(String[] command) {
		//check that the system is setup
		if (!ready) {
			System.out.println("Error: Not Ready to send");
			return;
		}

		//check message for validity
		if (command.length == 2 && (Integer.parseInt(command[1]) > 0)) {
			//allows for multiple runs on same overlay
			nodeList.restart();
			done = false;
			printing = false;
			display.reset();
			
			System.out.println("Initializing nodes...");
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

	//empty, only used inside of MessengingNode to exit upon losing connection with the Registry
	@Override
	public void exit() {

	}
}

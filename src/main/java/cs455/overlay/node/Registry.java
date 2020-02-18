package cs455.overlay.node;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPSenderThread;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.wireformats.*;

public class Registry implements Node {

	//package data to pass around easier around class, boolean is whether register/deregister was successful
	private class NodeResponse {
		public String message;
		public boolean status;

		public NodeResponse(boolean status, String message) {
			this.status = status;
			this.message = message;
		}

		public String getMessage() { return message; }
		public boolean successful() { return status; }
	}

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
	private ArrayList<RoutingTable> tables;		//keep track of RoutingTables being sent to MessagingNodes
	private boolean ready;

	public Registry() {
		nodeList = new NodeList();
		tables = new ArrayList<>();
		ready = false;
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
		System.out.println("Registry::listTables");
		for (RoutingTable rt : tables) {
			System.out.println(rt);
		}
		
		System.out.println(tables);
	}

	// node wants to deregister
	private synchronized void nodeDeregistration(Event e, Socket socket) throws IOException {
		System.out.println("RECEIVED NODE DEREGISTRATION REQUEST");

		OverlayNodeSendsDeregistration deregistration = (OverlayNodeSendsDeregistration) e;
		NodeResponse response = validateDeregister(deregistration.getIP(), deregistration.getPort(), 
				socket);
		
		int index;
		if (response.successful()) {
			index = nodeList.contains(deregistration.getIP(), deregistration.getPort());
			System.out.printf("Success: Found node: '%s'%n", nodeList.get(index));
			nodeList.removeNode(index);
		} else {
			index = -1;
		}	
		

		
		byte[] marshalledBytes = new RegistryReportsDeregistrationStatus(index, response.getMessage()).getBytes();
		sendMessage(socket, marshalledBytes);
	}

	// node wants to register with the registry
	private synchronized void nodeRegistration(Event e, Socket socket) throws IOException {
		//System.out.println("INSIDE REGISTRATION");
		OverlayNodeSendsRegistration registration = (OverlayNodeSendsRegistration) e;

		// check if Registry is fill, and that the node is accurate
		NodeResponse response = validateRegister(registration.getIP(), registration.getPort(), 
				socket);
		
		int id;
		if (response.successful()) {
			id = nodeList.getOpenID();
			nodeList.insertNode(new NodeData(registration.getIP(), registration.getPort(), id, socket));
		} else
			id = -1;
			
		// build response to send across socket
		byte[] marshalledBytes = new RegistryReportsRegistrationStatus(id, response.getMessage()).getBytes();
		
		sendMessage(socket, marshalledBytes);
	}

	private void nodeReportTraffic(Event e) {
		System.out.println("nodeReportTraffic()");
	}

	// node is reporting its status
	private void nodeSetupStatus(Event e) {
		System.out.println("nodeSetupStatus");
		NodeReportsOverlaySetupStatus status = (NodeReportsOverlaySetupStatus) e;
		
		//update node of list
		nodeList.getByID(status.getStatus()).setReady();
		
		//check that this is the last one
		if (nodeList.readyToStart())
			ready = true;
	}

	private void nodeTaskFinished(Event e) {
		System.out.println("nodeTaskFinished");
	}

	@Override
	public void onCommand(String[] command) {
		/*
		System.out.print("Registry::onCommand::Command_Length:" + command.length + ": ");
		for (String s : command)
			System.out.print("'" + s + "' ");
		System.out.println();
	 	*/

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
		//System.out.println("Getting Event type");
		try {
		switch (e.getType()) {
		case Protocol.OVERLAY_NODE_SENDS_REGISTRATION:
			nodeRegistration(e, socket);
			break;
		case Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION:
			nodeDeregistration(e, socket);
			break;
		case Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS:
			//TODO: keep track of whether each node's setup success was received inside nodeData?
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
		System.out.println("Registry::setupOverlay::STARTING UP");
		if (command.length != 2) {
			
			System.out.println("Error: Invalid # of parameters, only specify a table size, " + command.length);
			return;
		}
		
		//test if the second parameter is valid
		int tableSize = Integer.parseInt(command[1]);
		
		if (Math.pow(tableSize-1, 2) + 1 > nodeList.size() && tableSize > 0) {
			System.out.println("Error: Invalid overlay size: " + tableSize);
			return;
		} else {
			System.out.println("Sending out Routing Tables to MessagingNodes");
		}
		
		nodeList.sort();
		
		//setup tables
		setupRoutingTables(tableSize);
		
		int[] knownIDs = nodeList.generateKnownIDs();
		
		//start the sendout if necessary
		RoutingTable temp;
		byte[] marshalledBytes = null;
		
		//send each table off to its respective node
		for (int i = 0; i < nodeList.size(); i++) {
			temp = tables.get(i);
			
			marshalledBytes = new RegistrySendsNodeManifest(temp.getIpList(), 
					temp.getPortList(), temp.getIdList(), knownIDs).getBytes();
			
			new Thread(new TCPSenderThread(nodeList.get(i).getSocket(),
					marshalledBytes)).start();
		}
	}
	
	//bases its information off of the data inside of nodeList
	private void setupRoutingTables(int tableSize) {
		
		//cache so it doesn't query nodeList every time
		for (int i = 0; i < nodeList.size(); i++) {
			tables.add(RoutingTable.generateTable(nodeList, i, tableSize));
		}
		
	}

	// TODO: may need to synchronize this, as its setters and getters may be
	// accessed simultaneously, but this is done at the very beginning, so
	// multiple accesses aren't capable of happening
	@Override
	public void updateServerInfo(String ip, int port) {
		serverIP = ip;
		serverPort = port;
	}

	private synchronized NodeResponse validateDeregister(String ip, int port, Socket socket) {

		//make sure node is in registry to be taken out
		if (nodeList.contains(ip, port) == -1) {
			return new NodeResponse(false, "Error: MessagingNode isn't in overlay.");
		}

		// NOTE: This doesn't work on my local router for some reason, but is completely fine
		// on the school network
		/*
		if (!(ip.equals(socket.getInetAddress().getHostAddress()))) {
			System.out.printf("Socket IP doesn't match: '%s' vs '%s'%n", ip,
					 socket.getInetAddress().getHostAddress());
			return new NodeResponse(false, "Error: Connection IP doesn't match payload IP. ");
		}
		*/

		return new NodeResponse(true, "Success: Node successfully deregistered.");
	}

	private synchronized NodeResponse validateRegister(String ip, int port, Socket socket) {
		// NOTE: This doesn't work on my local router for some reason, but is completely fine
		// on the school network
		/*
		 * if (!(ip.equals( socket.getInetAddress().getHostAddress() ))) {
		 * System.out.printf("Socket IP doesn't match: '%s' vs '%s'%n", ip,
		 * socket.getInetAddress().getHostAddress()); return new NodeResponse(false,
		 * "Error: Socket IP doesn't match payload IP"); }
		 */

		// make sure registry isn't already full
		if (!nodeList.full()) 
			return new NodeResponse(false, "Error: MessagingNode was not added. No available space inside Registry for registration.");

		// make sure node isn't already in registry
		if (nodeList.contains(ip, port) > -1) 
			return new NodeResponse(false, "Error: MessagingNode was not Added. Registry already contains a node with this IP:Port combination.");

		//TODO: Fix allocation of string, as this is done out of sync
		//Node can be added to registry, but append increment size before as this is generated it's added
		return new NodeResponse(true, String.format("Success: MesssagingNode was successfully added. There are currently (%s) nodes"
				+ " in the system.", nodeList.size() + 1));
	}
	
	private void sendInitiate(String[] command) {
		//check that the system is setup
		if (!ready) {
			System.out.println("Error: Not Ready to send");
			return;
		}		
		
		//check message for validity
		if (command.length != 2 && (Integer.parseInt(command[1]) < 1)) {
			
		}
		
	}
	
	
	//when each message comes through it checks to see if the other nodes are ready to go
	//
	public void setReady() {
		ready = true;
	}

	@Override
	public void exit() {
		// TODO Auto-generated method stub
		
	}
}

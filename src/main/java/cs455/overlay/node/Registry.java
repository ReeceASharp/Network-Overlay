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

	// private static StatisticsCollectorAndDisplay statDisplay;

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

	//private TCPConnectionsCache cache;
	//private TCPServerThread server;
	private NodeList nodeList;
	private String serverIP;					//
	private int serverPort;
	private ArrayList<RoutingTable> tables;		//

	public Registry() {
		nodeList = new NodeList();
		tables = new ArrayList<>();
	}

	/*
	@Override
	public TCPConnectionsCache getCache() {

		// TODO Auto-generated method stub
		return null;
	}
	*/

	@Override
	public String getServerIP() {
		return serverIP;
	}

	@Override
	public int getServerPort() {
		return serverPort;
	}

	private void listNodes() {
		System.out.println(nodeList);
	}

	// node wants to deregister
	private void nodeDeregistration(Event e, Socket socket) throws IOException {
		System.out.println("RECEIVED NODE DEREGISTRATION REQUEST");

		OverlayNodeSendsDeregistration deregistration = (OverlayNodeSendsDeregistration) e;
		NodeResponse response = validateDeregister(deregistration.getIP(), deregistration.getPort(), 
				socket);
		
		int index;
		if (response.successful()) {
			index = nodeList.contains(deregistration.getIP(), deregistration.getPort());
			System.out.printf("Success: Found node: '%s'%n", nodeList.get(index));
		} else {
			index = -1;
		}	
		byte[] marshalledBytes = new RegistryReportsDeregistrationStatus(index, response.getMessage()).getBytes();
		
		
		sendMessage(socket, marshalledBytes);
	}

	// node wants to register with the registry
	private void nodeRegistration(Event e, Socket socket) throws IOException {
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

	private void nodeReportTraffic() {
		System.out.println("nodeReportTraffic()");
	}

	// node is reporting its status
	private void nodeSetupStatus() {
		System.out.println("nodeSetupStatus");
		//save to a data structure that keeps track of
	}

	private void nodeTaskFinished() {
		System.out.println("nodeTaskFinished");
	}

	@Override
	public void onCommand(String[] command) {
		System.out.print("Registry::onCommand::Command_Length:" + command.length + ": ");
		for (String s : command)
			System.out.print("'" + s + "' ");
		System.out.println();

		switch (command[0]) {
		case "list-messaging-nodes":
			listNodes();
			break;
		case "setup-overlay":
			setupOverlay(command);
			break;
		case "list-routing-tables":
			
			break;
		case "start":

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
			nodeSetupStatus();
			break;
		case Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED:
			nodeTaskFinished();
			break;
		case Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY:
			nodeReportTraffic();
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

	private void setupOverlay(String[] args) {
		//get parameter, and hopefully n <= (2^n)-1
	}

	// TODO: may need to synchronize this, as its setters and getters may be
	// accessed simultaneously
	@Override
	public void updateServerInfo(String ip, int port) {
		serverIP = ip;
		serverPort = port;
	}

	private NodeResponse validateDeregister(String ip, int port, Socket socket) {

		//make sure node is in registry to be taken out
		if (nodeList.contains(ip, port) == -1) {
			return new NodeResponse(false, "Error: MessagingNode isn't in overlay.");
		}

		if (!(ip.equals(socket.getInetAddress().getHostAddress()))) {
			
			System.out.println("Error: connection IP doesn't match payload IP"); //return ; }
			return new NodeResponse(false, "Error: Connection IP doesn't match payload IP. ");
		}

		return new NodeResponse(true, "Success: Node successfully deregistered.");
	}

	private NodeResponse validateRegister(String ip, int port, Socket socket) {
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
			return new NodeResponse(false, "Error: No available space inside Registry for registration.");

		// make sure node isn't already in registry
		if (nodeList.contains(ip, port) > -1) 
			return new NodeResponse(false, "Error: Registry already contains a node with this IP:Port combination.");

		//Node can be added to registry
		return new NodeResponse(true, String.format("Success: MesssagingNode was successfully added. There are currently (%s) nodes"
				+ " in the system.", nodeList.size() + 1));
	}

}

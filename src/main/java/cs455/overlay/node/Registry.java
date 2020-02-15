package cs455.overlay.node;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import cs455.overlay.transport.TCPSenderThread;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.RegistryReportsRegistrationStatus;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;

public class Registry implements Node {

	// used in the check during registry
	public class nodeResponse {
		public int status;
		public String message;

		public nodeResponse(int status, String message) {
			this.status = status;
			this.message = message;
		}
	}

	// private static StatisticsCollectorAndDisplay statDisplay;

	private NodeList nodeList;
	// private EventFactory factory;

	private String serverIP;
	private int serverPort;

	public Registry() {
		// factory = EventFactory.getInstance();
		nodeList = new NodeList();
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

		// System.out.println("Registry::main::exiting");
	}

	@Override
	public void onEvent(Event e, Socket socket) {
		switch (e.getType()) {
		case Protocol.OVERLAY_NODE_SENDS_REGISTRATION:
			try {
				nodeRegistration(e, socket);
			} catch (IOException e1) {
				System.out.printf("Failed nodeRegistration: '%s'%n", e.toString());
				e1.printStackTrace();
			}
			break;
		case Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION:
			nodeDeRegistration();
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
	}

	@Override
	public void onCommand(String[] command) {
		System.out.print("Registry::onCommand::Commands (len" + command.length + ": ");
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

	// node wants to register with the registry
	private void nodeRegistration(Event e, Socket socket) throws IOException {
		// check if node already exists
		String message = "rer";

		OverlayNodeSendsRegistration registration = (OverlayNodeSendsRegistration) e;
		// System.out.printf("Registry::nodeRegistration::IP: '%s, Port: %d%n",
		// registration.getIP(), registration.getPort());
		// System.out.printf("Registry::nodeRegistration::IP: '%s, Port: %d%n",
		// e.getIP(), e.getPort());

		// check if Registry is fill, and that the node is accurate
		int status = checkNode(registration.getIP(), registration.getPort(), socket);

		// if (status >= -1)
		nodeList.insertNode(new NodeData(registration.getIP(), registration.getPort(), status));

		// System.out.println(nodeList);

		// respond with a message
		byte[] marshalledBytes = new RegistryReportsRegistrationStatus(status, message).getBytes();

		// TODO: possibly change to anonymous thread
		// System.out.println("Sending to: " + socket);

		sendMessage(socket, marshalledBytes);

	}

	private void sendMessage(Socket socket, byte[] marshalledBytes) throws IOException {
		new Thread(new TCPSenderThread(socket, marshalledBytes)).start();
	}

	private int checkNode(String ip, int port, Socket socket) {
		// TEST: This doesn't work on my router for some reason, but is completely file
		// on school computers
		/*
		 * if (!(payload.getIP()).equals(socket.getInetAddress().getHostAddress())) {
		 * System.out.println("Socket IP doesn't match"); //return ; }
		 */

		// check that there's open space for it
		if (!nodeList.full()) {
			System.out.println("nodeList is full (128 inside already");
			return -1;
		}

		// check that it isn't already inside
		if (nodeList.contains(ip, port)) {
			System.out.println("Contains value");
			return -1;
		}

		// they're unequal
		return nodeList.getOpenID();
	}

	// node wants to deregister
	private void nodeDeRegistration() {

	}

	// node is reporting its status
	private void nodeSetupStatus() {

	}

	private void nodeTaskFinished() {

	}

	private void nodeReportTraffic() {

	}

	private void setupOverlay(String[] args) {

	}

	private void listNodes() {
		System.out.println(nodeList);
	}

	// TODO: may need to synchronize this, as its setters and getters may be
	// accessed simultaneously
	@Override
	public void updateServerInfo(String ip, int port) {
		serverIP = ip;
		serverPort = port;
	}

	@Override
	public String getServerIP() {
		return serverIP;
	}

	@Override
	public int getServerPort() {
		return serverPort;
	}

}

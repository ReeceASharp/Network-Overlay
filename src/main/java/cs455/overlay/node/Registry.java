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
import cs455.overlay.wireformats.EventFactory;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;

public class Registry implements Node {

	//private static StatisticsCollectorAndDisplay statDisplay;


	NodeList nodeList;


	private EventFactory factory;

	private byte[] serverIP;
	private int serverPort;


	public Registry() { 
		factory = EventFactory.getInstance();
		nodeList = new NodeList();
	}

	public static void main(String[] args) throws IOException {
		Registry node = new Registry();
		
		int port = Integer.parseInt(args[0]);
		
		
		InetAddress ip = InetAddress.getLocalHost();
		String host = ip.getHostName();
		
		
		System.out.println("Host: " + host + ", Port: " + port);

		//InetAddress addr = InetAddress.getByName("127.0.0.1");
		//InetAddress add2 = InetAddress.getByName("localhost");
		
		//System.out.println("addr: " + addr + ", add2: " + add2 );
		
		//start the server thread that will listen for clients wanting to connect
		Thread server = new Thread(new TCPServerThread(node));
		server.start();

		//start the interactive client
		Thread parser = new Thread(new InteractiveCommandParser(Protocol.REGISTRY, node));
		parser.start();

		//System.out.println("Registry::main::exiting");
	}

	@Override
	public void onEvent(Event e, Socket socket) {
		switch(e.getType()) {
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
		System.out.printf("Registry::onCommand:: '%s'%n", command.toString());

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
	public EventFactory getFactory() {
		return factory;
	}

	//node wants to register with the registry
	private void nodeRegistration(Event e, Socket socket) throws IOException {
		//check if node already exists
		String message = "rer";
		
		
		
		
		OverlayNodeSendsRegistration registration = (OverlayNodeSendsRegistration) e;
		System.out.printf("Registry::nodeRegistration::IP: '%s, Port: %d%n", registration.getIP(), registration.getPort());
		//System.out.printf("Registry::nodeRegistration::IP: '%s, Port: %d%n", e.getIP(), e.getPort());
		

		//check if Registry is fill, and that the node is accurate
		int status = checkNode(registration, socket);

		if (status != -1)
			nodeList.insertNode(new NodeData(registration.getIP(), registration.getPort(), status));

		System.out.println(nodeList);

		//respond with a message
		byte[] marshalledBytes = new RegistryReportsRegistrationStatus(status, message).getBytes();

		//TODO: possibly change to anonymous thread
		System.out.println("Sending to: " + socket);
		Thread sender = new Thread(new TCPSenderThread(socket, marshalledBytes));
		sender.start();


		sendMessage(socket);

	}



	private void sendMessage(Socket socket) {

	}

	private int checkNode(OverlayNodeSendsRegistration payload, Socket socket) {
		System.out.println("Registry::checkNode");
		//check that the payload IP matches the IP address it came from
		System.out.println(payload.getIP() + " vs. " + socket.getInetAddress());

		
		if (!(new String(payload.getIP()).equals(socket.getLocalAddress().getHostAddress()))) {
			return -1;
		}

		//check that there's open space for it


		//check that it isn't already inside

		return -1;
	}

	//node wants to deregister
	private void nodeDeRegistration() {

	}

	//node is reporting its status
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

	//TODO: may need to synchronize this, as its setters and getters may be accessed simultaneously
	@Override
	public void updateServerInfo(byte[] ip, int port) {
		serverIP = ip;
		serverPort = port;
	}

	@Override
	public byte[] getServerIP() {
		return serverIP;
	}

	@Override
	public int getServerPort() {
		return serverPort;
	}
}

package cs455.overlay.node;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;

public class Registry implements Node {
	
	//private static StatisticsCollectorAndDisplay statDisplay;

	
	NodeList nodeList;
	
	
	private EventFactory factory;
	
	private String serverIP;
	private int serverPort;
	
	
	public Registry() { 
		factory = EventFactory.getInstance();
		nodeList = new NodeList();
	}
	
	public static void main(String[] args) throws IOException {
		Registry node = new Registry();
		
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
			case (Protocol.OVERLAY_NODE_SENDS_REGISTRATION):
				try {
					nodeRegistration(e, socket);
				} catch (IOException e1) {
					System.out.printf("Failed nodeRegistration: '%s'%n", e.toString());
					e1.printStackTrace();
				}
				break;
			case (Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION):
				nodeDeRegistration();
				break;
			case (Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS):
				nodeSetupStatus();
				break;
			case (Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED):
				nodeTaskFinished();
				break;
			case (Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY):
				nodeReportTraffic();
				break;
			default:
				System.err.printf("Registry::onEvent::invalid control message: %d%n", e.getType());
				break;
		}
	}
	
	public void onCommand(String[] command) {
		System.out.printf("Registry::onCommand:: '%s'%n", command.toString());
		
		switch (command[0]) {
			case "list-messaging-nodes":
				break;
			case "setup-overlay":
				break;
			case "list-routing-tables":
				break;
			case "start":
				break;
			default:
				System.out.println("Should never reach this");
	}
		
	}
	
	public EventFactory getFactory() {
		return factory;
	}
	
	//node wants to register with the registry
	private void nodeRegistration(Event e, Socket socket) throws IOException {
		//check if node already exists
		String message;
		
		OverlayNodeSendsRegistration registration = (OverlayNodeSendsRegistration) e;
		System.out.printf("Registry::nodeRegistration::IP: %s, Port: %d%n", registration.getIP(), registration.getPort());
		
		
		//check if Registry is fill, and that the node is accurate
		int status = checkNode(e, socket);
		
		if (status != -1)
			nodeList.insertNode(new NodeData(registration.getIP(), registration.getPort(), status));
		
		
		//connection.
		
		System.out.println(nodeList);
		
		//respond with a message
		
	}
	
	private int checkNode(Event e, Socket socket) {
		//check that the node data matches the socket connection it came through
		
		//check that there's open space for it
		
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
		
	}

	//TODO: may need to synchronize this, as its setters and getters may be accessed simultaneously
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

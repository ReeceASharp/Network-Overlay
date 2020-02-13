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
	static final Random rng = new Random(); //ID # generator
	
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
	public void onEvent(Event e) {
		switch(e.getType()) {
			case (Protocol.OVERLAY_NODE_SENDS_REGISTRATION):
				try {
					nodeRegistration(e);
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
	
	public void onCommand(String command) {
		System.out.println("Registry::onCommand");
	}
	
	public EventFactory getFactory() {
		return factory;
	}
	
	//node wants to register with the registry
	private void nodeRegistration(Event e) throws IOException {
		System.out.println("Registry::nodeRegistration");
		OverlayNodeSendsRegistration registration = (OverlayNodeSendsRegistration) e;
		System.out.printf("IP: %s, Port: %d%n", registration.getIP(), registration.getPort());
		
		Socket connection = new Socket(registration.getIP(), registration.getPort());
		
		nodeList.insertNode(new NodeData(registration.getIP(), registration.getPort(), 43));
		
		//connection.

		
		//respond with a message
		
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

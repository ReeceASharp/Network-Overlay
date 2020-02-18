package cs455.overlay.node;

import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPReceiverThread;
import cs455.overlay.transport.TCPSenderThread;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.RegistryReportsDeregistrationStatus;
import cs455.overlay.wireformats.RegistryReportsRegistrationStatus;
import cs455.overlay.wireformats.RegistrySendsNodeManifest;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.OverlayNodeSendsDeregistration;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class MessagingNode implements Node {
	// int sendTracker; // # of messages sent, must be atomic
	// int receiveTracker; // # of messages received, must be atomic
	//private TCPConnectionsCache cache; //holds the socket addresses
	//private EventFactory factory;
	
	private String serverIP;
	private int serverPort;
	private int id;
	private Socket registrySocket;
	private RoutingTable table;
	private int[] knownIDs;
	
	//This may be helpful to implement so it can check the IP address? and the socket is saved?
	//private Socket socketToRegistry

	private MessagingNode() {
		//cache = new TCPConnectionsCache();
		registrySocket = null;
		id = -1;
	}
	
	
	public static void main(String[] args) throws IOException {
		String registryHost = args[0];
		int registryPort = Integer.parseInt(args[1]);
		
		InetAddress ip = InetAddress.getLocalHost();
		String host = ip.getHostName();

		System.out.printf("Host: %s, HostIP: %s%n",
				host, ip.getHostAddress());
		
		// get instance of self to pass a reference into the threads
		MessagingNode node = new MessagingNode();


		// start server to listen for incoming connections
		Thread server = new Thread(new TCPServerThread(node));
		server.start();
		
		//start the interactive client
		Thread parser = new Thread(new InteractiveCommandParser(Protocol.MESSAGING, node));
		parser.start();
		// *** init
		
		try {
		sendRegistration(node, registryHost, registryPort);
		} catch (IOException e) {
			System.out.printf("Wasn't able to connect to: %s:%d%n", registryHost, registryPort);
			server.interrupt();
			parser.interrupt();
		}
	
		//send registration to registry

		return;
	}
	
	private static boolean sendRegistration(MessagingNode node, String host, int port) throws IOException {
		//open a socket/connection with the registry
		Socket registrySocket = new Socket(host, port);
		node.setRegistrySocket(registrySocket);
		
		//construct the message, and get the bytes
		byte[] marshalledBytes = new OverlayNodeSendsRegistration(node.getServerIP(), node.getServerPort()).getBytes();
		
		//create a listener on this socket for the response from the Registry
		Thread receiver = new Thread(new TCPReceiverThread(registrySocket, node));
		receiver.start();

		//Send the message to the Registry to attempt registration
		node.sendMessage(registrySocket, marshalledBytes);
		
		return true;
	}
	
	private void sendMessage(Socket socket, byte[] marshalledBytes) throws IOException {
		new Thread(new TCPSenderThread(socket, marshalledBytes)).start();
	}
	
	
	private boolean sendDeregistration() throws IOException {
		byte[] message = new OverlayNodeSendsDeregistration(this.getServerIP(), 
				this.getServerPort(), id).getBytes();
		
		//create a thread with the Registry socket, and the message going to it
		sendMessage(registrySocket, message);

		return true;
	}
	

	@Override
	public void onEvent(Event e, Socket socket) {

		switch(e.getType()) {
		case Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS:
			registrationStatus(e);
			break;
		case Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS:
			deregistationStatus(e);
			break;
		case Protocol.REGISTRY_SENDS_NODE_MANIFEST:
			routingSetup(e);
			break;
		case Protocol.REGISTRY_REQUESTS_TASK_INITIATE:
			startPacketSending(e);
			break;
		case Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY:
			buildSummary(e);
			break;
		default:
			System.out.printf("Invalid Event type received: '%d'%n");
		}

	}


	private void buildSummary(Event e) {
		// TODO Auto-generated method stub
		//build message of type OverlayNodeReportsTrafficSummary
		System.out.println("MessagingNode::buildSummary::TODO");

	}


	private void startPacketSending(Event e) {
		System.out.println("MessagingNode::startPacketSending::TODO");
		
	}


	private void routingSetup(Event e) {
		RegistrySendsNodeManifest manifest = (RegistrySendsNodeManifest) e;
		//give the message data to the table
		table = new RoutingTable(manifest.getRoutingIPs(), manifest.getRoutingPorts(), manifest.getRoutingIDs());
		knownIDs = manifest.getKnownIDs();
		
		System.out.println(table);
		
		
	}


	private void deregistationStatus(Event e) {
		RegistryReportsDeregistrationStatus message = (RegistryReportsDeregistrationStatus) e;
		System.out.printf("DeregistrationStatus:: %s, ID: %d%n", message.getInfo(), message.getStatus());
	}


	private void registrationStatus(Event e) {
		RegistryReportsRegistrationStatus message = (RegistryReportsRegistrationStatus) e;
		
		//updates the id regardless, doesn't matter
		id = message.getStatus();
		
		System.out.println(message.getInfo());
	}

	@Override
	public String toString() {
		return String.format("MessagingNode: '%s'%n", this.getClass().toString());
	}


	@Override
	public void onCommand(String[] command) {
	
		switch (command[0].toLowerCase()) {
		case "print-counters-and-diagnostics":
			System.out.println("PRINTING");
			//TODO
			break;
		case "exit-overlay":
			try {
				sendDeregistration();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default:
			System.out.println("Should never reach this");
		}
		
	}


	/*
	 * Necessary for the ability to send the registration to the Registry, because this
	 * is done dynamically in the thread, it needs to be passed back up through the node ref
	 */
	@Override
	public void updateServerInfo(String ip, int port) {
		serverIP = ip;
		serverPort = port;
	}
	
	//TODO: may need to be synchronized, but because of the order this may not need to happen
	@Override
	public String getServerIP() {
		return serverIP;
	}
	
	@Override
	public int getServerPort() {
		return serverPort;
	}
	
	public Socket getRegistrySocket() {
		return registrySocket;
	}
	public void setRegistrySocket(Socket socket) {
		registrySocket = socket;
	}


	@Override
	public void exit() {
		// TODO Auto-generated method stub
		
	}
}

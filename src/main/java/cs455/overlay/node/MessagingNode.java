package cs455.overlay.node;

import cs455.overlay.transport.TCPConnectionsCache;
import cs455.overlay.transport.TCPReceiverThread;
import cs455.overlay.transport.TCPSenderThread;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.RegistryReportsRegistrationStatus;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.OverlayNodeSendsDeregistration;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class MessagingNode implements Node {
	// int sendTracker; // # of messages sent
	// int receiveTracker; // # of messages received
	private TCPConnectionsCache cache; //holds the socket addresses
	//private EventFactory factory;
	
	private String serverIP;
	private int serverPort;
	private int id;
	private Socket registrySocket;
	
	//This may be helpful to implement so it can check the IP address? and the socket is saved?
	//private Socket socketToRegistry

	private MessagingNode() {
		cache = new TCPConnectionsCache();
		registrySocket = null;
	}
	
	
	public static void main(String[] args) throws IOException {
		//TODO: get the host parameter from the arguments, along with the port
		String registryHost = args[0];
		int registryPort = Integer.parseInt(args[1]);
		
		
		
		InetAddress ip = InetAddress.getLocalHost();
		String host = ip.getHostName();

		//System.out.println("Host: " + registryHost + ", Port: " + registryPort + ", IP: " + ip.getAddress());
		
		System.out.printf("Host: %s, IPA: %s, HostIP: %s%n",
				host, ip.getAddress(), ip.getHostAddress());
		
		// *** init
		// get instance of self to pass a reference into the threads
		MessagingNode node = new MessagingNode();

		

		// start server to listen for incoming connections
		Thread server = new Thread(new TCPServerThread(node));
		server.start();
		
		//start the interactive client
		Thread parser = new Thread(new InteractiveCommandParser(Protocol.MESSAGING, node));
		parser.start();
		// *** init
		
		
		
		//send registration to registry
		if (!sendRegistration(node, registryHost, registryPort))
			return;

		return;
	}
	
	private static boolean sendRegistration(Node node, String host, int port) throws IOException {
		// Attempting to open a connection with the Registry
		Socket socketToRegistry = null;
		//open a socket/connection with the registry
		
		//System.out.println("InetAddress.getLocalHost(): " + InetAddress.getLoopbackAddress());

		//socketToRegistry = new Socket(host, port, InetAddress.getLoopbackAddress(), node.getServerPort());
		//get ip of host
		InetAddress ip = InetAddress.getByName(host);
		byte[] ipA = ip.getAddress();
		System.out.printf("HOST IP address: %s, Inet: %s%n", ipA, ip);
		
		socketToRegistry = new Socket(host, port);
		
		//System.out.println("MessagingNode::SendRegistration::Successful Connection opened");
		//System.out.printf("MessagingNode::SendRegistration::%s, '%s'%n", socketToRegistry, node.getServerIP());
		//construct the message, and get the bytes
		byte[] marshalledBytes = new OverlayNodeSendsRegistration(node.getServerIP(), node.getServerPort()).getBytes();
		
		//create a listener on this socket for the response from the Registry
		Thread receiver = new Thread(new TCPReceiverThread(socketToRegistry, node));
		receiver.start();

		//Send the message to the Registry to attempt registration
		Thread sender = new Thread(new TCPSenderThread(socketToRegistry, marshalledBytes));
		sender.start();
		


		return true;
	}
	
	private void sendMessage() {
		System.out.println("MessagingNode::SendMessage::Successful Connection opened");
	}
	
	
	private boolean sendDeregistration(Node node, String host, int port) throws IOException {
		
		//pull registry from cache
		//Socket connection = new Socket(host, port);
		//System.out.println("MessagingNode::SendDeRegistration::Successful Connection opened");
		byte[] message = new OverlayNodeSendsDeregistration(node.getServerIP(), node.getServerPort()).getBytes();
		
		
		//create a thread with the Registry socket, and the message going to it
		Thread sender = new Thread(new TCPSenderThread(registrySocket, message));
		sender.start();

		return true;
		
	}
	

	@Override
	public void onEvent(Event e, Socket socket) {
		//System.out.println("MessagingNode::onEvent:: TODO");
		
		switch(e.getType()) {
		case Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS:
			registrationStatus(e);
			break;
		case Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS:
			break;
		case Protocol.REGISTRY_SENDS_NODE_MANIFEST:
			break;
		case Protocol.REGISTRY_REQUESTS_TASK_INITIATE:
			break;
		case Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY:
			break;
		default:
			System.out.printf("Invalid Event type received: '%d'%n");
		}

	}



	private void registrationStatus(Event e) {
		//cast to the correct message type
		RegistryReportsRegistrationStatus r = (RegistryReportsRegistrationStatus) e;
		System.out.printf("RegistrationStatus::Message: %s, ID: %d%n", r.getInfo(), id);
		
		//if the status is valid (> -1), then save the ID
		int status = r.getStatus();
		if (status > -1) {
			id = status;
			return;
		}	
		System.out.println(r.getInfo());

		
	}

	@Override
	public String toString() {
		return String.format("MessagingNode: '%s'%n", this.getClass().toString());
	}


	@Override
	public void onCommand(String[] command) {
		System.out.printf("MessagingNode::onCommand:: '%s'%n", command.toString());
		
		switch (command[0]) {
		case "print-counter-and-diagnostics":
			//TODO
			break;
		case "exit-overlay":
			sendDeregistration();
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
	

}

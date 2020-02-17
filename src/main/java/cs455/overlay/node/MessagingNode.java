package cs455.overlay.node;

import cs455.overlay.transport.TCPConnectionsCache;
import cs455.overlay.transport.TCPReceiverThread;
import cs455.overlay.transport.TCPSenderThread;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.wireformats.Protocol;
<<<<<<< HEAD
=======
import cs455.overlay.wireformats.RegistryReportsDeregistrationStatus;
>>>>>>> branch 'master' of https://github.com/ReeceASharp/cs455_a1
import cs455.overlay.wireformats.RegistryReportsRegistrationStatus;
import cs455.overlay.wireformats.Event;
<<<<<<< HEAD
import cs455.overlay.wireformats.EventFactory;
=======
import cs455.overlay.wireformats.OverlayNodeSendsDeregistration;
>>>>>>> branch 'master' of https://github.com/ReeceASharp/cs455_a1
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class MessagingNode implements Node {
	// int sendTracker; // # of messages sent
	// int receiveTracker; // # of messages received
<<<<<<< HEAD
	TCPConnectionsCache cache; //holds the socket addresses
	private EventFactory factory;
=======
	private TCPConnectionsCache cache; //holds the socket addresses
	//private EventFactory factory;
>>>>>>> branch 'master' of https://github.com/ReeceASharp/cs455_a1
	
<<<<<<< HEAD
	private byte[] serverIP;
	private int serverPort;
	private int id;
=======
	private String serverIP;
	private int serverPort;
	private int id;
	private Socket registrySocket;
>>>>>>> branch 'master' of https://github.com/ReeceASharp/cs455_a1
	
	//This may be helpful to implement so it can check the IP address? and the socket is saved?
	//private Socket socketToRegistry

	private MessagingNode() {
<<<<<<< HEAD
		factory = EventFactory.getInstance();
		cache = new TCPConnectionsCache();
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
		Thread parser = new Thread(new InteractiveCommandParser(Protocol.REGISTRY, node));
		parser.start();
		// *** init
		
		
		
		//send registration to registry
		if (!sendRegistration(node, registryHost, registryPort))
			return;

		return;
=======
		cache = new TCPConnectionsCache();
		registrySocket = null;
>>>>>>> branch 'master' of https://github.com/ReeceASharp/cs455_a1
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
		
		System.out.println("MessagingNode::SendRegistration::Successful Connection opened");
		System.out.printf("MessagingNode::SendRegistration::%s, '%s'%n", socketToRegistry, node.getServerIP());
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
		
		sendRegistration(node, registryHost, registryPort);
		
		//send registration to registry

		return;
	}
	
<<<<<<< HEAD
	private boolean sendDeregistration(Node node, String host, int port) throws IOException {
		
		Socket connection = new Socket(host, port);
		System.out.println("MessagingNode::SendDeRegistration::Successful Connection opened");
		byte[] message = new OverlayNodeSendsRegistration(node.getServerIP(), node.getServerPort()).getBytes();
		

		
		//create a thread with the Registry socket, and the message going to it
		Thread sender = new Thread(new TCPSenderThread(connection, message));
=======
	private static boolean sendRegistration(MessagingNode node, String host, int port) throws IOException {
		// Attempting to open a connection with the Registry
		//open a socket/connection with the registry
		
		//System.out.println("InetAddress.getLocalHost(): " + InetAddress.getLoopbackAddress());

		//socketToRegistry = new Socket(host, port, InetAddress.getLoopbackAddress(), node.getServerPort());
		//get ip of host
		//InetAddress ip = InetAddress.getByName(host);
		//byte[] ipA = ip.getAddress();
		//System.out.printf("HOST IP address: %s, Inet: %s%n", ipA, ip);
		Socket registrySocket = new Socket(host, port);
		node.setRegistrySocket(registrySocket);
		
		//System.out.println("MessagingNode::SendRegistration::Successful Connection opened");
		//System.out.printf("MessagingNode::SendRegistration::%s, '%s'%n", socketToRegistry, node.getServerIP());
		//construct the message, and get the bytes
		byte[] marshalledBytes = new OverlayNodeSendsRegistration(node.getServerIP(), node.getServerPort()).getBytes();
		
		//create a listener on this socket for the response from the Registry
		Thread receiver = new Thread(new TCPReceiverThread(registrySocket, node));
		receiver.start();

		//Send the message to the Registry to attempt registration
		Thread sender = new Thread(new TCPSenderThread(registrySocket, marshalledBytes));
		sender.start();
		


		return true;
	}
	
	private void sendMessage() {
		System.out.println("MessagingNode::SendMessage::Successful Connection opened");
	}
	
	
	private boolean sendDeregistration() throws IOException {
		
		//pull registry from cache
		//Socket connection = new Socket(host, port);
		//System.out.println("MessagingNode::SendDeRegistration::Successful Connection opened");
		byte[] message = new OverlayNodeSendsDeregistration(this.getServerIP(), this.getServerPort()).getBytes();
		
		
		//create a thread with the Registry socket, and the message going to it
		
		
		Thread sender = new Thread(new TCPSenderThread(registrySocket, message));
>>>>>>> branch 'master' of https://github.com/ReeceASharp/cs455_a1
		sender.start();

		return true;
		
	}
	

	@Override
	public void onEvent(Event e, Socket socket) {
<<<<<<< HEAD
		System.out.println("MessagingNode::onEvent:: TODO");
		
		switch(e.getType()) {
		case Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS:
			registrationStatus(e);
			break;
		case Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS:
=======
		//System.out.println("MessagingNode::onEvent:: TODO");
		
		switch(e.getType()) {
		case Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS:
			registrationStatus(e);
			break;
		case Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS:
			deregistationStatus(e);
>>>>>>> branch 'master' of https://github.com/ReeceASharp/cs455_a1
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


<<<<<<< HEAD

	private void registrationStatus(Event e) {
		System.out.println("RegistrationStatus");
		
		RegistryReportsRegistrationStatus r = (RegistryReportsRegistrationStatus) e;
		
		int status = r.getStatus();
		//if (status < 0)
		//	return;
		id = status;
		
		System.out.printf("Message: %s, ID: %d%n", r.getInfo(), id);
		
	}

	@Override
	public String toString() {
		return String.format("MessagingNode: '%s'%n", this.getClass().toString());
	}

	@Override
	public EventFactory getFactory() {
		return factory;
	}

	@Override
	public void onCommand(String[] command) {
		System.out.printf("MessagingNode::onCommand:: '%s'%n", command.toString());
		
		switch (command[0]) {
		case "print-counter-and-diagnostics":
			//TODO
			break;
		case "exit-overlay":
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
	public void updateServerInfo(byte[] ip, int port) {
		serverIP = ip;
		serverPort = port;
	}
	
	//TODO: may need to be synchronized, but because of the order this may not need to happen
	@Override
	public byte[] getServerIP() {
		return serverIP;
	}
	
	@Override
	public int getServerPort() {
		return serverPort;
	}
	
=======
	private void deregistationStatus(Event e) {
		// TODO Auto-generated method stub
		RegistryReportsDeregistrationStatus message = (RegistryReportsDeregistrationStatus) e;
		System.out.printf("RegistrationStatus::Message: %s, ID: %d%n", message.getInfo(), id);
	}


	private void registrationStatus(Event e) {
		//cast to the correct message type
		RegistryReportsRegistrationStatus message = (RegistryReportsRegistrationStatus) e;
		System.out.printf("RegistrationStatus::Message: %s, ID: %d%n", message.getInfo(), message.getStatus());
		
		//if the status is valid (> -1), then save the ID
		int status = message.getStatus();
		if (status > -1) {
			id = status;
			return;
		}	
		System.out.println(message.getInfo());
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
>>>>>>> branch 'master' of https://github.com/ReeceASharp/cs455_a1

}

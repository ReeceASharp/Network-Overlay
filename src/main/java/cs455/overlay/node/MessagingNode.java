package cs455.overlay.node;

import cs455.overlay.transport.TCPSenderThread;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;

public class MessagingNode implements Node {
	// int sendTracker; // # of messages sent
	// int receiveTracker; // # of messages received
	// TCPConnectionsCache cache; //holds the socket addresses
	private EventFactory factory;
	
	private String serverIP;
	private int serverPort;

	private MessagingNode() {
		factory = EventFactory.getInstance();
	}

	public static void main(String[] args) throws IOException {
		//TODO: get the host parameter from the arguments, along with the port
		

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
		if (!sendRegistration(node, "localhost", Integer.parseInt(args[0])))
			return;
		
		
		
		
		
		
		
		
		
		
		
		
		/*
		Event e = factory.createEvent(message);
		System.out.println("Event Factory Created '" + e.getType() + "'");

		System.out.println("Sending Data");
		// get message
		// byte[] message = new String("Hello World!").getBytes();

		Integer messageLength = message.length;

		outputStream.writeInt(messageLength);
		outputStream.write(message, 0, messageLength);
		*/


		// deregistering the node, or exiting it

		// close the streams
		
		/*
		 * System.out.println("Closing Streams and socket to Registry now"); try {
		 * Thread.sleep(2000); } catch (InterruptedException e) { // TODO Auto-generated
		 * catch block e.printStackTrace(); }
		 * 
		 * outputStream.close(); socketToRegistry.close();
		 */

		// B. Assign random identifiers (between 0-127) to nodes within the system; the
		// registry also has to
		// ensure that two nodes are not assigned the same IDs i.e., there should be no
		// collisions in the
		// ID space.

		// check for space
		/*
		 * boolean valid = true; do {
		 * 
		 * 
		 * 
		 * }while (!valid);
		 */

		// C. Allows messaging nodes to deregister themselves. This is performed when a
		// messaging node
		// leaves the overlay.
		// D. Enables the construction of the overlay by populating the routing table at
		// the messaging nodes.
		// The routing table dictates the connections that a messaging node initiates
		// with other messaging
		// nodes in the system.

		return;
	}
	
	private static boolean sendRegistration(Node node, String host, int port) throws IOException {
		// Attempting to open a connection with the Registry
		Socket socketToRegistry = null;
		//open a socket/connection with the registry
		socketToRegistry = new Socket(host, port);
		//failed to open the socket
		//System.out.println("MessagingNode::main::creating_the_server_socket:: " + e);
		System.out.println("Successful Connection opened");

		//construct the message, and get the bytes
		byte[] message = new OverlayNodeSendsRegistration(node.getServerIP(), node.getServerPort()).getBytes();
		
		//create a thread with the Registry socket, and the message going to it
		Thread sender = new Thread(new TCPSenderThread(socketToRegistry, message));
		sender.start();

		return true;
	}
	
	
	private void sendDeregistration() {
		
	}
	
	private void packageMessage(byte[] message) {
		
	}

	@Override
	public void onEvent(Event e) {
		// TODO Auto-generated method stub
		System.out.println("MessagingNode::onEvent:: TODO");
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
	public void onCommand(String command) {
		
		
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

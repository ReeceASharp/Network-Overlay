package cs455.overlay.node;

import cs455.overlay.transport.TCPSenderThread;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;

public class MessagingNode implements Node {
	// int sendTracker; // # of messages sent
	// int receiveTracker; // # of messages received
	// TCPConnectionsCache cache; //holds the socket addresses
	private EventFactory factory;

	private MessagingNode() {
		factory = EventFactory.getInstance();
	}

	public static void main(String[] args) throws IOException {

		// get instance of self to pass a reference into the threads
		MessagingNode node = new MessagingNode();
		System.out.println(node);

		// start server to listen for incoming connections
		Thread server = new Thread(new TCPServerThread(node));
		server.start();
		
		//start the interactive client
		Thread parser = new Thread(new InteractiveCommandParser(Protocol.REGISTRY, node));
		parser.start();
		
		if (!sendRegistration("localhost", Integer.parseInt(args[0])) {
			
			return;
		}
		
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
	
	private static boolean sendRegistration(String host, int port) throws IOException {
		// Attempting to open a connection with the Registry
		Socket socketToRegistry = null;
		//open a socket/connection with the registry
		socketToRegistry = new Socket(host, port);
		//failed to open the socket
		//System.out.println("MessagingNode::main::creating_the_server_socket:: " + e);
		System.out.println("Successful Connection opened");


		// Send the registry a registration notice
		//EventFactory factory = EventFactory.getInstance();

		int type = Protocol.OVERLAY_NODE_SENDS_REGISTRATION;
		byte[] message = null;
		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(byteOutStream));
		try {
			dout.writeInt(type);
			dout.flush();
			message = byteOutStream.toByteArray();
			byteOutStream.close();
			dout.close();
		} catch (IOException e) {
			//failed for some reason
			e.printStackTrace();
		}
		Thread sender;
		try {
			sender = new Thread(new TCPSenderThread(socketToRegistry, message));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

}

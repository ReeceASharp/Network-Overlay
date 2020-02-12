package cs455.overlay.node;

import cs455.overlay.transport.TCPSenderThread;
import cs455.overlay.transport.TCPServerThread;
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
	// static final Random rng = new Random(); //ID # generator
	private EventFactory factory;

	private MessagingNode() {
		factory = EventFactory.getInstance();
	}

	public static void main(String[] args) throws IOException {

		// get instance of self to pass a reference into the threads
		MessagingNode self = new MessagingNode();
		System.out.println(self);

		// start server to listen for incoming connections
		//Thread server = new Thread(new TCPServerThread(self));
		//server.start();

		// Attempting to open a connection with the Registry
		Socket socketToRegistry = null;
		DataOutputStream outputStream = null;
		try {
			socketToRegistry = new Socket("localhost", Integer.parseInt(args[0]));
			outputStream = new DataOutputStream(socketToRegistry.getOutputStream());
		} catch (IOException e) {
			System.out.println("MessagingNode::main::creating_the_server_socket:: " + e);
		}
		System.out.println("Successful Connection opened");
		

		// Send the registry a registration notice
		EventFactory factory = EventFactory.getInstance();
		int type = Protocol.OVERLAY_NODE_SENDS_REGISTRATION;
		byte[] message = null;
		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(byteOutStream));
		dout.writeInt(type);
		dout.flush();
		message = byteOutStream.toByteArray();
		byteOutStream.close();
		dout.close();
		
		
		Thread sender = new Thread(new TCPSenderThread(socketToRegistry, message));
		sender.start();
		
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
		
		System.out.println("Closing Streams and socket to Registry now");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		outputStream.close();
		socketToRegistry.close();
		//server.interrupt();

		// Thread receiver = new Thread(TCPServerThread());

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

	@Override
	public void onEvent(Event e) {
		// TODO Auto-generated method stub
		System.out.println("MessagingNode::onEvent::");
	}

	@Override
	public String toString() {
		return String.format("MessagingNode: '%s'%n", this.getClass().toString());
	}

	@Override
	public EventFactory getFactory() {
		return factory;
	}

}

package cs455.overlay.node;

import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPConnectionsCache;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.Event;
import java.io.*;
import java.util.Random;

public class MessagingNode implements Node {
	int sendTracker;		// # of messages sent
	int receiveTracker;		// # of messages received
	TCPConnectionsCache cache;	//holds the socket addresses
	static final Random rng = new Random();	//ID # generator
	
	static final int MAX_SIZE = 128;

	public static void main(String[] args) throws IOException {
		//A. Allows messaging nodes to register themselves. This is performed when a messaging node starts
		//up for the first time.
		System.out.printf("# of args: %d\n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("'%s' ", args[i]);
		}
		
		if (args.length != 1) {
			
			System.out.println("Port parameter not specified Ex: '45650'");
			return;
		}
		
		Thread server = new Thread(new TCPServerThread());
		server.start();
		
		
		//Thread receiver = new Thread(TCPServerThread());
		
		//B. Assign random identifiers (between 0-127) to nodes within the system; the registry also has to
		//ensure that two nodes are not assigned the same IDs i.e., there should be no collisions in the
		//ID space.
		
		//check for space
		boolean valid = true;
		do {
			
			
			
		}while (!valid);
		
		//C. Allows messaging nodes to deregister themselves. This is performed when a messaging node
		//leaves the overlay.
		//D. Enables the construction of the overlay by populating the routing table at the messaging nodes.
		//The routing table dictates the connections that a messaging node initiates with other messaging
		//nodes in the system. 
		
		return;
	}
	
	
	
	

	@Override
	public void onEvent(Event e) {
		// TODO Auto-generated method stub

	}

}

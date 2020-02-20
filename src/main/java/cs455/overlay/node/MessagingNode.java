package cs455.overlay.node;

import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPReceiverThread;
import cs455.overlay.transport.TCPSenderThread;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.wireformats.*;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


public class MessagingNode implements Node {
	volatile AtomicInteger packetsSent; 		// # of data messages sent, must be atomic
	volatile AtomicInteger packetsRelayed;		// # of data messages relayed
	volatile AtomicInteger packetsReceived; 	// # of data messages received, must be atomic
	volatile AtomicLong sentSum;				// adding up the payloads being sent
	volatile AtomicLong receivedSum;			// adding up the payloads being received

	static final Random rng = new Random(); // node generator
	private String serverIP;				// IP of serverSocket
	private int serverPort;					// Port of serverSocket
	private int id;							// Node ID inside Registry
	private Socket registrySocket;			// connection to registry
	private RoutingTable table;				// table of other MessagingNodes that can be sent to
	private int[] knownIDs;					// other known MessagingNodes

	private MessagingNode() {
		registrySocket = null;
		id = -1;

		packetsSent = new AtomicInteger();
		packetsRelayed = new AtomicInteger();
		packetsReceived = new AtomicInteger();

		sentSum = new AtomicLong();
		receivedSum = new AtomicLong();	
	}


	public static void main(String[] args) throws IOException {
		String registryHost = args[0];
		int registryPort = Integer.parseInt(args[1]);

		InetAddress ip = InetAddress.getLocalHost();
		String host = ip.getHostName();

		System.out.printf("Host: %s, HostIP: %s, ", host, ip.getHostAddress());

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
			System.exit(0);
		}

		return;
	}

	//Node wants to register with the Registry, throw a message at it to check 
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

	//send the bytes through a specific connection via thread
	private void sendMessage(Socket socket, byte[] marshalledBytes) throws IOException {
		new Thread(new TCPSenderThread(socket, marshalledBytes)).start();
	}


	//Node wants to deregister from the Registered list in the Register, throw a message at it to see if it's possible
	private boolean sendDeregistration() throws IOException {
		byte[] message = new OverlayNodeSendsDeregistration(this.getServerIP(), 
				this.getServerPort(), id).getBytes();

		//create a thread with the Registry socket, and the message going to it
		sendMessage(registrySocket, message);

		return true;
	}

	//handling the Protocol types passed via packages
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
		case Protocol.OVERLAY_NODE_SENDS_DATA:
			dataPacketProcess(e);
			break;
		default:
			System.out.printf("Invalid Event type received: '%d'%n");
		}

	}

	//package results to send back to the Registry for display
	private void buildSummary(Event e) {
		//inside nodeData set whether all of the data has been received by the nodes before attempting to print out values
		byte[] marshalledBytes = new OverlayNodeReportsTrafficSummary(id, packetsSent.get(), 
				packetsRelayed.get(), sentSum.get(), packetsReceived.get(), receivedSum.get()).getBytes();

		System.out.println("Sending results back to Registry");
		try {
			sendMessage(registrySocket, marshalledBytes);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	//Received the go-ahead from the Registry to begin sending off
	private void startPacketSending(Event e) {
		RegistryRequestsTaskInitiate init = (RegistryRequestsTaskInitiate) e;
		System.out.println("Starting Task...");
		
		//reset counters
		resetCounters();
		
		int maxSending = init.getPacketsToSend();

		for (int i = 0; i < maxSending; i++) {
			int destinationID = getRandomKnownNode();
			int payload = rng.nextInt();
			Event temp = (Event) new OverlayNodeSendsData(destinationID, id, payload, 0, new int[] {});

			//increment sent
			packetsSent.incrementAndGet();
			//add up payload being sent
			sentSum.addAndGet(payload);

			//find socket of closest packet
			int index = table.contains(destinationID);
			Socket socket;
			//node is in table, send directly
			if (index > -1) {
				socket = table.get(index).getEntrySocket();
			}
			else {
				index = findClosestIDIndex(destinationID);

				socket = table.get(index).getEntrySocket();
			}

			//send it off, don't use dataPacketProcess as that will incorrectly increment the relay
			byte[] marshalledBytes = temp.getBytes();
			try {
				sendMessage(socket, marshalledBytes);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		//send task completion to Registry
		System.out.printf("Finished Task. Sent: %d, Total: %d%n", packetsSent.get(), sentSum.get());

		byte[] marshalledBytes = new OverlayNodeReportsTaskFinished(serverIP, serverPort, id).getBytes();

		try {
			sendMessage(registrySocket, marshalledBytes);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}
	
	//used to reset counters in between runs, allows for multiple runs on the same overlay
	private void resetCounters() {
		packetsSent.set(0);
		packetsRelayed.set(0);	
		packetsReceived.set(0); 
		sentSum.set(0);	
		receivedSum.set(0);
	}

	//Packet processing, handles the relay and collection of data messages from other MessagingNodes
	private void dataPacketProcess(Event temp) {
		OverlayNodeSendsData data = (OverlayNodeSendsData) temp;
		//check if the node goes here, otherwise relay
		if (data.getDestinationID() == id) {

			//add payload to running total
			receivedSum.addAndGet(data.getPayload());

			//increment received
			packetsReceived.incrementAndGet();

		}
		else {
			//find node in routing list, or send it somewhere else
			int index = table.contains(data.getDestinationID());
			if (index == -1) {
				index = findClosestIDIndex(data.getDestinationID());
			}
			Socket socket = table.get(index).getEntrySocket();
			//increment relay
			packetsRelayed.incrementAndGet();

			//increment visited #
			int visitedTotal = data.getNodesVisited() + 1;
			//append visited
			int[] visited = data.getVisitedList();
			if (visited.length > 0) {
				visited  = Arrays.copyOf(visited, visited.length + 1);
				visited[visited.length -1] = id;
			}
			else {
				visited = new int[]{id};
			}

			if (visitedTotal != visited.length) {
				return;
			}
			else {
				byte[] marshalledBytes = new OverlayNodeSendsData(data.getDestinationID(), data.getSourceID(), data.getPayload(),
						visitedTotal, visited).getBytes();

				try {
					sendMessage(socket, marshalledBytes);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	//setting up connections from the list of information received from the Registry
	private void routingSetup(Event e) {
		RegistrySendsNodeManifest manifest = (RegistrySendsNodeManifest) e;
		//give the message data to the table, will setup the connections as well
		table = new RoutingTable(manifest.getNodes());
		knownIDs = manifest.getKnownIDs();

		System.out.println("Received Overlay Routing Table from the Registry");

		//open and save the connections
		table.openConnections();

		byte[] marshalledBytes = new NodeReportsOverlaySetupStatus(id, String.format("Success: Node #%d finished setup", id)).getBytes();


		try {
			sendMessage(registrySocket, marshalledBytes);
		} catch (IOException e1) {
			System.out.println("Failed to Send Message");
			e1.printStackTrace();
		}

	}

	//When the node received a message back from the Registry about deregistration
	private void deregistationStatus(Event e) {
		RegistryReportsDeregistrationStatus message = (RegistryReportsDeregistrationStatus) e;

		//register handles message, simply output what the result is
		System.out.printf("%s ID: %d%n", message.getInfo(), message.getStatus());


		//After receiving a deregistration acknowledgement, either the it was successful, or it wasn't
		//either way, the MessagingNode has no way of connecting again, so exit
		exit();
	}

	//When the node received a message back from the Registry about registration
	private void registrationStatus(Event e) {
		RegistryReportsRegistrationStatus message = (RegistryReportsRegistrationStatus) e;

		//updates the id, it'll either stay at the initialized value of -1, or update to a valid ID
		id = message.getStatus();

		//print out message
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
			printResults();
			break;
		case "exit-overlay":
			try {
				sendDeregistration();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		default:
			System.out.println("Error. Invalid command received");
		}

	}

	//find a node in the knownlist that isn't itself
	private int getRandomKnownNode() {
		int index = -1;
		int knownSize = knownIDs.length;	//save to streamline queries

		boolean valid = false;
		while (!valid) {
			index = rng.nextInt(knownSize);
			if (knownIDs[index] != id)
				valid = true;
		}

		return knownIDs[index];
	}

	//print saved results from a run
	public void printResults() {
		System.out.println("Results");
		System.out.println("********");
		
		System.out.printf(  
				"%-25s%-20d%n" +
				"%-25s%-20d%n" +
				"%-25s%-20d%n" +
				"%-25s%-20d%n" +
				"%-25s%-20d%n",
				"Packets Sent:", packetsSent.get(), 
				"Packets Relayed:", packetsRelayed.get(), 
				"Packets Received", packetsReceived.get(), 
				"Payload Sent Sum:", sentSum.get(), 
				"Payload Received Sum:", receivedSum.get());
		
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

	//algorithm to find the ID in the routing table that is closest behind the destination
	//It will never overshoot otherwise messages will bounce around
	private int findClosestIDIndex(int destinationID) {

		int closest = 128;
		int routingIndex = -1;
		int distance = -1;
		//for each ID in routing Table
		for (int i = 0; i < table.size(); i++) {

			int tempID = table.get(i).getID();

			//standardize as this is circular
			if (tempID > destinationID)
				tempID -= 128;

			//get node distance, want to be as low as possible as that maximizes distance jumped
			distance = destinationID - tempID;

			if (closest > distance) {
				closest = destinationID - tempID;
				routingIndex = i;
			}
		}
		return routingIndex;
	}


	@Override
	public void exit() {
		System.out.println("Exiting");
		try {
			registrySocket.close();
			System.exit(0);
		} catch (IOException e) {
			System.out.println("Socket already closed. Exiting");
		}
	}
}

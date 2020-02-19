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
	volatile AtomicLong sentSum;			// adding up the payloads being sent
	volatile AtomicLong receivedSum;		// adding up the payloads being received
	
	static final Random rng = new Random(); //node generator
	private String serverIP;
	private int serverPort;
	private int id;
	private Socket registrySocket;
	private RoutingTable table;
	private int[] knownIDs;
	
	
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
		case Protocol.OVERLAY_NODE_SENDS_DATA:
			dataPacketProcess(e);
			break;
		default:
			System.out.printf("Invalid Event type received: '%d'%n");
		}

	}


	private void buildSummary(Event e) {
		//build message of type OverlayNodeReportsTrafficSummary
		//inside nodeData set whether all of the data has been received by the nodes before attempting to print out values
		System.out.println("MessagingNode::buildSummary::TODO");

	}


	private void startPacketSending(Event e) {
		RegistryRequestsTaskInitiate init = (RegistryRequestsTaskInitiate) e;
		
		int maxSending = init.getPacketsToSend();
		
		for (int i = 0; i < maxSending; i++) {
			int destinationID = getRandomKnownNode();
			int payload = rng.nextInt();
			System.out.printf("Packet #%d, Dest: %d%n", i, destinationID);
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
		System.out.printf("Sent: %d, Total: %d%n", packetsSent.get(), sentSum.get());
		
		byte[] marshalledBytes = new OverlayNodeReportsTaskFinished(serverIP, serverPort, id).getBytes();
		
		try {
			System.out.println("Sending Confirmation");
			sendMessage(registrySocket, marshalledBytes);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}

	//handle the event
	private void dataPacketProcess(Event temp) {
		System.out.println("Processing Packet");
		OverlayNodeSendsData data = (OverlayNodeSendsData) temp;
		

		
		//check if the node goes here, otherwise relay
		if (data.getDestinationID() == id) {
			System.out.println("PACKET MADE IT TO DESTINATION");
			
			receivedSum.addAndGet(data.getPayload());
			
			//increment received
			packetsReceived.incrementAndGet();
			
		}
		else {
			System.out.println("ROUTING PACKET");
			//find node in routing list, or send it somewhere else
			Socket socket = null;
			int index = table.contains(data.getDestinationID());
			if (index > -1) {
				socket = table.get(index).getEntrySocket();
				System.out.println("DIRECT SOCKET: " + socket);
				
			}
			else {
				index = findClosestIDIndex(data.getDestinationID());

				socket = table.get(index).getEntrySocket();
				System.out.println("ROUTING SOCKET: " + socket);
			}
			
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
				System.out.println("NODE VISITED TOTAL:" + visitedTotal + ", Array total:" + visited.length + ", DONT MATCH");
				return;
			}
			else {
				byte[] marshalledBytes = new OverlayNodeSendsData(data.getDestinationID(), data.getSourceID(), data.getPayload(),
						visitedTotal, visited).getBytes();
			
			try {
				System.out.println("Attempting to Relay Packets");
				sendMessage(socket, marshalledBytes);
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			}
		}
		
		
		
	}

	private void routingSetup(Event e) {
		RegistrySendsNodeManifest manifest = (RegistrySendsNodeManifest) e;
		//give the message data to the table, will setup the connections as well
		table = new RoutingTable(manifest.getNodes());
		knownIDs = manifest.getKnownIDs();
		
		System.out.println(table);
		
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
				e.printStackTrace();
			}
			break;
		default:
			System.out.println("Should never reach this");
		}
		
	}
	
	public int getRandomKnownNode() {
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
		//start at this ID
		//System.out.println("Destination: " + destinationID);
		//System.out.println(table);

		int closest = 128;
		int routingIndex = -1;
		int distance = -1;
		//for each ID in routing Table
		for (int i = 0; i < table.size(); i++) {

			int tempID = table.get(i).getID();
			//System.out.printf("i: %d, id: %d, %n", i, tempID);
			
			//standardize as this is circular
			if (tempID > destinationID)
				tempID -= 128;
			
			//get node distance, want to be as low as possible as that maximizes distance jumped
			distance = destinationID - tempID;
			
			//System.out.printf("Closest: %d, Distance: %d%n", closest, distance);
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
		} catch (IOException e) {
			System.out.println("Socket already closed. Exiting");
		}
	}
}

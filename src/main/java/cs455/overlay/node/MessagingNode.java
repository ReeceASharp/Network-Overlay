package cs455.overlay.node;

import cs455.overlay.wireformats.Event;
import java.io.*;
import java.net.*;

public class MessagingNode implements Node {
	int sendTracker;		// # of messages sent
	int receiveTracker;		// # of messages received
	
	public MessagingNode() {
		//pg 9, 4.1
		this.sendTracker = 0;
		this.receiveTracker = 0;
	}
	
	static String SERVER_ADDRESS = "denver.cs.colostate.edu";
	static Integer PORT = 5001;

	public static void main(String[] args) throws IOException {
		Socket socketToTheServer = null;
		DataInputStream inputStream = null;
		DataOutputStream outputStream = null; 

		try {
			//We create the socket AND try to connect to the address and port we are running the server on
			socketToTheServer = new Socket(SERVER_ADDRESS, PORT);
			inputStream = new DataInputStream(socketToTheServer.getInputStream());
			outputStream = new DataOutputStream(socketToTheServer.getOutputStream());
		} catch(IOException e) {
			System.out.println("Client::main::creating_the_socket:: " + e);
			System.exit(1);
		}
		// We assume that if we get here we have connected to the server.
		System.out.println("Connected to the server.");

		try {
			Integer msgLength = 0;
			//Try to read an integer from our input stream. This will block if there is nothing.
			msgLength = inputStream.readInt();

			//If we got here that means there was an integer to read and we have the 
			// length of the rest of the next message.
			System.out.println("Received a message length of: " + msgLength);

			//Try to read the incoming message.
			byte[] incomingMessage = new byte[msgLength];
			inputStream.readFully(incomingMessage, 0, msgLength);

			//You could have used .read(byte[] incomingMessage), however this will read 
			// *potentially* incomingMessage.length bytes, maybe less.
			// Whereas .readFully(...) will read exactly msgLength number of bytes. 

			System.out.println("Received Message: " + incomingMessage);

			//Now, let's respond.
			byte[] msgToServer = new String("CS455").getBytes();
			Integer msgToServerLength = msgToServer.length;
			//Our self-inflicted protocol says we send the length first
			outputStream.writeInt(msgToServerLength);
			//Then we can send the message
			outputStream.write(msgToServer, 0, msgToServerLength);

			//Close streams and then sockets
			inputStream.close();
			outputStream.close();
			socketToTheServer.close();
		} catch(IOException e) {
			System.out.println("Client::main::talking_to_the_server:: " + e);
			System.exit(1);
		}
	}
	
	
	
	

	@Override
	public void onEvent(Event e) {
		// TODO Auto-generated method stub

	}

}

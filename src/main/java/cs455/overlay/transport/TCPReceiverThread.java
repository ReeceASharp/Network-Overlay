package cs455.overlay.transport;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class TCPReceiverThread implements Runnable {
	
	int sendTracker;		// # of messages sent
	int receiveTracker;		// # of messages received
	TCPConnectionsCache cache;	//holds the socket addresses
	String SERVER_ADDRESS = "localhost";
	int PORT = 0;
	
	public TCPReceiverThread(int port) {
		//pg 9, 4.1
		System.out.println("TCPReceiverThread::ctor()");
		this.sendTracker = 0;
		this.receiveTracker = 0;
		this.cache = new TCPConnectionsCache(3);
		this.PORT = port;
	}
	
	
	@Override
	public void run() {
		System.out.println("TCPReceiverThread::run::");
		
		Socket socket = null;
		DataInputStream inputStream = null; 
		boolean listening = true;
		
		try {
			inputStream = new DataInputStream(socket.getInputStream());
		} catch(IOException e) {
			System.out.println("Client::main::creating_the_socket:: " + e);
		}
		
		
		while (listening) {
			
			try {
				ServerSocket serverSocket = new ServerSocket(PORT);
				Socket clientSocket = serverSocket.accept();
				
				Integer msgLength = 0;
				msgLength = inputStream.readInt();
				System.out.println("Received a message length of: " + msgLength);
	
				byte[] incomingMessage = new byte[msgLength];
				inputStream.readFully(incomingMessage, 0, msgLength);
				
				System.out.println("Received Message: " + incomingMessage);
			} catch (SocketException se) {
				System.out.println(se.getMessage());
			} catch (IOException ioe) {
				System.out.println(ioe.getMessage()) ;
			}
		}
		
		try {
			inputStream.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}

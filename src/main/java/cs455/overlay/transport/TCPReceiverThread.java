package cs455.overlay.transport;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import cs455.overlay.node.Node;
import cs455.overlay.wireformats.Event;


public class TCPReceiverThread implements Runnable {
	
	//int sendTracker;		// # of messages sent
	//int receiveTracker;		// # of messages received
	//TCPConnectionsCache cache;	//holds the socket addresses
	
	private Socket socket;
	private DataInputStream dataIn;
	private Node node;
	
	public TCPReceiverThread(Socket socket, Node node) throws IOException {
		//pg 9, 4.1
		System.out.printf("TCPReceiverThread::ctor(), Listening on Socket: '%s', IP: %s%n", socket.toString(), node.getServerIP());
		this.socket = socket;
		dataIn = new DataInputStream(socket.getInputStream());
		this.node = node;
		
	}
	 
	
	@Override
	public void run() {
		//System.out.println("TCPReceiverThread::run::");
		int dataLength;
		
		while (socket != null) {
			try {
				//should block
				//byte messageType = dataIn.readByte();
				
				dataLength = dataIn.readInt();
				
				//System.out.println("Received a message length of: " + dataLength);
				
				byte[] incomingMessage = new byte[dataLength];
				dataIn.readFully(incomingMessage, 0, dataLength);	
				
				Event e = node.getFactory().createEvent(incomingMessage);
				
				
				//received message, decode
				node.onEvent(e, socket);
				
				//System.out.printf("Received Message: '%s'%n", new String(incomingMessage));
				
			} catch (SocketException se) {
				System.out.println("TCPReceiverThread::run::socketException: " + se.getMessage());
				break;
			} catch (IOException ioe) {
				System.out.printf("TCPReceiverThread::run::IOException: '%s' '%s'%n", ioe.toString(), ioe.getMessage());
				//TODO: at this point the thread should call the node to remove this node from its registry
				ioe.printStackTrace();
				break;
			}
		}
		
		try {
			dataIn.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}

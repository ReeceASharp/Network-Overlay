package cs455.overlay.transport;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import cs455.overlay.node.Node;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;


public class TCPReceiverThread implements Runnable {
	
	//int sendTracker;		// # of messages sent
	//int receiveTracker;		// # of messages received
	
	private Socket socket;
	private DataInputStream dataIn;
	private Node node;
	
	public TCPReceiverThread(Socket socket, Node node) throws IOException {
		//pg 9, 4.1
		//System.out.printf("TCPReceiverThread::ctor(), Listening on Socket: '%s', IP: %s%n", socket.toString(), node.getServerIP());
		this.socket = socket;
		dataIn = new DataInputStream(socket.getInputStream());
		this.node = node;
		
	}
	
	
	@Override
	public void run() {
		int dataLength = -1;
		while (socket != null) {
			try {
				//should block
				//byte messageType = dataIn.readByte();
				
				byte[] incomingMessage = null;
				dataLength = dataIn.readInt();

				//System.out.println("Received a message length of: " + dataLength + ", from " + socket);
				synchronized(socket) {
					incomingMessage = new byte[dataLength];
					dataIn.readFully(incomingMessage, 0, dataLength);
				}
				
				Event e = EventFactory.getInstance().createEvent(incomingMessage);
				
				
				//received message, decode
				node.onEvent(e, socket);
				
				//System.out.printf("Received Message: '%s'%n", new String(incomingMessage));
				
			} catch (SocketException se) {
				System.out.println("TCPReceiverThread::run::socketException: " + se.getMessage());
				break;
			} catch (IOException ioe) {
				
				System.out.println("DataLength: " + dataLength);
				System.out.println("Socket unexpectedly closed, no longer listening to: " + socket.getRemoteSocketAddress() + 
						", DataLength: " + dataLength);
				//TODO: at this point the thread should call the node to remove this data from its registry
				break;
			} catch (Exception e) {
				System.out.println("CAUGHT AN EXCEPTION REGARDING RECEIVER");
				System.out.println("Exception: '" + e + "'");
			}
		}
		try {
			dataIn.close();
		} catch (IOException e) {
			System.out.println("Attempting to close Pipes");
			e.printStackTrace();
		}
		
	}

}

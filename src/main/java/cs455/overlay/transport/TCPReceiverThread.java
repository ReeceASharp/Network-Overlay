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

				//synchronize reads from a socket to make sure it's all read in chunks
				synchronized(socket) {
					incomingMessage = new byte[dataLength];
					dataIn.readFully(incomingMessage, 0, dataLength);
				}
				
				Event e = EventFactory.getInstance().createEvent(incomingMessage);
				
				
				//received message, decode
				node.onEvent(e, socket);
				
				
			} catch (SocketException se) {
				System.out.println("TCPReceiverThread::run::socketException: " + se.getMessage());
				break;
			} catch (IOException ioe) {
				System.out.println("Connection closed, no longer listening to: " + socket.getRemoteSocketAddress());
				
				break;
			}  catch (NullPointerException ne) {
				ne.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
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

package cs455.overlay.transport;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import cs455.overlay.node.Node;


public class TCPServerThread implements Runnable {

	ServerSocket serverSocket;
	TCPConnectionsCache cache;
	Node node;
	InetAddress addr;
	int port;
	

	//private volatile boolean listening;
	public TCPServerThread(Node node) {
		this(node, 0);
	}
	
	public TCPServerThread(Node node, int port) {
		this.node = node;
		cache = new TCPConnectionsCache();
		serverSocket = null;
		this.port = port;
	}
	
	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(port);
			addr = InetAddress.getLocalHost();
		} catch(IOException e) {
			System.out.println("TCPServerThread::run::creating_the_socket:: " + e);
		}
		
		//The serverSocket doesn't have an IP associated, only a port, need to query this host for the IP it's running on
		
		
		
		//update the referenced node with the details of the serverSocket, so it can send its details to the Registry
		node.updateServerInfo(addr.getHostAddress(), serverSocket.getLocalPort());
		//System.out.printf("Address: %s, Port: %s %n", serverSocket.getInetAddress().getAddress(), serverSocket.getLocalPort());
		System.out.printf("TCPServer on %s%n", serverSocket.getLocalSocketAddress());
		//listen for new connections to this program
		//TODO: look at interrupting from above
		
		try {
			while (true) {
					//System.out.println("TCPServerThread::run::blocking");
					Socket clientSocket = serverSocket.accept();
					
					//String value = clientSocket.getLocalAddress().getHostAddress();
					//System.out.println("VALUE: " + value);
					
					//System.out.printf("Received Connection: %s%n", clientSocket.getRemoteSocketAddress());
					
					cache.saveConnection(clientSocket);
					//System.out.println(cache);
					
					//spawn a thread to handle that specific connection, 
					new Thread(new TCPReceiverThread(clientSocket, node)).start();
				} 
	
		} catch (SocketException e) {
			System.out.println("TCPServerThread::run::error::socketClosed::" + e);
		} catch (IOException e) {
			System.out.println("TCPServerThread::run::error_blocking_for_client:: " + e);
		}  
		
		
		
		
		System.out.println("TCPServerThread::run::exiting");
	}
	
	/*
	public void stop() {
		listening = false;
	}
	*/
	
	public void stopServer() throws IOException {
		serverSocket.close();
	}
	
}

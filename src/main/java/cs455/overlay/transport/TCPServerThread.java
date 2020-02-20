package cs455.overlay.transport;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import cs455.overlay.node.Node;


public class TCPServerThread implements Runnable {

	ServerSocket serverSocket;
	Node node;
	InetAddress addr;
	int port;
	

	//private volatile boolean listening;
	public TCPServerThread(Node node) {
		this(node, 0);
	}
	
	public TCPServerThread(Node node, int port) {
		this.node = node;
		serverSocket = null;
		this.port = port;
		
		try {
			serverSocket = new ServerSocket(port);
			addr = InetAddress.getLocalHost();
		} catch(IOException e) {
			System.out.println("TCPServerThread::run::creating_the_socket:: " + e);
		}
		
		// I know this is awful to do, but update the referenced node with the details of the serverSocket, 
		// so it can send its details to the Registry without it sending incomplete information
		node.updateServerInfo(addr.getHostAddress(), serverSocket.getLocalPort());
	}
	
	@Override
	public void run() {

		//System.out.printf("Address: %s, Port: %s %n", serverSocket.getInetAddress().getAddress(), serverSocket.getLocalPort());
		System.out.printf("Server listening on Port:%s%n", serverSocket.getLocalPort());
		//listen for new connections to this program
		try {
			while (true) {
					//System.out.println("TCPServerThread::run::blocking");
					Socket clientSocket = serverSocket.accept();
					
					//String value = clientSocket.getLocalAddress().getHostAddress();
					//System.out.println("VALUE: " + value);
					
					//System.out.printf("Received Connection: %s%n", clientSocket.getRemoteSocketAddress());
					
					
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

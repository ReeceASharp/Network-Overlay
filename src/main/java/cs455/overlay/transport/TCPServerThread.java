package cs455.overlay.transport;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.sun.jdi.InternalException;

import cs455.overlay.node.Node;


public class TCPServerThread implements Runnable {

	ServerSocket serverSocket;
	TCPConnectionsCache cache;
	Node node;
	//private volatile boolean listening;
	
	
	public TCPServerThread(Node node) {
		this.node = node;
		cache = new TCPConnectionsCache();
		serverSocket = null;
		//listening = true;
	}
	
	
	@Override
	public void run() {
		//System.out.println("TCPServerThread::run::");
		
		//Find an open port and allow X connections
		//the number of connections is at most log base 2 of the registry's max nodes recorded
		//this is because each connection will be 
		try {
			serverSocket = new ServerSocket(0);
			
		} catch(IOException e) {
			System.out.println("TCPServerThread::run::creating_the_socket:: " + e);
		}
		System.out.printf("TCPServer listening on IP: %s, Port: %s, Socket: %s%n", serverSocket.getInetAddress().getHostAddress(), 
				serverSocket.getLocalPort(), serverSocket.getLocalSocketAddress());
		
		//update the referenced node with the details of the serverSocket, so it can send its details to the Registry
		node.updateServerInfo(serverSocket.getInetAddress().getHostAddress(), serverSocket.getLocalPort());
		
		//listen for new connections to this program
		//TODO: look at interrupting from above
		
		try {
			while (true) {
					System.out.println("TCPServerThread::run::blocking");
					Socket clientSocket = serverSocket.accept();
					System.out.printf("Received Connection: %s, %s%n", clientSocket.getRemoteSocketAddress(), clientSocket.getInetAddress());
					cache.saveConnection(clientSocket);
					System.out.println(cache.toString());
					
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

package cs455.overlay.transport;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import cs455.overlay.node.Node;


public class TCPServerThread implements Runnable {

	ServerSocket serverSocket;
	TCPConnectionsCache cache;
	Node node;
	
	public TCPServerThread(Node node) {
		this.node = node;
		cache = new TCPConnectionsCache();
		serverSocket = null;
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
		System.out.printf("TCPServer listening on IP: %s, Port: %s, Socket: %s%n", serverSocket.getLocalSocketAddress(), 
				serverSocket.getLocalPort(), serverSocket.getLocalSocketAddress());
		/*
		try {
			//System.out.println(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/
		
		
		//update the referenced node with the details of the serverSocket, so it can send its details to the Registry
		node.updateServerInfo(serverSocket.getInetAddress().toString(), serverSocket.getLocalPort());
		
		boolean listening = true;
		//listen for new connections to this program
		
		while (listening) {
			try {
				System.out.println("Blocking");
				Socket clientSocket = serverSocket.accept();
				System.out.printf("Received Connection: %s, %s%n", clientSocket.getRemoteSocketAddress(), clientSocket.getInetAddress());
				//cache.saveConnection(clientSocket);
				System.out.println(cache.toString());
				
				//spawn a thread to handle that specific connection, 
				new Thread(new TCPReceiverThread(clientSocket, node)).start();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("TCPServerThread::run::blocking_for_client:: " + e);
			}
			
		}
	}
}

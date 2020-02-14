package cs455.overlay.transport;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
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
		try {
			serverSocket = new ServerSocket(0);
		} catch(IOException e) {
			System.out.println("TCPServerThread::run::creating_the_socket:: " + e);
		}
		System.out.printf("TCPServer on %s%n", serverSocket);
		
		//update the referenced node with the details of the serverSocket, so it can send its details to the Registry
		node.updateServerInfo(serverSocket.getInetAddress().getHostAddress(), serverSocket.getLocalPort());
		
		//listen for new connections to this program
		//TODO: look at interrupting from above
		
		try {
			while (true) {
					System.out.println("TCPServerThread::run::blocking");
					Socket clientSocket = serverSocket.accept();
					System.out.printf("Received Connection: %s%n", clientSocket);
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

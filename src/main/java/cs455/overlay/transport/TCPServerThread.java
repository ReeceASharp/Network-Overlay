package cs455.overlay.transport;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class TCPServerThread implements Runnable {
	
	Integer NUM_POSSIBLE_CONNECTIONS = 3;
	ServerSocket serverSocket;
	TCPConnectionsCache cache;
	
	public TCPServerThread() {

		cache = new TCPConnectionsCache(3);
		serverSocket = null;
	}
	

	@Override
	public void run() {
		//System.out.println("TCPServerThread::run::");

		
		try {
			serverSocket = new ServerSocket(0, NUM_POSSIBLE_CONNECTIONS);
			
		} catch(IOException e) {
			System.out.println("TCPServerThread::run::creating_the_socket:: " + e);
		}
		
		System.out.printf("TCPServer: Port=%d%n", serverSocket.getLocalPort());
		
		boolean listening = true;
		//listen for new connections to this program
		
		while (listening) {
			try {
				//System.out.println("Blocking");
				Socket clientSocket = serverSocket.accept();
				System.out.printf("Received Connection: %s, %s%n", clientSocket.getRemoteSocketAddress(), clientSocket.getInetAddress());
				cache.saveConnection(clientSocket);
				System.out.println(cache.toString());
				
				//spawn a thread to handle that specific connection, 
				Thread receiver = new Thread(new TCPReceiverThread(clientSocket));
				receiver.start();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("TCPServerThread::run::blocking_for_client:: " + e);
			}
			
		}
	}
}

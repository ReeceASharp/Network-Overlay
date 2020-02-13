package cs455.overlay.transport;

import java.net.Socket;
import java.util.ArrayList;

public class TCPConnectionsCache {
	private ArrayList<Socket> sockets;
	
	public TCPConnectionsCache(int size) {
		sockets = new ArrayList<>(size);
	}
	
	public void saveConnection(Socket socket) {
		System.out.println("TCPConnectionsCache::saveConnection");
		sockets.add(socket);
	}
	
	/*
	 * attempts to remove the socket connection from the cache
	 * returns true if successful, false if the socket wasn't in the cache
	 */
	public boolean removeConnection(Socket socket) {
		return sockets.remove(socket);
	}
	
	@Override
	public String toString() {
		return sockets.toString();
	}

}

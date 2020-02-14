package cs455.overlay.transport;

import java.net.Socket;
import java.util.ArrayList;

public class TCPConnectionsCache {
	private ArrayList<Socket> sockets;
	
	public TCPConnectionsCache() {
		sockets = new ArrayList<>();
	}
	
	public void saveConnection(Socket socket) {
		System.out.println("TCPConnectionsCache::saveConnection::" + socket.toString());
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
		StringBuilder sb = new StringBuilder();
		sb.append("TCPConnectionsCache: \n");
		sb.append(sockets.toString());
		sb.append("\n***\n");
		return sb.toString();
	}

}

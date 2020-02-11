package cs455.overlay.transport;

import java.net.Socket;
import java.util.ArrayList;

public class TCPConnectionsCache {
	public ArrayList<Socket> sockets;
	
	public TCPConnectionsCache(int size) {
		sockets = new ArrayList<>(size);
	}
	
	public void saveConnection(Socket receiver) {
		System.out.println("TCPConnectionsCache::saveConnection");
		sockets.add(receiver);
	}
	
	@Override
	public String toString() {
		return sockets.toString();
	}

}

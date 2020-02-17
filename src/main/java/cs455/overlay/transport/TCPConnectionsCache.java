package cs455.overlay.transport;

import java.net.Socket;
import java.util.ArrayList;

public class TCPConnectionsCache {
	private ArrayList<Socket> sockets;
	
	public TCPConnectionsCache() {
		sockets = new ArrayList<>();
	}
	
	public void saveConnection(Socket socket) {
		//System.out.println("TCPConnectionsCache::saveConnection::" + socket.toString());
		sockets.add(socket);
	}
	
	/*
	 * attempts to remove the socket connection from the cache
	 * returns true if successful, false if the socket wasn't in the cache
	 */
	public boolean removeConnection(Socket socket) {
		return sockets.remove(socket);
	}
	
	public Socket getSocketByIndex(int i) {
		return sockets.get(i);
	}
	
	public Socket getSocketByInet(String ip, int port) {
		int index = contains(ip, port);
		if (index > -1)
			return getSocketByIndex(index);
		return null;
		
	}
	
	//Check to see if the IP:port combination is already inside of the registry, shouldn't really happen though
	//as the port allocation on the server is dynamic
	public int contains(String ip, int port) {
		System.out.println(this.getClass().getSimpleName() + "::contains::listConnections");
		
		
		
		
		
		for (int i = 0; i < sockets.size(); i++) {
			Socket temp = sockets.get(i);
			System.out.printf("%s, %s, %s%n", temp.getInetAddress(), temp.getLocalSocketAddress(),temp);
			
			//if (sockets.get(i).getInetAddress().getHostAddress().equals(ip) && sockets.get(i).getPort() == port)
				//return i;
		}		
		return -1;
		
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("TCPConnectionsCache: \n");
		sb.append(sockets.toString());
		sb.append("\n***");
		return sb.toString();
	}

}

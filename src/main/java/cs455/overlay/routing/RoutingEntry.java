package cs455.overlay.routing;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class RoutingEntry {
	String ip;		//IP associated with the node's server
	int port;		//port associated with the node's server
	int id;			//ID issued by the Registry
	Socket entrySocket;	//the socket that points to this entry
	
	public RoutingEntry(String ip, int port, int id) {
		this.ip = ip;
		this.port = port;
		this.id = id;
		entrySocket = null;
		
	}
	public String getIP() {
		return ip;
	}
	public int getPort() {
		return port;
	}
	public int getID() {
		return id;
	}
	
	public Socket getEntrySocket() {
		return entrySocket;
	}
	
	@Override
	public String toString() {
		return String.format("IP: %s, Port: %d, ID: %d", ip, port, id);
	}
	
	public void openConnections() {
		try {
			entrySocket = new Socket(ip, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}

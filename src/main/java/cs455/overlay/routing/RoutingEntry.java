package cs455.overlay.routing;

public class RoutingEntry {
	String ip;		//IP associated with the node's server
	int port;		//port associated with the node's server
	int id;			//ID issued by the Registry
	
	
	public RoutingEntry(String ip, int port, int id) {
		this.ip = ip;
		this.port = port;
		this.id = id;
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
	
}

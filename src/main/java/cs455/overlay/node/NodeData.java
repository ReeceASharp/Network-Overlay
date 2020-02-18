package cs455.overlay.node;

import java.net.Socket;

public class NodeData implements Comparable<NodeData> {
	String ip;		//IP associated with the node's server
	int port;		//port associated with the node's server
	int id;			//ID issued by the Registry
	Socket socket;
	boolean ready;	//whether this node reports its RoutingTable is received and set up	
	
	public NodeData(String ip, int port, int id, Socket socket) {
		this.ip = ip;
		this.port = port;
		this.id = id;
		this.socket = socket;
		this.ready = false;
	}
	
	@Override
	public String toString() {
		return String.format("IP: %s, Port: %d, ID: %d", ip, port, id);
	}

	
	/*
	 * This will be used to organize the arraylist of NodeData into ascending order
	 * This will allow Collections.sort to accurately organize them and then generation
	 * of the tables will be easier as it'll simply be based on generating based off index
	 */
	@Override
	public int compareTo(NodeData otherNode) {
		return (this.getID() < otherNode.getID() ? -1 : 
            (this.getID() == otherNode.getID() ? 0 : 1));
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

	public boolean ready() {
		return ready;
	}
	
	public void setReady() {
		ready = true;
	}

	public Socket getSocket() {
		return socket;
	}
	
}

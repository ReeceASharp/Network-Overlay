package cs455.overlay.node;

public class NodeData implements Comparable<NodeData> {
<<<<<<< HEAD
	byte[] ip;	//IP associated with the node's server
	int port;		//port associated with the node's server
	int id;			//ID issued by the Registry
	
	public NodeData(byte[] bs, int port, int id) {
		this.ip = bs;
		this.port = port;
		this.id = id;
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
	
	public byte[] getIP() {
=======
	String ip;	//IP associated with the node's server
	int port;		//port associated with the node's server
	int id;			//ID issued by the Registry
	
	public NodeData(String ip, int port, int id) {
		this.ip = ip;
		this.port = port;
		this.id = id;
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
>>>>>>> branch 'master' of https://github.com/ReeceASharp/cs455_a1
		return ip;
	}
	
	public int getPort() {
		return port;
	}
	
	public int getID() {
		return id;
	}
	
	
}

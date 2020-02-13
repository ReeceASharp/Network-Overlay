package cs455.overlay.node;

public class NodeData {
	String ip;	//IP associated with the node's server
	int port;		//port associated with the node's server
	int id;			//ID issued by the Registry
	
	public NodeData(String ip, int port, int id) {
		this.ip = ip;
		this.port = port;
		this.id = id;
	}
}

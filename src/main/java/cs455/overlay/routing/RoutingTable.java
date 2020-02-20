package cs455.overlay.routing;

import cs455.overlay.node.NodeData;
import cs455.overlay.node.NodeList;

public class RoutingTable {
	//used on the client side
	private RoutingEntry[] nodes;
	
	//only used on the server side as it is easier to send this data
	
	public RoutingTable(RoutingEntry[] nodes) {
		this.nodes = nodes;
	}
	
	
	//only used by the Registry to generate a list of nodes to pass on to each MessagingNode
	public static RoutingTable generateTable(NodeList nodeList, int listOwnerIndex, int tableSize) {
		
		int nodeListSize = nodeList.size();
		RoutingEntry[] table = new RoutingEntry[tableSize];
		
		NodeData data;
		//logic here
		for (int i = 0; i < tableSize; i++) {
			//get index based on starting location
			int index = (listOwnerIndex + (int)Math.pow(2, i)) % nodeListSize;
			data = nodeList.get(index);
	
			
			//set the array values
			table[i] = new RoutingEntry(data.getIP(), data.getPort(), data.getID());
		}
		return new RoutingTable(table);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("******* Table Size: " + nodes.length + "\n");
		
		for (int i = 0; i < nodes.length; i++) {
			sb.append(nodes[i] + "\n");
		}
		sb.append("*******");
		
		
		return sb.toString();
	}
	
	public RoutingEntry[] getNodes() {
		return nodes;
	}
	
	public void openConnections() {
		for (RoutingEntry n : nodes)
			n.openConnections();
	}
	
	public int size() {
		return nodes.length;
	}
	
	public RoutingEntry get(int i) {
		return nodes[i];
	}
	
	public int contains(int id) {
		for (int i = 0; i < nodes.length; i++)
			if (nodes[i].getID() == id)
				return i;
		return -1;
	}
}

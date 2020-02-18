package cs455.overlay.routing;

import cs455.overlay.node.NodeData;
import cs455.overlay.node.NodeList;

public class RoutingTable {
	private NodeData[] nodes;
	
	//raw arrays simplify the sending of the table from the Registry to the node
	private String[] ipList;
	private int[] portList;
	private int[] idList;
	
	public RoutingTable(String[] routingIPList, int[] routingPortList, int[] routingIDList) {
		//preset 
		nodes = new NodeData[routingIPList.length];
		//ipList = routingIPList;
		//portList = routingPortList;
		//idList = routingIDList;
		
		
		for (int i = 0; i < ipList.length; i++) {
			nodes[i] = new NodeData(routingIPList[i], routingPortList[i], routingIDList[i], null);
		}
	}

	public String[] getIpList() {
		return ipList;
	}

	public int[] getPortList() {
		return portList;
	}

	public int[] getIdList() {
		return idList;
	}
	
	
	//only used by the Registry to generate a list of nodes to pass on to each MessagingNode
	public static RoutingTable generateTable(NodeList nodeList, int listOwnerIndex, int tableSize) {
		System.out.println("Generating Table for: " + nodeList.get(listOwnerIndex));
		
		int nodeListSize = nodeList.size();
		String[] ipList = new String[tableSize];
		int[] portList = new int[tableSize];
		int[] idList = new int[tableSize];
		
		NodeData data;
		//logic here
		for (int i = 0; i < tableSize; i++) {
			//get index based on starting location
			int index = (listOwnerIndex + (int)Math.pow(2, i)) % nodeListSize;
			data = nodeList.get(index);
	
			//set the array values
			ipList[i] = data.getIP();
			portList[i] = data.getPort();
			idList[i] = data.getID();
		}
		
		return new RoutingTable(ipList, portList, idList);
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
	
}

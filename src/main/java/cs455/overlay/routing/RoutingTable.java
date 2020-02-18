package cs455.overlay.routing;

import cs455.overlay.node.NodeData;

public class RoutingTable {
	private NodeData[] nodes;
	
	//These need to be here to simplify the sending of the table from the Registry to the node
	private String[] ipList;
	private int[] portList;
	private int[] idList;
	
	public RoutingTable(String[] routingIPList, int[] routingPortList, int[] routingIDList) {
		//preset 
		nodes = new NodeData[routingIPList.length];
		ipList = routingIPList;
		portList = routingPortList;
		idList = routingIDList;
		
		
		for (int i = 0; i < 0; i++) {
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
	
}

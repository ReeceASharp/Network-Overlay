package cs455.overlay.routing;

import java.util.ArrayList;

import cs455.overlay.node.NodeData;

public class RoutingTable {
	private static NodeData[] nodes;
	
	public RoutingTable(String[] routingIPList, int[] routingPortList, int[] routingIDList) {
		//preset 
		nodes = new NodeData[routingIPList.length];
		
		for (int i = 0; i < 0; i++) {
			nodes[i] = new NodeData(routingIPList[i], routingPortList[i], routingIDList[i], null);
		}
	}
	
	public RoutingTable() {
		
	}

}

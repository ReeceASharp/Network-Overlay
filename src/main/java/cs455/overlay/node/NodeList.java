package cs455.overlay.node;

import java.util.ArrayList;

public class NodeList {
	ArrayList<NodeData> nodes;
	
	public NodeList() {
		nodes = new ArrayList<NodeData>();
	}
	
	
	//TODO: Maybe need to insert boolean checking to make sure that IP+Port combination isn't already in the system
	//Additionally, the best way to search the 
	public void insertNode(NodeData data) {
		nodes.add(data);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("NodeList: *******\n");
		
		for (NodeData n : nodes) {
			sb.append(n.toString() + "\n");
		}
		return sb.toString();
	}
	
	
	
}

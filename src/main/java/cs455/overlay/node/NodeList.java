package cs455.overlay.node;

import java.util.ArrayList;
import java.util.Random;

public class NodeList {
	ArrayList<NodeData> nodes;
	
	static final Random rng = new Random(); //ID # generator
	static final int MAX_SIZE = 128;		//2^7 nodes can be inserted
	
	public NodeList() {
		nodes = new ArrayList<NodeData>();
	}
	
	
	//TODO: Maybe need to insert boolean checking to make sure that IP+Port combination isn't already in the system
	//Additionally, the best way to search the 
	public void insertNode(NodeData data) {
		System.out.println("Inserting node: " + data);
		nodes.add(data);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("NodeList: *******Size: " + nodes.size() + "\n");
		
		for (NodeData n : nodes) {
			sb.append(n.toString() + "\n");
		}
		return sb.toString();
	}
	
	public int getOpenID() {
		//TODO: Check somewhere for the max # of spots available, probably before here
		//at this point it would probably be assumed that it was open
		System.out.println(this);
		
		boolean found = true;
		int id;
		
		do {
			//generate a number
			id = rng.nextInt(128);
			
			//check the array for it
			for (NodeData nd : nodes)
				if (nd.getID() == id)
					found = false;
			
		} while (!found);
		
		return id;
	}
	
	public boolean full() {
		return nodes.size() < MAX_SIZE;
	}
	
	
	
}

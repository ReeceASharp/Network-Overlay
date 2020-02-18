package cs455.overlay.node;

import java.util.ArrayList;
import java.util.Collections;
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
	
	public void removeNode(String ip, int port) {
		int index = contains(ip, port); 
		if (index > -1)
			nodes.remove(index);
	}
	
	public void removeNode(int index) {
		nodes.remove(index);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("NodeList: *******Size: " + nodes.size() + "\n");
		
		for (NodeData n : nodes) {
			sb.append(n.toString() + "\n");
		}
		sb.append("-NodeList End");
		return sb.toString();
	}
	
	public int getOpenID() {
		//TODO: Check somewhere for the max # of spots available, probably before here
		//at this point it would probably be assumed that it was open
		//System.out.println(this);
		
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
	
	//Check to see if the IP:port combination is already inside of the registry, shouldn't really happen though
	//as the port allocation on the server is dynamic
	public int contains(String ip, int port) {
		for (int i = 0; i < nodes.size(); i++)
			if (nodes.get(i).getIP().equals(ip) && nodes.get(i).getPort() == port)
				return i;
		return -1;
		
	}
	
	public NodeData getByID(int id) {
		for (int i = 0; i < nodes.size(); i++)
			if (nodes.get(i).getID() == id)
				return nodes.get(i);
		return null;
	}
	
	public NodeData get(int i) {
		return nodes.get(i);
	}
	
	public int size() {
		return nodes.size();
	}
	
	public void sort() {
		Collections.sort(nodes);
	}
	
	public boolean readyToStart() {
		for (NodeData nd : nodes)
			if (!nd.ready())
				return false;
		return true;
	}
	
	public void setReady(int index) {
		
	}
	
	
}

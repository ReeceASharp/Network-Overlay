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
	
	public synchronized void insertNode(NodeData data) {
		System.out.println("Inserting node: " + data);
		nodes.add(data);
	}
	
	public synchronized void removeNode(String ip, int port) {
		int index = contains(ip, port); 
		if (index > -1)
			nodes.remove(index);
	}
	
	public synchronized void removeNode(int index) {
		nodes.remove(index);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("******* Size: " + nodes.size() + "\n");
		
		for (NodeData n : nodes) {
			sb.append(n.toString() + "\n");
		}
		sb.append("*******");
		return sb.toString();
	}
	
	public synchronized int getOpenID() {		
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
	public synchronized int contains(String ip, int port) {
		for (int i = 0; i < nodes.size(); i++)
			if (nodes.get(i).getIP().equals(ip) && nodes.get(i).getPort() == port)
				return i;
		return -1;
		
	}
	
	public synchronized NodeData getByID(int id) {
		for (int i = 0; i < nodes.size(); i++)
			if (nodes.get(i).getID() == id) {
				return nodes.get(i);
			}
		return null;
	}
	
	public synchronized NodeData get(int i) {
		return nodes.get(i);
	}
	
	public synchronized int size() {
		return nodes.size();
	}
	
	public synchronized void sort() {
		Collections.sort(nodes);
	}
	
	public boolean readyToStart() {
		for (NodeData nd : nodes)
			if (!nd.ready())
				return false;
		return true;
	}
	
	public synchronized void setReady(int index) {
		nodes.get(index).setReady();
	}

	public int[] generateKnownIDs() {
		int[] knownIDs = new int[nodes.size()];
		
		for (int i = 0; i < nodes.size(); i++)
			knownIDs[i] = nodes.get(i).getID();
		
		return knownIDs;
	}
	
	
	
}

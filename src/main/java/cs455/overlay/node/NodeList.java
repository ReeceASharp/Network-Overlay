package cs455.overlay.node;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class NodeList {
	private ArrayList<NodeData> nodes;
	
	private static final Random rng = new Random(); //ID # generator
	private static final int MAX_SIZE = 128;		//2^7 nodes can be inserted
	
	public NodeList() {
		nodes = new ArrayList<NodeData>();
	}
	
	public synchronized int insertNode(String ip, int port, Socket socket) {
		int index = contains(ip, port);
		int id = index;
		if (index == -1) {
			id = getOpenID();
			nodes.add(new NodeData(ip, port, id, socket));
		}
		return id;
		
	}
	
	public synchronized int removeNode(String ip, int port) {
		int index = contains(ip, port);
		if (index > -1) {
			nodes.remove(index);
		}
		return index;
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
	
	private synchronized int getOpenID() {
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
	private int contains(String ip, int port) {
		for (int i = 0; i < nodes.size(); i++)
			if (nodes.get(i).getIP().equals(ip) && nodes.get(i).getPort() == port)
				return i;
		return -1;
		
	}
	
	//return the NodeData object associated with the ID passed
	public NodeData getByID(int id) {
		for (int i = 0; i < nodes.size(); i++)
			if (nodes.get(i).getID() == id) {
				return nodes.get(i);
			}
		return null;
	}
	
	public int getIndex(int id) {
		for (int i = 0; i < nodes.size(); i++)
			if (nodes.get(i).getID() == id) {
				return i;
			}
		return -1;
	}
	
	//return the NodeData
	public NodeData get(int i) {
		return nodes.get(i);
	}
	
	
	
	public synchronized int size() {
		return nodes.size();
	}
	
	//sort by ID, needs to be synchronized so no nodes are appended out of order
	public synchronized void sort() {
		Collections.sort(nodes);
	}
	
	public boolean readyToStart() {
		for (NodeData nd : nodes)
			if (!nd.ready())
				return false;
		return true;
	}
	
	public void setReady(int index) {
		nodes.get(index).setReady();
	}
	
	
	public boolean completelyDone() {
		for (NodeData nd : nodes)
			if (!nd.done)
				return false;
		return true;
	}

	//only used by Registry (1 thread)
	public synchronized int[] generateKnownIDs() {
		int[] knownIDs = new int[nodes.size()];
		
		for (int i = 0; i < nodes.size(); i++)
			knownIDs[i] = nodes.get(i).getID();
		
		return knownIDs;
	}
	
	
	
}

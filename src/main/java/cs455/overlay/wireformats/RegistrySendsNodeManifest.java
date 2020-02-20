package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import cs455.overlay.routing.RoutingEntry;

public class RegistrySendsNodeManifest implements Event {
	public static final int type = Protocol.REGISTRY_SENDS_NODE_MANIFEST;
	
	RoutingEntry[] nodes;
	int[] knownIDs;

	public RegistrySendsNodeManifest(RoutingEntry[] nodes, int[] knownIDs) {
		this.nodes = nodes;
		this.knownIDs = knownIDs;
	}
	
	public RegistrySendsNodeManifest(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		//remove the type
		din.readInt();
		
		int size = din.readInt();
		
		
		//setup arrays
		nodes = new RoutingEntry[size];
		
		String ip;
		int port;
		int id;
		
		
		for (int i = 0; i < size; i++) {
			//read in IP
			int infoLength = din.readInt();
			byte[] infoBytes = new byte[infoLength];
			din.readFully(infoBytes);
			ip = new String(infoBytes);
			
			//read in Port
			port = din.readInt();
			
			//read in ID
			id = din.readInt();
			
			
			
			nodes[i] = new RoutingEntry(ip, port, id);
		}
		
		int registryListSize = din.readInt();
		knownIDs = new int[registryListSize];
		
		for (int i = 0; i < registryListSize; i++)
			knownIDs[i] = din.readInt();
		
			
		//close buffer
		baInputStream.close();
		din.close();
		
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public byte[] getBytes() {
		//put into a buffer
		byte[] message = null;
		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(byteOutStream));
		
		try {
			dout.writeInt(type);

			//# of elements in routing table
			dout.writeInt(nodes.length);
			
			//for each element in the tentative routing table being sent
			for (int i = 0; i < nodes.length; i++) {
				//write in IP
				
				
				byte[] infoBytes = nodes[i].getIP().getBytes();
				dout.writeInt(infoBytes.length);
				dout.write(infoBytes);
				
				//read in Port
				dout.writeInt(nodes[i].getPort()); 
				
				//read in ID
				dout.writeInt(nodes[i].getID());
			}
			
			//read in list of known IDs
			dout.writeInt(knownIDs.length);
			for (int i = 0; i < knownIDs.length; i++)
				dout.writeInt(knownIDs[i]);
			
			dout.flush();
			
			message = byteOutStream.toByteArray();
			byteOutStream.close();
			dout.close();
			
			
		} catch (IOException e) {
			//failed for some reason
			System.out.println(e);
		}
		
		return message;
	}
	
	
	public int[] getKnownIDs() {
		return knownIDs;
	}
	
	public RoutingEntry[] getNodes() {
		return nodes;
	}

}



























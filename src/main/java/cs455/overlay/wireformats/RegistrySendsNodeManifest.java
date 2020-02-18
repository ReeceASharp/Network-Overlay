package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RegistrySendsNodeManifest implements Event {
	public static final int type = Protocol.REGISTRY_SENDS_NODE_MANIFEST;
	
	int size;
	String[] routingIPs;
	int[] routingPorts;
	int[] routingIDs;

	public RegistrySendsNodeManifest(String[] routingIPs, int[] routingPorts, int[] routingIDs) {
		this.routingIPs = routingIPs;
		this.routingPorts = routingPorts;
		this.routingIDs = routingIDs;
		size = routingIPs.length;			//protocols will always assume they have the same size
	}
	
	public RegistrySendsNodeManifest(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		//remove the type
		din.readInt();
		
		size = din.readInt();
		
		//setup arrays
		routingIPs = new String[size];
		routingPorts = new int[size];
		routingIDs = new int[size];
		
		
		for (int i = 0; i < size; i++) {
			//read in IP
			int infoLength = din.readInt();
			byte[] infoBytes = new byte[infoLength];
			din.readFully(infoBytes);
			routingIPs[i] = new String(infoBytes);
			
			//read in Port
			routingPorts[i] = din.readInt();
			
			//read in ID
			routingIDs[i] = din.readInt();
		}
			
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
			dout.writeInt(routingIPs.length);
			
			//for each element in the tentative routing table being sent
			for (int i = 0; i < size; i++) {
				//write in IP
				byte[] infoBytes = routingIPs[i].getBytes();
				dout.writeInt(infoBytes.length);
				dout.write(infoBytes);
				
				//read in Port
				dout.writeInt(routingPorts[i]); 
				
				//read in ID
				dout.writeInt(routingIDs[i]);
			}
			
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
	
	public int getSize() {
		return size;
	}
	
	public String[] getRoutingIPs() {
		return routingIPs;
	}
	
	public int[] getRoutingPorts() {
		return routingPorts;
	}
	
	public int[] getRoutingIDs() {
		return routingIDs;
	}

}



























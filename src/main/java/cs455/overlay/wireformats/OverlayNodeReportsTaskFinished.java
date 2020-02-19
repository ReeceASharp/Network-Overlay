package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class OverlayNodeReportsTaskFinished implements Event {
	private static final int type = Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED;

	String ip;
	int port;
	int id;
	
	public OverlayNodeReportsTaskFinished(String ip, int port, int id) {
		this.ip = ip;
		this.port = port;
		this.id = id;
	}
	
	public OverlayNodeReportsTaskFinished(byte[] marshalledBytes) throws IOException {
		
		//create a wrapper around the bytes to leverage some methods to easily extract values
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		//disregard, the buffer starts at the beginning of the byte array
		din.readInt();
		
		//retrieve IP address
		int ipLength = din.readInt();
		//System.out.printf("IP Length: '%d'%n", ipLength);
		
		byte[] ipBytes = new byte[ipLength];
		din.readFully(ipBytes);
		ip = new String(ipBytes);
		
		//retrieve port
		port = din.readInt();
		id = din.readInt();
		
		//close wrapper streams
		baInputStream.close();
		din.close();	
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public byte[] getBytes() {
		byte[] marshalledBytes = null;
		
		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(byteOutStream));
		try {
			//write event type to decode on arrival
			dout.writeInt(type);
			
			//write IP address
			byte[] ipBytes = ip.getBytes();
			dout.writeInt(ipBytes.length);
			dout.write(ipBytes); 
			//System.out.println("IP: " + ip);
			
			//write port
			dout.writeInt(port);
			
			//write registry ID
			dout.writeInt(id);
			
			//ensure all is written before the buffer is converted to a byte array
			dout.flush();
			


			marshalledBytes = byteOutStream.toByteArray();
			
			byteOutStream.close();
			dout.close();
		} catch (IOException e) {
			//failed for some reason
			System.out.println(e);
		}
		
		return marshalledBytes;
	}

	public String getIP() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public int getID() {
		return id;
	}
	
	

}

package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class OverlayNodeSendsDeregistration implements Event {
	static final int type = Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION;
	
	private String ip;
	private int port;
	
	public OverlayNodeSendsDeregistration(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}
	
	public OverlayNodeSendsDeregistration(byte[] marshalledBytes) throws IOException {
		//System.out.println("OverlayNodeSendsDregistration::ctor");
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
		//System.out.printf("OverlayNodeSendsDregistration::IP: '%s', Port: '%d'%n", ip, port);
		
		//close wrapper streams
		baInputStream.close();
		din.close();		
		
	}
	
	

	@Override
	public int getType() {
<<<<<<< HEAD
		return Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION;
=======
		return type;
>>>>>>> branch 'master' of https://github.com/ReeceASharp/cs455_a1
	}

	@Override
	public byte[] getBytes() {
<<<<<<< HEAD
		
		
		// TODO Auto-generated method stub
		return null;
=======
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
			
			//ensure all is written before the buffer is converted to a byte array
			dout.flush();
			
			//System.out.printf("ip: '%s', Port: %d, ipBytes Length: '%d'%n", ip, port, ipBytes.length);


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
>>>>>>> branch 'master' of https://github.com/ReeceASharp/cs455_a1
	}

}

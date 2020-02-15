package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class OverlayNodeSendsDeregistration implements Event {
	static final int type = Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION;
	
	String ip;
	int port;
	
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
		return type;
	}

	@Override
	public byte[] getBytes() {
		
		
		// TODO Auto-generated method stub
		return null;
	}

}

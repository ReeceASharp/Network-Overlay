package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class OverlayNodeSendsRegistration implements Event {
	static final int type = Protocol.OVERLAY_NODE_SENDS_REGISTRATION;
	
	
	private byte[] ip;
	private int port;
	
	public OverlayNodeSendsRegistration(byte[] bs, int port) {
		this.ip = bs;
		this.port = port;
	}
	
	
	public OverlayNodeSendsRegistration(byte[] marshalledBytes) throws IOException {
		System.out.println("OverlayNodeSendsRegistration::ctor");
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
		ip = ipBytes;
		
		//retrieve port
		port = din.readInt();
		//System.out.printf("IP: '%s', Port: '%d'%n", ip, port);
		
		//close wrapper streams
		//System.out.println("OverlayNodeSendsRegistration::ctor::Closing inputStream");
		baInputStream.close();
		
		//System.out.println("OverlayNodeSendsRegistration::ctor::Closing datainputStream");
		din.close();		
		
	}
	

	@Override
	public int getType() {
		return type;
	}

	@Override
	public byte[] getBytes() {
		
		byte[] message = null;
		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(byteOutStream));
		
		try {
			//write event type to decode on arrival
			dout.writeInt(type);
			
			//write IP address
			//byte[] ipBytes = ip;
			dout.writeInt(ip.length);
			dout.write(ip); 
			//System.out.println("IP: " + ip);
			
			//write port
			dout.writeInt(port);
			
			//ensure all is written before the buffer is converted to a byte array
			dout.flush();
			
			//System.out.printf("ip: '%s', Port: %d, ipBytes Length: '%d'%n", ip, port, ipBytes.length);


			message = byteOutStream.toByteArray();
			
			byteOutStream.close();
			dout.close();
		} catch (IOException e) {
			//failed for some reason
			System.out.println(e);
		}
		
		return message;
	}
	
	public byte[] getIP() {
		return ip;
	}

	public int getPort() {
		return port;
	}
}

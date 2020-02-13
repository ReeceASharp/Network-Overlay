package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import cs455.overlay.node.Node;

public class OverlayNodeSendsRegistration implements Event {
	static final int type = Protocol.OVERLAY_NODE_SENDS_REGISTRATION;
	
	
	private String ip;
	private int port;
	
	
	public OverlayNodeSendsRegistration(byte[] marshalledBytes) throws IOException {
		System.out.println("OverlayNodeSendsRegistration::ctor");
		//create a wrapper around the bytes to leverage some methods to easily extract values
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		int length = din.readInt();
		
		
		System.out.printf("Message Value: '%d'%n", length);
		
		//retrieve ip address
		int ipLength = din.readInt();
		System.out.printf("IP Length: '%d'%n", ipLength);
		byte[] ipBytes = new byte[ipLength];
		din.readFully(ipBytes);
		ip = new String(ipBytes);
		
		//retrieve port
		port = din.readInt();
		
		//close wrapper streams
		baInputStream.close();
		din.close();		
		
		System.out.printf("IP: '%s', Port: '%d'%n", ip, port);
	}
	

	@Override
	public int getType() {
		return type;
	}

	@Override
	public byte[] getBytes(Node node) {
		int type = Protocol.OVERLAY_NODE_SENDS_REGISTRATION;
		byte[] message = null;
		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(byteOutStream));
		
		try {
			//write the data to the stream
			dout.writeInt(type);
			byte[] ipBytes = new String("localhost").getBytes();
			dout.writeInt(ipBytes.length);
			System.out.printf("ipBytes Length: '%d'%n", ipBytes.length);
			
			//dout.write(new String("127.0.0.1").getBytes());
			//make sure it's all written
			dout.flush();
			
			message = byteOutStream.toByteArray();
			byteOutStream.close();
			dout.close();
		} catch (IOException e) {
			//failed for some reason
			System.out.println();
			e.printStackTrace();
		}
		
		
		
		return message;
	}

}

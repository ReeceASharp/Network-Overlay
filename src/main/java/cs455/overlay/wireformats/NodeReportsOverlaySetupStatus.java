package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class NodeReportsOverlaySetupStatus implements Event {
	private static final int type = Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS;
	
	private int status;
	private String information;
	
	public NodeReportsOverlaySetupStatus(int status, String information) {
		this.status = status;
		this.information = information;
	}
	
	
	
	public NodeReportsOverlaySetupStatus(byte[] marshalledBytes) throws IOException {
		
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		//remove the type
		din.readInt();
		
		//get the status (int)
		status = din.readInt();
		
		//get the information string
		int infoLength = din.readInt();
		byte[] infoBytes = new byte[infoLength];
		din.readFully(infoBytes);
		information = new String(infoBytes);
		
		
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
		
		byte[] message = null;
		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(byteOutStream));
		try {
			dout.writeInt(type);
			
			dout.writeInt(status);
			
			byte[] infoBytes = information.getBytes();
			dout.writeInt(infoBytes.length);
			dout.write(infoBytes);
			
			dout.flush();
			
			System.out.printf("NodeReportsOverlaySetupStatus: ID: %d,'%s'%n", status, information);
			
			message = byteOutStream.toByteArray();
			byteOutStream.close();
			dout.close();
			
		} catch (IOException e) {
			//failed for some reason
			System.out.println(e);
		}
		
		return message;
	}

	public int getStatus() {
		return status;
	}
	
	public String getInfo() {
		return information;
	}

	
}

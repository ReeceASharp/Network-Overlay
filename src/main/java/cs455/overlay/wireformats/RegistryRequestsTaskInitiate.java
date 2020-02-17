package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RegistryRequestsTaskInitiate implements Event {
	static final int type = Protocol.REGISTRY_REQUESTS_TASK_INITIATE;
	
	int packetsToSend;

	public RegistryRequestsTaskInitiate(int packetsToSend) {
		this.packetsToSend = packetsToSend;
	}
	
	public RegistryRequestsTaskInitiate(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		//remove the type
		din.readInt();
		
		packetsToSend = din.readInt();
		
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
			dout.writeInt(packetsToSend);
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

}

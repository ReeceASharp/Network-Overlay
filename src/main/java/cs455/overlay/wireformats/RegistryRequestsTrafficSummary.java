package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RegistryRequestsTrafficSummary implements Event {
	private static final int type = Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY;

	@Override
	public int getType() {
		return type;
	}
	
	public RegistryRequestsTrafficSummary() { }
	

	@Override
	public byte[] getBytes() {
		
		byte[] marshalledBytes = null;
		
		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(byteOutStream));
		
		try {
			dout.writeInt(type);
			
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
}

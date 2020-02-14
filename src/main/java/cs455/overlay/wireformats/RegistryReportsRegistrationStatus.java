package cs455.overlay.wireformats;

public class RegistryReportsRegistrationStatus implements Event {
	static final int type = Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS;
	
	private String message;
	private String ip;
	private int port;
	
	public RegistryReportsRegistrationStatus(String message, String ip, int port) {
		this.message = message;
		this.ip = ip;
		this.port = port;
	}

	public RegistryReportsRegistrationStatus(byte[] marshalledBytes) {
		//put into a buffer
		
		
		//remove the type
		
		//get the status (int)
	
		//get the information string
		
		
		//close buffer
	}
	
	@Override
	public int getType() {
		return type;
	}

	@Override
	public byte[] getBytes() {
		
		
		return null;
	}

}

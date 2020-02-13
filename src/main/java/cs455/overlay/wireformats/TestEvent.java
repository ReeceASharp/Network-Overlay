package cs455.overlay.wireformats;

public class TestEvent implements Event {

	@Override
	public int getType() {
		
		return Protocol.TEST_EVENT;
	}

	@Override
	public byte[] getBytes() {
		
		
		return null;
	}

}

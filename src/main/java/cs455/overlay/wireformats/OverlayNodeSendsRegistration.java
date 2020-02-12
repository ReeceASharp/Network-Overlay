package cs455.overlay.wireformats;


public class OverlayNodeSendsRegistration implements Event {

	@Override
	public int getType() {
		return Protocol.OVERLAY_NODE_SENDS_REGISTRATION;
	}

	@Override
	public byte[] getBytes() {
		return this.getBytes();
	}

}

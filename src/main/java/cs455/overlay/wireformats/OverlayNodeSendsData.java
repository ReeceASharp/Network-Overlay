package cs455.overlay.wireformats;

public class OverlayNodeSendsData implements Event {

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return Protocol.OVERLAY_NODE_SENDS_DATA;
	}

	@Override
	public byte[] getBytes() {
		// TODO Auto-generated method stub
		return null;
	}

}

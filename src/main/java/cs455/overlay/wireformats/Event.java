package cs455.overlay.wireformats;

import cs455.overlay.node.Node;

public interface Event {
	public int getType();
	public byte[] getBytes();
}
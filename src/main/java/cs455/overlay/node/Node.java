package cs455.overlay.node;

import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;

public interface Node {
	public void onEvent(Event e);
	public EventFactory getFactory();
}

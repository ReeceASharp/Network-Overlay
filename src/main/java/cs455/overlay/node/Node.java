package cs455.overlay.node;

import java.net.Socket;

import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;

public interface Node {
	public void onEvent(Event e, Socket socket);
	public EventFactory getFactory();
	public void onCommand(String[] command);
	public void updateServerInfo(byte[] bs, int port);
	public byte[] getServerIP();
	public int getServerPort();
}

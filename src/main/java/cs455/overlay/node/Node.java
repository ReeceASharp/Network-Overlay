package cs455.overlay.node;

import java.net.Socket;

import cs455.overlay.wireformats.Event;

public interface Node {
	public void onEvent(Event e, Socket socket);
	//public EventFactory getFactory();
	public void onCommand(String[] command);
	public void updateServerInfo(String string, int port);
	public String getServerIP();
	public int getServerPort();
	//public TCPConnectionsCache getCache();
}

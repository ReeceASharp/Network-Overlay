package cs455.overlay.node;

import java.net.Socket;

import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;

public interface Node {
	public void onEvent(Event e, Socket socket);
<<<<<<< HEAD
	public EventFactory getFactory();
	public void onCommand(String[] command);
	public void updateServerInfo(byte[] bs, int port);
	public byte[] getServerIP();
=======
	//public EventFactory getFactory();
	public void onCommand(String[] command);
	public void updateServerInfo(String string, int port);
	public String getServerIP();
>>>>>>> branch 'master' of https://github.com/ReeceASharp/cs455_a1
	public int getServerPort();
}

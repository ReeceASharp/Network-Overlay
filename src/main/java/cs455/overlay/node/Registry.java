package cs455.overlay.node;

import java.io.*;

import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.Consts;
import cs455.overlay.wireformats.Event;

public class Registry implements Node {
	
	//private Scanner keyboard = new Scanner(System.in);
	//private Thread parser = new Thread(new InteractiveCommandParser(Consts.REGISTRY, this));
	//private static StatisticsCollectorAndDisplay statDisplay;
	
	
	public Registry() { }
	
	public static void main(String[] args) throws IOException {
		System.out.println("Registry::main");
		
		Registry node = new Registry();
		
		
		
		//start the server thread that will listen for clients wanting to connect
		Thread server = new Thread(new TCPServerThread(node));
		server.start();
		
		
	}

	@Override
	public void onEvent(Event e) {
		// TODO Auto-generated method stub
		switch(e.getType()) {
			case (Consts.OVERLAY_NODE_SENDS_REGISTRATION):
				nodeRegistration();
				break;
			case (Consts.OVERLAY_NODE_SENDS_DEREGISTRATION):
				nodeDeRegistration();
				break;
			case (Consts.NODE_REPORTS_OVERLAY_SETUP_STATUS):
				nodeSetupStatus();
				break;
			case (Consts.OVERLAY_NODE_REPORTS_TASK_FINISHED):
				nodeTaskFinished();
				break;
			case (Consts.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY):
				nodeReportTraffic();
				break;
			default:
				System.err.printf("Registry::onEvent::invalid control message: %d%n", e.getType());
				break;
		}
	}
	
	//node wants to register with the registry
	private void nodeRegistration() {
		
	}
	
	//node wants to deregister
	private void nodeDeRegistration() {
		
	}
	
	//node is reporting its status
	private void nodeSetupStatus() {
		
	}
	
	private void nodeTaskFinished() {
		
	}
	
	private void nodeReportTraffic() {
		
		
	}
	
	private void setupOverlay(String[] args) {
		
	}
	
	private void listNodes() {
		
	}
}

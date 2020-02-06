package cs455.overlay.node;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import cs455.overlay.util.Consts;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.util.StatisticsCollectorAndDisplay;
import cs455.overlay.wireformats.Event;

public class Registry implements Node {
	
	private InteractiveCommandParser parser = new InteractiveCommandParser(Consts.REGISTRY);
	private Scanner keyboard = new Scanner(System.in);
	
	private static StatisticsCollectorAndDisplay statDisplay = new StatisticsCollectorAndDisplay();
	
	//We specify the port 5001, which is what we will listen to for incoming connections
	static Integer OUR_PORT = 0;
	
	private static ArrayList<nodeEntry> nodeList;

	public static void main(String[] args) throws IOException {
		nodeList = new ArrayList<>();
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

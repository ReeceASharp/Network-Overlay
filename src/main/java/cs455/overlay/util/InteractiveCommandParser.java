package cs455.overlay.util;

import java.util.Scanner;

public class InteractiveCommandParser implements Runnable {
	int type;
	
	private Scanner input = new Scanner(System.in);
	
	public InteractiveCommandParser(int type) {
		this.type = type;
	}
	
	
	@Override
	public void run() {
		String command = input.nextLine();
		parseInput(command);
		
		
	}
	
	private void parseInput(String input) {
		String[] args = input.split(" ");
		switch (args[0]) {
			case "list-messaging-nodes":
				//listNodes();
				break;
			case "setup-overlay":
				//setupOverlay(args);
				break;
			case "list-routing-tables":
				break;
			case "start":
				break;
			}
	}
	
	/*
	 * 
	 */
	private void help() {
		switch (type) {
		case Consts.MESSAGING:
			break;
		case Consts.REGISTRY:
			break;
		}
	}
	

}

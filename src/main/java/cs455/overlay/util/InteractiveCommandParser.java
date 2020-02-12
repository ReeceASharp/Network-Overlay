package cs455.overlay.util;

import java.util.Scanner;

import cs455.overlay.node.Node;
import cs455.overlay.wireformats.Protocol;

public class InteractiveCommandParser implements Runnable {
	int type;
	Node node;
	
	private Scanner input = new Scanner(System.in);
	
	public InteractiveCommandParser(int type, Node node) {
		this.type = type;
		this.node = node;
	}
	
	@Override
	public void run() {
		System.out.println("InteractiveCommandParser::run");
		while(true) {
			System.out.print("Input: ");
			String command = input.nextLine();
			parseInput(command);
		}
		
		
	}
	
	//differentiate between what the interactiveCommandParser is listing
	private void parseInput(String input) {
		if (type == Protocol.REGISTRY)
			parseMessaging(input);
		else
			parseRegistry(input);
	
		
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
	
	private void parseMessaging(String input) {
		
	}
	
	private void parseRegistry(String input) {
		
	}
	
	/*
	 * 
	 */
	private void help() {
		switch (type) {
		case Protocol.MESSAGING:
			break;
		case Protocol.REGISTRY:
			break;
		}
	}
	

}

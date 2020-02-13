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
			parseRegistry(input);
		else if (type == Protocol.MESSAGING)
			parseMessaging(input);
	}
	
	private void parseMessaging(String input) {
		String[] args = input.split(" ");
		switch (args[0]) {
		case "print-counter-and-diagnostics":
		case "exit-overlay":
			System.out.println("Valid Command");
			node.onCommand(input);
		default:
			help();
		}
	}
	
	private void parseRegistry(String input) {
		String[] args = input.split(" ");
		switch (args[0]) {
			//fall through then attempt to parse
			case "list-messaging-nodes":
			case "setup-overlay":
			case "list-routing-tables":
			case "start":
				System.out.println("Valid Command");
				node.onCommand(input);
				break;
			default:
				help();
		}
	}
	
	/*
	 * 
	 */
	private void help() {
		switch (type) {
		case Protocol.MESSAGING:
			System.out.println("parseMessaging::invalid Command");
			//TODO: update command list to be a string[]
			break;
		case Protocol.REGISTRY:
			System.out.println("parseRegistry::invalid Command");
			//TODO: update command list to be a string[]
			break;
		}
	}
	

}

package cs455.overlay.util;

import java.util.Arrays;
import java.util.Scanner;

import cs455.overlay.node.Node;
import cs455.overlay.wireformats.Protocol;

public class InteractiveCommandParser implements Runnable {
	int type;
	Node node;
	
	private Scanner input = new Scanner(System.in);
	private volatile boolean running = true;
	
	public InteractiveCommandParser(int type, Node node) {
		this.type = type;
		this.node = node;
	}
	
	@Override
	public void run() {
		while(running) {
			String command = input.nextLine();
			parseInput(command);
		}
	}
	
	public void exit() {
		running = false;
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
		
		
		if (Arrays.asList(Commands.MESSAGING_COMMANDS).contains(args[0]))
			node.onCommand(args);
		else
			help();
	}
	
	private void parseRegistry(String input) {
		String[] args = input.split(" ");
		
		
		if (Arrays.asList(Commands.REGISTER_COMMANDS).contains(args[0]))
			node.onCommand(args);
		else
			help();
	}
	
	/*
	 * 
	 */
	private void help() {
		System.out.println("Available Commands: ");
		System.out.println("*******************");
		
		switch (type) {
		case Protocol.MESSAGING:
			for (String s : Commands.MESSAGING_COMMANDS)
				System.out.println(s);
			break;
		case Protocol.REGISTRY:
			for (String s : Commands.REGISTER_COMMANDS)
				System.out.println(s);
			
			break;
		}
	}
	

}

package cs455.overlay.util;

import java.util.Arrays;
import java.util.Scanner;

import cs455.overlay.node.Node;
import cs455.overlay.wireformats.Protocol;

//Console input for each of the nodes
public class InteractiveCommandParser implements Runnable {
	int type;
	Node node;
	
	private Scanner input = new Scanner(System.in);	//keyboard input
	
	public InteractiveCommandParser(int type, Node node) {
		this.type = type;
		this.node = node;
	}
	
	@Override
	public void run() {
		while(true) {
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
	
	//get command for MessagingNode
	private void parseMessaging(String input) {
		String[] args = input.split(" ");
		
		
		if (Arrays.asList(Commands.MESSAGING_COMMANDS).contains(args[0]))
			node.onCommand(args);
		else
			help();
	}
	
	//get command for Registry
	private void parseRegistry(String input) {
		String[] args = input.split(" ");
		if (Arrays.asList(Commands.REGISTER_COMMANDS).contains(args[0]))
			node.onCommand(args);
		else
			help();
	}
	
	//I kept forgetting the commands, and wanted a quick source to copy and paste
	//hopefully this doesn't count as a gui
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

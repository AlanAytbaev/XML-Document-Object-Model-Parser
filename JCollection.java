import java.util.*;
import java.io.*;

public class JCollection {
	ElementNode head = new ElementNode("head"); //initial node
	ElementNode[] order = new ElementNode[300]; //order of nodes
	int currentnodeindex = 0; //current node to be processed
	int arraycap = 0;
	int chariterator = 0;
	char currentchar;
	int firstappend = 0;
	Scanner sourcefileinput; //main scanner for reading XML
	String currentline;
	
	public static void main(String [] args) {
		String sourcefilename; //name of file
		if(args.length > 0) {
			sourcefilename = args[0];
		} else {
			return;
		}
		
		File sourcefile = new File(sourcefilename);
		
		if(sourcefile.exists() == false) {
			return;
		}
		JCollection XMLreader = new JCollection();
		XMLreader.getXML(sourcefile);
	}
	public class Node {
		String name;
		ElementNode parent;
		ElementNode[] subnodes;
		String[][] values;
		int subnodelength = 0;
		String text;
		//Element
		public Node(ElementNode a, ElementNode[] b, String c, String[][] d, String e) {
			parent = a;
			subnodes = b;
			name = c;
			values = d;
			text = e;
		}
		//Head
		public Node (String a) {
			name = a;
			parent = null;
			subnodes = new ElementNode[100];
			}
	}
	public class ElementNode extends Node {
			ElementNode(ElementNode a, ElementNode[] b, String c, String[][] d, String e) {
				super(a, b, c, d, e);
			}
			ElementNode(String a) {
				super(a);
			}
	}
	
	
	public JCollection() {
		
	}
	 
	public void getXML(File file) {
		order[0] = head;
		
		try {
			sourcefileinput = new Scanner(file);
		} catch (Exception e) { 
			System.out.println("Error: " + e);
		}
		String output = sourcefileinput.nextLine();
		currentline = output;
		int choice = judgeLine(output);
		switch(choice) {
				case 1: 
				logCompactTag(output);	
				break;
				case 0: 
				logElement(output);
				break;
				case -1: 
				break;
		}
	}
	
	public int judgeLine(String input) {
		int choice;
		if(input.indexOf("<") < input.indexOf("/>")) {
				choice = 1;
		} else if(input.indexOf("<") < input.indexOf(">") && input.indexOf("<") != input.indexOf("</")){
				choice = 0;
		} else {
			choice = -1;
		}
		switch(choice) {
				case 1: return 1;
				case 0: return 0;
				case -1: return -1;
		}
		return -1;
	}
	public void logElement(String input) {
		int first = input.indexOf("<"); // initialize variables for creation of newNode
		int second = input.indexOf(">");
		String[] elementdata = getItems(input.substring(first,second));
		String[][] formattedelementdata = formatItems(elementdata);
		String elementstring = formattedelementdata[0][1];
		StringBuilder text = new StringBuilder(1000);
		ElementNode[] children = new ElementNode[100];
		ElementNode newNode = new ElementNode(order[currentnodeindex], children, elementstring, formattedelementdata, null); //create node
		push(newNode); //push new node on stack
		System.out.println("Node Created: " + newNode.name + " | " + "Parent Node: " + newNode.parent.name + " | ");
		
		chariterator = second + 1 + firstappend;
		
		if(chariterator < currentline.length()) {
			currentchar = currentline.charAt(chariterator);
		} else {
			currentline = sourcefileinput.nextLine();
			chariterator = 0;
			currentchar = currentline.charAt(0);
		}
		
		boolean EOF = false;
		boolean endtagreached = false;
		StringBuilder appendtext = new StringBuilder(1000);
		System.out.println("CURRENT INPUT BEING PROCESSED: " + input);
		System.out.println("STARTING CURRENTCHAR: " + currentchar);
		
		while(!EOF && !endtagreached) {
			System.out.println("==========================================");
			System.out.println("CURRENT LINE BEING PROCESSED: " + currentline);
			System.out.println("ELEMENTSTRING: " + elementstring);
			System.out.println("CURRENTCHAR: " + currentchar);
			System.out.println("CHARITERATOR: " + chariterator);
			while(chariterator < currentline.length()) {
				currentchar = currentline.charAt(chariterator);
				if(currentchar == '<' && currentline.charAt(chariterator + 1) == '/' && !endtagreached) {
					StringBuilder foo = new StringBuilder(100);
					while(currentline.charAt(chariterator) != '>' && chariterator < currentline.length()) {
						currentchar = currentline.charAt(chariterator);
						foo.append(currentchar);
						System.out.println("APPENDED: " + currentchar + " AT: " + chariterator);
						chariterator++;
					}
					foo.append(currentline.charAt(chariterator));
					System.out.println("APPENDED: " + currentline.charAt(chariterator) + " AT: " + chariterator);
					String recursive = foo.toString();
					recursive = recursive.substring(2, recursive.length() - 1);
					System.out.println("FOUND END TAG: " + recursive);
					if(recursive.equals(elementstring)) {
						System.out.println("EXITING TAG: " + elementstring);
						System.out.println("APPENDTEXT: " + appendtext.toString());
						System.out.println("ENDTAGREACHED SET TO TRUE");
						pop();
						endtagreached = true;
						break;
					} 
					} else if(currentchar == '<' && !endtagreached) {
					firstappend = chariterator;
					StringBuilder foo = new StringBuilder(100);
					while(currentline.charAt(chariterator) != '>' && chariterator < currentline.length()) {
						currentchar = currentline.charAt(chariterator);
						foo.append(currentchar);
						chariterator++;
					}
					foo.append(currentline.charAt(chariterator));
					String recursive = foo.toString();
					System.out.println("FOUND TAG: " + recursive);
					int choice = judgeLine(foo.toString());
					switch(choice) {
						case 1: 
							logCompactTag(recursive);	
							break;
						case 0: 
							logElement(recursive);
							break;
						case -1: 
					 }
				} else  {
					if(endtagreached == false) {
					currentchar = currentline.charAt(chariterator);
					appendtext.append(currentchar);
					System.out.println("ADDED: " + currentchar + " TO: " + elementstring + " AT: " + chariterator);
					}
				}
				chariterator++;
				System.out.println("CHARITERATOR INCREMENTED");
			}
			if(endtagreached) {
				System.out.println("ENDTAGREACHED TESTED, LOOP BREAK");
				break;
			}
 			if(sourcefileinput.hasNextLine() == false) {
				System.out.println("BAIL");
				EOF = true;
				break;
			} else {
				System.out.println("NEW LINE CALLED SUCCESSFULLY");
				currentline = sourcefileinput.nextLine();
				chariterator = 0;
		}
		}
	System.out.println("METHOD FINISHED: " + elementstring);
	}
	
	
	public void logCompactTag(String input) {
		String[] linedata = getItems(input);
		String[][] formattedlinedata = formatItems(linedata);
		int iterator = 0;
		Node newNode = new ElementNode(order[currentnodeindex], null, formattedlinedata[0][1], formattedlinedata, null); //create node
		System.out.println("Compact Node Created: " + newNode.name + " | " + "Parent Node: " + newNode.parent.name + " | ");
	}
	
	public boolean isTextualContext(String input) {
		int count = 0;
		for(int i = 0; i < input.length(); i++) {
			if(input.charAt(i) == '<')
				count++;
		}
		if(count > 1) {
			return true;
		} else {
			return false;
		}
	}
	public String[] getItems(String input) {
		int firstindex = 0;
		int secondindex;
		int stringindex = 0; 
		int i,j;
		String[] output = new String[100];
		while(stringindex != output.length - 1) {
			for(i = firstindex; i != input.length(); i++) {
				if(Character.isLetterOrDigit(input.charAt(i))) {
					break;
				}
			}
			firstindex = i;
			secondindex = firstindex;
			for(j = secondindex; j != input.length(); j++) {
				if(!Character.isLetterOrDigit(input.charAt(j))) {
					break;
				}
			}
			secondindex = j;
		
			output[stringindex] = input.substring(firstindex, secondindex);
			stringindex++;
			firstindex = secondindex;
			
		}
		 
		return output;
		
	}
	
	public String[][] formatItems(String[] input) {
		int iterator = 0; 
		String[][] output = new String[100][2];
		String name = input[0]; 
		iterator++;
		output[0][0] = "NAME";
		output[0][1] = input[0];
		int i = 1;
		int j = 0;
		while(iterator < input.length - 1) {
			output[i][j] = input[iterator];
			iterator++;
			if(j == 0) {
				j++;
			} else {
				j--;
				i++;
			}
		}
		return output;
	}  
	
	public void pop() {
		ElementNode currentnode = order[currentnodeindex];
		System.out.println("POPPING: " + currentnode.name);
		order[currentnodeindex] = null;
		currentnodeindex--;
	}
	
	public void push(ElementNode input) {
		ElementNode currentnode = order[currentnodeindex];
		currentnode.subnodes[currentnode.subnodelength] = input;
		currentnode.subnodelength++;
		currentnodeindex++;
		order[currentnodeindex] = input;
	}
	  
}
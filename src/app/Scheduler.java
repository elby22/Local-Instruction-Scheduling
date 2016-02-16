package app;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.StringTokenizer;

import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.generate.*;
import org.jgrapht.traverse.*;
public class Scheduler {
	
	static ArrayList<Instruction> instructions;
	
	static SimpleDirectedWeightedGraph dependencies;
	
	static Instruction root;
	
	public static void main(String[] args) {
		String mode = args[0];
		String input = args[1].replaceAll(",", "");
		
		instructions = new ArrayList<Instruction>();

		parseILOC(input);
		
		
		dependencies = new SimpleDirectedWeightedGraph(DefaultEdge.class);
		
		//Add verticies
		for(Instruction ins : instructions){
			dependencies.addVertex(ins);
		}
		
		//Build graph edges
		addEdges();
		
		//BAD IDEA PLEASE CHANGE
		//Look below
		root = instructions.get(instructions.size() - 1);
		
		root.priority = root.getLatency();
		
		//Find leafs
		//CAN FIND ROOTS, DO THAT LATER
		for(Instruction ins : instructions){
			//Check if leaf
			System.out.println(dependencies.outgoingEdgesOf(ins).size());
			System.out.println(dependencies.incomingEdgesOf(ins).size());
			System.out.println("++++++++++++");

			if(dependencies.outgoingEdgesOf(ins).isEmpty()){
				ins.isLeaf = true;
			}
			if(dependencies.incomingEdgesOf(ins).isEmpty()){
				ins.isRoot = true;
			}
		}
				
		prioritize();
		
		optionA();
		
//		//Print Graph
//		Set verticies = dependencies.vertexSet();
//		Set edges = dependencies.edgeSet();
//				
//		for(Object i : verticies){
//			
//			Instruction ii = (Instruction)i;
//			System.out.println("Priority: " + ii.priority + " - " + i);
//			for(Object e : edges){
//				
//				if(dependencies.getEdgeSource(e).equals(i)){
//					System.out.println("	" + e);
//				}
//			}
//		}
		
		for(Instruction ins : instructions){
			System.out.println(ins.schedule +" =S= " + ins.toString() + " =P= " + ins.priority);
		}
		
		
	}
	
	
	public static void prioritize(){
		
		BreadthFirstIterator bfs = new BreadthFirstIterator(dependencies, root);
		bfs.next();
		while(bfs.hasNext()){
			Instruction ins = (Instruction)bfs.next();
			
			Set edges = dependencies.edgesOf(ins);
			System.out.println(ins);
			
			//There should only be one source edge
			for(Object e : edges){
				Instruction source = (Instruction)dependencies.getEdgeSource(e);
				if(!source.equals(ins)){
					ins.priority = source.priority + ins.getLatency();
				}
			}
		}
	}
	
	
	//DOES NOT ACCOUNT FOR ANTI, NOP AND OUTPUT
	//Also does not yet add weights.
	public static void addEdges(){
		for(int i = instructions.size() - 1; i >= 0; i--){
			
			
			
			Instruction thisIns = instructions.get(i);
			String type = thisIns.getType();
			
			boolean found1 = false;
			boolean found2 = false;

			for(int j = i - 1; j >= 0; j--){
				
				Instruction thatIns = instructions.get(j);
				
				if(type.equals("add") ||
				   type.equals("sub") ||
				   type.equals("div") ||
				   type.equals("mult")){
					
					//Make sure both left side operands are found, then break loop
					if(thisIns.getIn1().equals(thatIns.getOut()) && !found1){
						dependencies.addEdge(thisIns, thatIns);
						found1 = true;
					}else if(thisIns.getIn2().equals(thatIns.getOut()) && !found2){
						dependencies.addEdge(thisIns, thatIns);
						found2 = true;
					}
					
				}else if(type.equals("addI") || 
						 type.equals("subI") || 
						 type.equals("storeAO") ||
						 type.equals("storeAI")){
					
					//Only have one operand to find
					if(thisIns.getIn1().equals(thatIns.getOut()) && !found1){
						dependencies.addEdge(thisIns, thatIns);
						found1 = true;
					}
					
				}else if(type.equals("loadAO") && thatIns.getType().equals("storeAO")){
					
					if(thisIns.getIn1().equals(thatIns.getOut()) && thisIns.getRegOffset().equals(thatIns.getRegOffset())){
						dependencies.addEdge(thisIns, thatIns);
						break;
					}
				}else if(type.equals("loadAI") && thatIns.getType().equals("storeAI")){
					
					if(thisIns.getIn1().equals(thatIns.getOut()) && thisIns.getOffset() == thatIns.getOffset()){
						dependencies.addEdge(thisIns, thatIns);
						break;
					}
				}
				//WHAT ABOUT NOP
			}
		}
	}
	
	/*
	 * Parses the input string and stores instructions in 'instructions' ArrayList
	 * Map of the instructions and their feilds:
	 * 
		//loadI [constant] => [out]
		//load [in1] => [out]
		//loadAI [in1], [offset] => [out]
		//loadAO [in1], [regOffset] => [out]
		//store [in1] => [out]
		//storeAI [in1] => [out], [offset]
		//storeAO [in1] => [out], [regOffset]
		//addI [in1], [const] => [out]
		//add [in1], [in2] => [out]
		//subI [in1], [const] => [out]
		//sub [in1], [in2] => [out]
		//mult [in1], [in2] => [out]
		//div [in1], [in2] => [out]
		//nop
		//output [out]
	 */
	
	public static void parseILOC(String input){
		StringTokenizer tokenizer = new StringTokenizer(input);
		int count = 1;
		//Parse
		while(tokenizer.hasMoreTokens()){
			String next = tokenizer.nextToken();
			Instruction ins = null;
			
			//loadI [constant] => [out]
			if("loadI".equals(next)){
				ins = new Instruction(next);
				
				next = tokenizer.nextToken();
				ins.setConst(Integer.parseInt(next));
				
				next = tokenizer.nextToken(); // =>
				next = tokenizer.nextToken();
				ins.setOut(next);
				ins.latency = 1;
				
				ins.instructionString = ins.getType() + " " + ins.getConst() + " => " + ins.getOut();
				
			//load [in1] => [out]
			}else if("load".equals(next)){
				
				ins = new Instruction(next);
				next = tokenizer.nextToken();
				
				ins.setIn1(next);
				
				next = tokenizer.nextToken(); // =>
				next = tokenizer.nextToken();
				ins.setOut(next);
				ins.latency = 5;
				
				ins.instructionString = ins.getType() + " " + ins.getConst() + " => " + ins.getOut();
				
			//loadAI [in1], [offset] => [out]
			}else if("loadAI".equals(next)){
				
				ins = new Instruction(next);
				next = tokenizer.nextToken();
				
				ins.setIn1(next);
				next = tokenizer.nextToken();
				
				ins.setOffset(Integer.parseInt(next));
				next = tokenizer.nextToken(); // =>
				next = tokenizer.nextToken();
				
				ins.setOut(next);
				ins.latency = 5;
				
				ins.instructionString = ins.getType() + " " + ins.getIn1() + ", " + ins.getOffset() + " => " + ins.getOut();
				
			//loadAO [in1], [regOffset] => [out]
			}else if("loadAO".equals(next)){
				
				ins = new Instruction(next);
				next = tokenizer.nextToken();
				
				ins.setIn1(next);
				next = tokenizer.nextToken();
				
				ins.setRegOffset(next);
				next = tokenizer.nextToken(); // =>
				next = tokenizer.nextToken();
				
				ins.setOut(next);
				ins.latency = 5;
				
				ins.instructionString = ins.getType() + " " + ins.getIn1() + ", " + ins.getRegOffset() + " => " + ins.getOut();

			//store [in1] => [out]
			}else if("store".equals(next)){
				
				ins = new Instruction(next);
				next = tokenizer.nextToken();
				
				ins.setIn1(next);				
				next = tokenizer.nextToken(); // =>
				next = tokenizer.nextToken();
				
				ins.setOut(next);
				ins.latency = 5;
				
				ins.instructionString = ins.getType() + " " + ins.getIn1() + " => " + ins.getOut();

				
			//storeAI [in1] => [out], [offset]
			}else if("storeAI".equals(next)){
				
				ins = new Instruction(next);
				next = tokenizer.nextToken();
				
				ins.setIn1(next);				
				next = tokenizer.nextToken(); // =>
				next = tokenizer.nextToken();
				
				ins.setOut(next);
				next = tokenizer.nextToken();
				
				ins.setOffset(Integer.parseInt(next));
				ins.latency = 5;
				
				ins.instructionString = ins.getType() + " " + ins.getIn1() + " => " + ins.getOut() + ", " + ins.getOffset(); 

				
			//storeAO [in1] => [out], [regOffset]
			}else if("storeAO".equals(next)){
				
			    ins = new Instruction(next);
				next = tokenizer.nextToken();
				
				ins.setIn1(next);				
				next = tokenizer.nextToken(); // =>
				next = tokenizer.nextToken();
				
				ins.setOut(next);
				next = tokenizer.nextToken();
				
				ins.setRegOffset(next);
				ins.latency = 5;
				
				ins.instructionString = ins.getType() + " " + ins.getIn1() + " => " + ins.getOut() + ", " + ins.getRegOffset(); 

				
			//addI [in1], [const] => [out]
			}else if("addI".equals(next)){
				
			    ins = new Instruction(next);
				next = tokenizer.nextToken();
				
				ins.setIn1(next);
				next = tokenizer.nextToken();
				
				//Input doesn't matter here, no dependiences 
				ins.setConst(Integer.parseInt(next));
				next = tokenizer.nextToken(); // =>
				next = tokenizer.nextToken();
				
				ins.setOut(next);
				next = tokenizer.nextToken();
				ins.latency = 1;
				
				ins.instructionString = ins.getType() + " " + ins.getIn1() + ", " + ins.getConst() + " => " + ins.getOut();

				
			//add [in1], [in2] => [out]
			}else if("add".equals(next)){
				
			    ins = new Instruction(next);
				next = tokenizer.nextToken();
				
				ins.setIn1(next);
				next = tokenizer.nextToken();

				ins.setIn2(next);
				next = tokenizer.nextToken(); // =>
				next = tokenizer.nextToken();
				
				ins.setOut(next);

				ins.latency = 1;
				
				ins.instructionString = ins.getType() + " " + ins.getIn1() + ", " + ins.getIn2() + " => " + ins.getOut();

				
			//subI [in1], [const] => [out]
			}else if("subI".equals(next)){
				
			    ins = new Instruction(next);
				next = tokenizer.nextToken();
				
				ins.setIn1(next);
				next = tokenizer.nextToken();
				
				//Input doesn't matter here, no dependiences 
				ins.setConst(Integer.parseInt(next));
				next = tokenizer.nextToken(); // =>
				next = tokenizer.nextToken();
				
				ins.setOut(next);
				next = tokenizer.nextToken();
				ins.latency = 1;
				
				ins.instructionString = ins.getType() + " " + ins.getIn1() + ", " + ins.getConst() + " => " + ins.getOut();

				
			//sub [in1], [in2] => [out]
			}else if("sub".equals(next)){
				
			    ins = new Instruction(next);
				next = tokenizer.nextToken();
				
				ins.setIn1(next);
				next = tokenizer.nextToken();

				ins.setIn2(next);
				next = tokenizer.nextToken(); // =>
				next = tokenizer.nextToken();
				
				ins.setOut(next);
				ins.latency = 1;
				
				ins.instructionString = ins.getType() + " " + ins.getIn1() + ", " + ins.getIn2() + " => " + ins.getOut();

				
			//mult [in1], [in2] => [out]
			}else if("mult".equals(next)){
				
			    ins = new Instruction(next);
				next = tokenizer.nextToken();
				
				ins.setIn1(next);
				next = tokenizer.nextToken();

				ins.setIn2(next);
				next = tokenizer.nextToken(); // =>
				next = tokenizer.nextToken();
				
				ins.setOut(next);

				ins.latency = 3;
				
				ins.instructionString = ins.getType() + " " + ins.getIn1() + ", " + ins.getIn2() + " => " + ins.getOut();

				
			//div [in1], [in2] => [out]
			}else if("div".equals(next)){
				
			    ins = new Instruction(next);
				next = tokenizer.nextToken();
				
				ins.setIn1(next);
				next = tokenizer.nextToken();

				ins.setIn2(next);
				next = tokenizer.nextToken(); // =>
				next = tokenizer.nextToken();
				
				ins.setOut(next);

				ins.latency = 3;
				
				ins.instructionString = ins.getType() + " " + ins.getIn1() + ", " + ins.getIn2() + " => " + ins.getOut();

			
			//nop
			}else if("nop".equals(next)){ 
				
			    ins = new Instruction(next);
				
				ins.latency = 1;
				
				ins.instructionString = ins.getType();
				
			//output [out]
			}else if("output".equals(next)){ 
				
			    ins = new Instruction(next);
				next = tokenizer.nextToken();

				ins.setOut(next); 
				ins.latency = 1;
				
				ins.instructionString = ins.getType() + " " + ins.getOut();

			}
			ins.inputIndex = count;
			instructions.add(ins);
			count++;
		}
	}
	
	public static void optionA(){
		int cycle = 1;
		PriorityQueue<Instruction> ready = new PriorityQueue<Instruction>(); 
		ArrayList<Instruction> active = new ArrayList<Instruction>();
		
		for(Instruction ins : instructions){
			if(ins.isLeaf) ready.add(ins);
		}
		
		while(!(ready.isEmpty() && active.isEmpty())){
			if(!ready.isEmpty()){
				Instruction op = ready.remove();
				op.schedule = cycle;
				active.add(op);
			}
			
			cycle++;
			ArrayList<Instruction> removeFromActive = new ArrayList<Instruction>();
			
			for(Instruction op : active){
				if(op.schedule + op.latency <= cycle){
					removeFromActive.add(op);
					//Tree is reversed, so the outgoing edges point to instructions that must fire before this instruction
					Set source = dependencies.outgoingEdgesOf(op);
					boolean thisOpReady = true;
					for(Object edge : source){
						DefaultEdge e = (DefaultEdge)edge;
						Instruction predecessor = (Instruction)dependencies.getEdgeTarget(e);
						//
						if(!ready.contains(predecessor)){
							thisOpReady = false;
							break;
						}
					}
					
					if(thisOpReady){
						ready.add(op);
					}
				}
			}
			
			active.removeAll(removeFromActive);
		}
		
	}

}

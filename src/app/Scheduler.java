package app;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.generate.*;
import org.jgrapht.traverse.*;
public class Scheduler {
	
	static ArrayList<Instruction> instructions;

	public static void main(String[] args) {
		String mode = args[0];
		String input = args[1].replaceAll(",", "");
		
		instructions = new ArrayList<Instruction>();

		parseILOC(input);
		
//		for(Instruction ins : instructions){
//			System.out.println(instructions.indexOf(ins) + " " + ins.toString());
//		}
		
		SimpleDirectedWeightedGraph dependencies = new SimpleDirectedWeightedGraph(DefaultEdge.class);
		
		//Add verticies
		for(Instruction ins : instructions){
			dependencies.addVertex(ins);
		}

		for(int i = 0; i < instructions.size(); i++){
			
			String out = instructions.get(i).getOut();
			int outOffset = instructions.get(i).getOffset();
			String outRegOffset = instructions.get(i).getRegOffset();

			for(int j = i+1; j < instructions.size(); j++){
				
				//true dependenceis output : storeAI [in1] => [out], [offset]
				//loadAI [in1], [offset] => [out]
				if(instructions.get(i).getType().equals("storeAI") && instructions.get(j).getType().equals("loadAI")){
					if(out.equals(instructions.get(j).getIn1()) && outOffset == instructions.get(j).getOffset()){
						dependencies.addEdge(instructions.get(i), instructions.get(j));
					}
				}
				//true dependenceis output : storeAO [in1] => [out], [regOffset]
				//loadAO [in1], [regOffset] => [out]
				else if(instructions.get(i).getType().equals("storeAO") && instructions.get(j).getType().equals("loadAO")){
					if(out.equals(instructions.get(j).getIn1()) && outRegOffset.equals(instructions.get(j).getRegOffset())){
						dependencies.addEdge(instructions.get(i), instructions.get(j));
					}
				}
				//Everything else
				else if(out.equals(instructions.get(j).getIn1())
						|| out.equals(instructions.get(j).getIn2())){
					dependencies.addEdge(instructions.get(i), instructions.get(j));
				}	
					
			}
		}
		
		Set verticies = dependencies.vertexSet();
		Set edges = dependencies.edgeSet();
		
		for(Object i : verticies){
			
			System.out.println(i);
			for(Object e : edges){
				
				if(dependencies.getEdgeSource(e).equals(i)){
					System.out.println("	" + e);
				}
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

}

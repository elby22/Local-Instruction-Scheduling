import java.util.ArrayList;
import java.util.StringTokenizer;

public class Scheduler {
	
	static ArrayList<Instruction> instructions;

	public static void main(String[] args) {
		String mode = args[0];
		String input = args[1];
		
		input = input.replaceAll(",", "");
		
		instructions = new ArrayList<Instruction>();
		System.out.println(instructions);
	}
	
	public static void parseILOC(String input){
		StringTokenizer tokenizer = new StringTokenizer(input);
		
		//Parse
		while(tokenizer.hasMoreTokens()){
			String next = tokenizer.nextToken();
			System.out.println(next);
			Instruction ins = null;
			if("loadI".equals(next)){
				ins = new Instruction(next);
				
				next = tokenizer.nextToken();
				ins.setMemConst(Integer.parseInt(next));
				
				next = tokenizer.nextToken(); // =>
				next = tokenizer.nextToken();
				ins.setOut1(next);
				ins.latency = 1;
			}else if("load".equals(next)){
				
				ins = new Instruction(next);
				next = tokenizer.nextToken();
				
				ins.setIn1(next);
				
				next = tokenizer.nextToken(); // =>
				next = tokenizer.nextToken();
				ins.setOut1(next);
				ins.latency = 5;
			}else if("loadAI".equals(next)){
				
				ins = new Instruction(next);
				next = tokenizer.nextToken();
				
				ins.setIn1(next);
				next = tokenizer.nextToken();
				
				ins.setOffset(Integer.parseInt(next));
				next = tokenizer.nextToken(); // =>
				next = tokenizer.nextToken();
				
				ins.setOut1(next);
				ins.latency = 5;
			}else if("loadAO".equals(next)){
				
				ins = new Instruction(next);
				next = tokenizer.nextToken();
				
				ins.setIn1(next);
				next = tokenizer.nextToken();
				
				ins.setIn2(next);
				next = tokenizer.nextToken(); // =>
				next = tokenizer.nextToken();
				
				ins.setOut1(next);
				ins.latency = 5;
			}else if("store".equals(next)){
				
				ins = new Instruction(next);
				next = tokenizer.nextToken();
				
				ins.setIn1(next);				
				next = tokenizer.nextToken(); // =>
				next = tokenizer.nextToken();
				
				ins.setOut1(next);
				ins.latency = 5;
			}else if("storeAI".equals(next)){
				
				ins = new Instruction(next);
				next = tokenizer.nextToken();
				
				ins.setIn1(next);				
				next = tokenizer.nextToken(); // =>
				next = tokenizer.nextToken();
				
				ins.setOut1(next);
				next = tokenizer.nextToken();
				
				ins.setOffset(Integer.parseInt(next));
				ins.latency = 5;
			}else if("storeAO".equals(next)){
				
			    ins = new Instruction(next);
				next = tokenizer.nextToken();
				
				ins.setIn1(next);				
				next = tokenizer.nextToken(); // =>
				next = tokenizer.nextToken();
				
				ins.setOut1(next);
				next = tokenizer.nextToken();
				
				ins.setRegOffset(next);
				ins.latency = 5;
			}else if("addI".equals(next)){
				
			    ins = new Instruction(next);
				next = tokenizer.nextToken();
				
				ins.setIn1(next);				
				next = tokenizer.nextToken(); // =>
				next = tokenizer.nextToken();
				
				ins.setOut1(next);
				next = tokenizer.nextToken();
				
				ins.setRegOffset(next);
				ins.latency = 1;
			}
			
			instructions.add(ins);
		}
	}

}

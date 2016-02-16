package app;

public class Instruction implements Comparable{
	
	//Delay()
	int latency;
	
	//Priority in array
	int priority = 0;

	int offset = 0;
	int constant = 0;
	int schedule;
	String in1;
	String in2;
	String out;
	String regOffset;
	String type;
	int inputIndex = 1;
	String instructionString;
	
	boolean isLeaf = false;
	boolean isRoot = false;
	
	
	public Instruction(String type){
		this.type = type;
		instructionString = type;
	}
	
	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public String getRegOffset() {
		return regOffset;
	}

	public void setRegOffset(String regOffset) {
		this.regOffset = regOffset;
	}

	public int getLatency() {
		return latency;
	}

	public void setLatency(int latency) {
		this.latency = latency;
	}

	public int getConst() {
		return constant;
	}

	public void setConst(int Const) {
		this.constant = Const;
	}

	public String getIn1() {
		return in1;
	}

	public void setIn1(String in1) {
		this.in1 = in1;
	}

	public String getIn2() {
		return in2;
	}

	public void setIn2(String in2) {
		this.in2 = in2;
	}

	public String getOut() {
		return out;
	}

	public void setOut(String out) {
		this.out = out;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "[" + inputIndex + "] (" + latency + ") " + instructionString;
	}

	
	//Compareto is used for heap, compares based on priority
	@Override
	public int compareTo(Object o) {
		Instruction that = (Instruction) o;
		return (this.priority - that.priority);
	}
	
	
}

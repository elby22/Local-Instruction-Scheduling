
public class Instruction {
	
	int latency;
	int offset;
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

	int memConst = 0;
	String in1;
	String in2;
	String out1;
	String regOffset;
	String type;
	String instructionString;
	
	public Instruction(String type){
		this.type = type;
		instructionString = type;
	}

	public int getLatency() {
		return latency;
	}

	public void setLatency(int latency) {
		this.latency = latency;
	}

	public int getMemConst() {
		return memConst;
	}

	public void setMemConst(int memConst) {
		this.memConst = memConst;
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

	public String getOut1() {
		return out1;
	}

	public void setOut1(String out1) {
		this.out1 = out1;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}

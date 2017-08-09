package cn.standardai.tool.trafficimage;

public class AnkorPair {

	public Ankor from;

	public Ankor to;

	public int index;

	public AnkorPair(Ankor from, Ankor to, int index) {
		this.from = from;
		this.to = to;
		this.index = index;
	}

	public Ankor getFrom() {
		return from;
	}

	public void setFrom(Ankor from) {
		this.from = from;
	}

	public Ankor getTo() {
		return to;
	}

	public void setTo(Ankor to) {
		this.to = to;
	}
}
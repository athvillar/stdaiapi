package cn.standardai.node.action;

public abstract class Action {

	public enum Verb {
		establish,
		connect,
		send,
		calculate
	};

	protected Verb verb;

	public Action(Verb verb) {
		super();
		this.verb = verb;
	}

	public static Verb parse(String verb) {
		switch (verb) {
		case "establish":
			return Verb.establish;
		case "connect":
			return Verb.connect;
		case "send":
			return Verb.send;
		case "calculate":
			return Verb.calculate;
		default:
			return null;
		}
	}

	public Verb getVerb() {
		return verb;
	}
}

package cn.standardai.node.action;

public class SimpleAction extends Action {

	private String target;

	public SimpleAction(Verb verb, String target) {
		super(verb);
		this.target = target;
	}

	public String getTarget() {
		return target;
	}
}

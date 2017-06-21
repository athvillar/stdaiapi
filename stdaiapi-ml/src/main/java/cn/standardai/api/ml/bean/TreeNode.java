package cn.standardai.api.ml.bean;

public class TreeNode {

	private Integer value;

	private String op;

	private TreeNode lc;

	private TreeNode rc;

	public TreeNode() {
		super();
	}

	public TreeNode(Integer value) {
		this.value = value;
	}

	public Integer getValue() {
		if (value != null) return value;
		switch (op) {
		case "+":
			value = lc.getValue() + rc.getValue();
			break;
		case "-":
			value = lc.getValue() - rc.getValue();
			break;
		case "*":
			value = lc.getValue() * rc.getValue();
			break;
		case "/":
			value = lc.getValue() * rc.getValue();
			break;
		default:
			return null;
		}
		return value;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public void setLc(TreeNode lc) {
		this.lc = lc;
	}

	public void setRc(TreeNode rc) {
		this.rc = rc;
	}
}
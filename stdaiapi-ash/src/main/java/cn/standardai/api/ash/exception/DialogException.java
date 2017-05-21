package cn.standardai.api.ash.exception;

public class DialogException extends AshException {

	private static final long serialVersionUID = 1L;

	public String question;

	public String answerField;

	public DialogException(String msg) {
		super(msg);
	}

	public DialogException(String msg, String question, String answerField) {
		super(msg);
		this.question = question;
		this.answerField = answerField;
	}
}

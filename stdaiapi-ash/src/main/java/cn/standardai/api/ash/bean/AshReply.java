package cn.standardai.api.ash.bean;

public class AshReply {

	public enum Status { success, warn, failure };

	public Status status;

	public String display;

	public String hidden;

	public String message;
}

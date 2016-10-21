package cn.standardai.api.core.base;

import org.springframework.context.ApplicationEvent;

public class SystemEvent extends ApplicationEvent {

	private static final long serialVersionUID = -6861148899599748292L;

	public static final String STARTUP = "startup";

	public static final String SHUTDOWN = "shutdown";

	private String type;

	public SystemEvent(Object source, String type) {
		super(source);
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}

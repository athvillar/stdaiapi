package cn.standardai.api.ash.command;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import cn.standardai.api.core.bean.PropertyConfig;

public abstract class AshCommand {

	@Autowired
	public PropertyConfig propertyConfig;

	public enum Command {

		help("help"), ls("ls"), man("man");

		String command;

		private Command(String command) {
			this.command = command;
		}

		private static final Map<String, Command> mappings = new HashMap<String, Command>();

		static {
			for (Command command : values()) {
				mappings.put(command.command, command);
			}
		}

		public static Command resolve(String command) {
			return (command != null ? mappings.get(command) : null);
		}
	}

	public abstract String exec(String[] params);

	public abstract String help();

	public abstract String man();

	public static AshCommand getInstance(String commandString) {
		Command command = AshCommand.Command.resolve(commandString);
		if (command == null) return null;
		switch (command) {
		case help:
			return new AshHelp();
		case ls:
			return new AshLs();
		case man:
			return new AshMan();
		}
		return null;
	}
}

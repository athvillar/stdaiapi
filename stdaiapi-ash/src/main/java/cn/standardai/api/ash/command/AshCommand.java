package cn.standardai.api.ash.command;

import java.util.HashMap;
import java.util.Map;

public abstract class AshCommand {

	public enum Command {

		help("help"), ls("ls"), man("man");

		String command;

		private Command(String command) {
			this.command = command;
		}

		private static final Map<String, Command> mappings = new HashMap<String, Command>(5);

		static {
			for (Command command : values()) {
				mappings.put(command.command, command);
			}
		}

		public static Command resolve(String command) {
			return (command != null ? mappings.get(command) : null);
		}
	}

	public abstract String exec(String params);

	public abstract String help();

	public abstract String man();
}

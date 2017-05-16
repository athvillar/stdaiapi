package cn.standardai.api.ash.command;

import java.util.HashMap;
import java.util.Map;

public abstract class AshCommand {

	public String token;

	public enum Command {

		help("help"), ls("ls"), man("man"), rm("rm"), msg("msg"), message("message");

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

	public AshCommand(String token) {
		this.token = token;
	}

	public abstract String exec(String[] params);

	public abstract String help();

	public abstract String man();

	public String help(String msg) {
		return msg + "\n" + this.help();
	}

	public static AshCommand getInstance(String commandString, String token) {
		Command command = AshCommand.Command.resolve(commandString);
		if (command == null) return null;
		switch (command) {
		case help:
			return new AshHelp(token);
		case ls:
			return new AshLs(token);
		case man:
			return new AshMan(token);
		case msg:
			return new AshMsg(token);
		case message:
			return new AshMsg(token);
		case rm:
			return new AshRm(token);
		}
		return null;
	}
}

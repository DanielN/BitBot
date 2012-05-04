package bitbot;

import java.util.Arrays;
import java.util.Collection;


public abstract class Command {

	private final String command;
	private final String[] aliases;

	public Command(String command, String ...aliases) {
		this.command = command;
		this.aliases = aliases;
	}

	public String getCommand() {
		return command;
	}

	public Collection<String> getAliases() {
		return Arrays.asList(aliases);
	}

	public abstract String getDescription();

	public abstract String getArgumentTemplate();

	public abstract String getHelpText();

	public abstract void execute(String command, String args, User user, boolean pm, Replier replier);

}

package bitbot;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;



public class MessageHandler {

	private final char commandPrefix;
	private final Map<String, Command> commandIndex = new HashMap<String, Command>();
	private final SortedSet<Command> commands = new TreeSet<Command>(new Comparator<Command>() {
		@Override
		public int compare(Command o1, Command o2) {
			return o1.getCommand().compareTo(o2.getCommand());
		}
	});

	public MessageHandler(char commandPrefix) {
		this.commandPrefix = commandPrefix;
		addCommand(new HelpCommand());
	}

	public void addCommand(Command command) {
		commands.add(command);
		commandIndex.put(command.getCommand(), command);
		for (String alias : command.getAliases()) {
			commandIndex.put(alias, command);
		}
	}

	public void processMessage(String text, User user, boolean pm, Replier replier) {
		if (!text.isEmpty() && (pm || text.charAt(0) == commandPrefix)) {
			if (text.charAt(0) == commandPrefix) {
				text = text.substring(1);
			}
			String[] parts = text.split("\\s", 2);
			String cmd = parts[0].toLowerCase();
			String args = parts.length == 1 ? "" : parts[1];
			Command command = commandIndex.get(cmd);
			if (command != null) {
				command.execute(cmd, args, user, pm, replier);
			} else {
				replier.replyPrivately("Jag känner inte till kommandot '" + cmd + "'");
			}
		}
	}

	
	private class HelpCommand extends Command {

		public HelpCommand() {
			super("hjälp", "help");
		}

		@Override
		public void execute(String command, String args, User user, boolean pm, Replier replier) {
			if (args.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				sb.append("Jag förstår följande kommandon:\n");
				for (Command cmd : commands) {
					if (!pm) {
						sb.append(commandPrefix);
					}
					sb.append(cmd.getCommand()).append('\t').append(cmd.getDescription()).append('\n');
				}
				sb.append("För specifik hjälp skriv: ");
				if (!pm) {
					sb.append(commandPrefix);
				}
				sb.append(getCommand()).append(" <kommando>'");
				replier.replyPrivately(sb.toString());
			} else if (commandIndex.containsKey(args)) {
				Command cmd = commandIndex.get(args);
				StringBuilder sb = new StringBuilder();
				if (!pm) {
					sb.append(commandPrefix);
				}
				sb.append(cmd.getCommand());
				if (!cmd.getArgumentTemplate().isEmpty()) {
					sb.append(' ').append(cmd.getArgumentTemplate());
				}
				sb.append('\n');
				sb.append(cmd.getHelpText());
				if (!cmd.getAliases().isEmpty()) {
					sb.append("\nAlias: ").append(cmd.getAliases().toString());
				}
				replier.replyPrivately(sb.toString());
			} else {
				replier.replyPrivately("Jag känner inte till kommandot '" + args + "'");
			}
		}

		@Override
		public String getDescription() {
			return "Hjälp för kommandon";
		}

		@Override
		public String getArgumentTemplate() {
			return "[kommando]";
		}

		@Override
		public String getHelpText() {
			return ("Utan argument visas listan med kommandon.\n" +
					"Med ett kommando som argument visas specifik hjälp.");
		}

	}

}

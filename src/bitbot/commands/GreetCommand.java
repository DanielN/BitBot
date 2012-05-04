package bitbot.commands;

import bitbot.Command;
import bitbot.Replier;
import bitbot.User;


public class GreetCommand extends Command {

	public GreetCommand() {
		super("hej", "tja", "hallå", "hi", "hello");
	}

	@Override
	public void execute(String command, String args, User user, boolean pm, Replier replier) {
		replier.reply("Hej, " + user.getNick());
	}

	@Override
	public String getArgumentTemplate() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Hej, hej!";
	}

	@Override
	public String getHelpText() {
		return "Om ingen annan säger hej så är i alla fall jag trevlig =)";
	}
}

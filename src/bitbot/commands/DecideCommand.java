package bitbot.commands;

import java.util.Random;

import bitbot.Command;
import bitbot.Replier;
import bitbot.User;


public class DecideCommand extends Command {
	
	private Random rnd = new Random();

	public DecideCommand() {
		super("välj");
	}

	@Override
	public void execute(String command, String args, User user, boolean pm, Replier replier) {
		String[] val = args.split("eller", 2);
		if (val.length == 2) {
			String str = val[rnd.nextInt(2)].trim();
			if (str.endsWith("?")) {
				str = str.substring(0, str.length()-1);
			}
			replier.reply(str);
		} else {
			replier.reply(rnd.nextBoolean() ? "Ja!" : "Nej!");
		}
	}

	@Override
	public String getArgumentTemplate() {
		return "<alternativ1>[ eller <alternativ2>][?]";
	}

	@Override
	public String getDescription() {
		return "Ta ett beslut";
	}

	@Override
	public String getHelpText() {
		return ("Med bara ett alternativ blir svaret ja eller nej.\n" +
				"Med två alternativ väljs ett av dem.");
	}
}

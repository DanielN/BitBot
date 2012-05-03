package bitbot;



public class MessageHandler {

	private final char commandPrefix;

	public MessageHandler(char commandPrefix) {
		this.commandPrefix = commandPrefix;
	}

	public void processMessage(String text, User user, boolean pm, Replier replier) {
		if (!text.isEmpty() && (pm || text.charAt(0) == commandPrefix)) {
			if (text.charAt(0) == commandPrefix) {
				text = text.substring(1);
			}
			String[] parts = text.split("\\s", 2);
			String cmd = parts[0];
			String args = parts.length == 1 ? "" : parts[1];
			if (cmd.equalsIgnoreCase("hej")) {
				replier.reply("Hej, " + user.getNick());
			} else if (cmd.equalsIgnoreCase("test")) {
				for (String s : args.split("\\s")) {
					replier.replyPrivately(s);
				}
			}
		}
	}

}

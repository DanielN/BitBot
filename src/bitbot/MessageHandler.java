package bitbot;



public class MessageHandler {

	private final char commandPrefix;

	public MessageHandler() {
		commandPrefix = '!';
	}

	public void processMessage(String text, User user, boolean pm, Replier replier) {
		if (!text.isEmpty() && (pm || text.charAt(0) == commandPrefix)) {
			if (text.charAt(0) == commandPrefix) {
				text = text.substring(1);
			}
			String[] parts = text.split("\\s", 1);
			String cmd = parts[0];
			String args = parts.length == 1 ? null : parts[1];
			if (cmd.equalsIgnoreCase("hej")) {
				replier.reply("Hej, " + user.getNick());
			}
		}
	}

}

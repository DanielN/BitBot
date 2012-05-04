package bitbot;

import java.io.File;
import java.io.IOException;

import bitbot.commands.DecideCommand;
import bitbot.commands.GreetCommand;


public class BitBot {

	public static void main(String[] args) {
		try {
			BotConfig config = new BotConfig();
			config.load(new File("bitbot.properties"));
			BitBot bot = new BitBot(config);
			bot.run();
		} catch (IOException e) {
			System.out.println("Failed to load configuration: " + e);
		}
	}

	private final BotConfig config;
	private final ChatServer server;

	public BitBot(BotConfig config) {
		this.config = config;
		MessageHandler messageHandler = new MessageHandler(config.getCommandPrefix());
		messageHandler.addCommand(new GreetCommand());
		messageHandler.addCommand(new DecideCommand());
		server = new ChatServer(config.getChatServerConfig(), messageHandler);
	}
	
	public void run() {
		server.connect();
		for (RoomConfig rc : config.getRoomConfigs()) {
			server.joinRoom(rc);
		}
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			server.disconnect();
		}
	}

}

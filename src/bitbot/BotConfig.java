package bitbot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class BotConfig {

	private ChatServerConfig chatServerConfig;
	private List<RoomConfig> roomConfigs = new ArrayList<RoomConfig>();

	public BotConfig() {
	}
	
	public void setChatServerConfig(ChatServerConfig chatServerConfig) {
		this.chatServerConfig = chatServerConfig;
	}

	public ChatServerConfig getChatServerConfig() {
		return chatServerConfig;
	}
	
	public List<RoomConfig> getRoomConfigs() {
		return roomConfigs;
	}

	public void load(File file) throws IOException {
		Properties props = new Properties();
		FileInputStream in = new FileInputStream(file);
		try {
			props.load(in);
		} finally {
			in.close();
		}
		chatServerConfig = new ChatServerConfig(props);
		roomConfigs.clear();
		String rooms = props.getProperty("rooms");
		for (String room : rooms.split("\\s")) {
			roomConfigs.add(new RoomConfig(props, "room." + room));
		}
	}

}

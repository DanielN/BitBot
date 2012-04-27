package bitbot;

import java.util.Properties;

public class RoomConfig {

	private String room;
	private String nick;

	public RoomConfig(String room, String nick) {
		this.room = room;
		this.nick = nick;
	}

	public RoomConfig(Properties props, String prefix) {
		this.room = props.getProperty(prefix + ".name");
		this.nick = props.getProperty(prefix + ".nick");
	}

	public String getRoom() {
		return room;
	}

	public String getNick() {
		return nick;
	}
}
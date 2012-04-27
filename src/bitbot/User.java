package bitbot;

import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.Occupant;


public class User {

	static User fromOccupant(Occupant occupant) {
		return new User(occupant.getNick());
	}
	
	static User fromRealJID(String jid) {
		return new User(StringUtils.parseName(jid));
	}

	static User fromRoomJID(String jid) {
		return new User(StringUtils.parseResource(jid));
	}

	private final String nick;

	private User(String nick) {
		this.nick = nick;
	}

	public String getNick() {
		return nick;
	}

}

package bitbot;

import org.jivesoftware.smackx.muc.Occupant;


public class User {

	private final Occupant occupant;

	public User(Occupant occupant) {
		this.occupant = occupant;
	}

	public String getNick() {
		return occupant.getNick();
	}

}

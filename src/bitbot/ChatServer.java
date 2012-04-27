package bitbot;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.Occupant;


public class ChatServer {
	
	private final List<Room> rooms = new ArrayList<Room>();
	private final ChatServerConfig config;
	private final MessageHandler messageHandler;

	private Connection connection;

	public ChatServer(ChatServerConfig config, MessageHandler messageHandler) {
		this.config = config;
		this.messageHandler = messageHandler;
	}
	
	public void connect() {
		connection = new XMPPConnection(config.getServer());
		try {
			connection.connect();
			System.out.println("Connected to " + connection.getServiceName());
			connection.login(config.getUsername(), config.getPassword(), config.getResource());
			System.out.println("Logged in as " + connection.getUser());
		} catch (XMPPException e) {
			System.out.println("Failed to connect to " + config.getServer() + ": " + e);
		}
	}

	public void disconnect() {
		connection.disconnect();
		connection = null;
	}

	public void joinRoom(RoomConfig roomConfig) {
		try {
			rooms.add(new Room(roomConfig));
		} catch (XMPPException e) {
			System.out.println("Failed to join " + roomConfig.getRoom() + ": " + e);
		}
	}
	
	private class Room implements PacketListener {
		
		private MultiUserChat muc;

		public Room(RoomConfig roomConfig) throws XMPPException {
			muc = new MultiUserChat(connection, roomConfig.getRoom());
			muc.addMessageListener(this);
			DiscussionHistory history = new DiscussionHistory();
			history.setMaxStanzas(0);
			muc.join(roomConfig.getNick(), null, history, SmackConfiguration.getPacketReplyTimeout());
			System.out.println("Joined " + muc.getRoom() + " as " + muc.getNickname());
		}

		@Override
		public void processPacket(Packet packet) {
			if (packet instanceof Message) {
				Message msg = (Message) packet;
				System.out.println("Room message: " + msg.getFrom() + " -> " + msg.getTo() + ": " + msg.getBody());
				Occupant occupant = muc.getOccupant(msg.getFrom());
				if (occupant == null) {
					System.out.println("Ignoring due to unknown sender!");
				} else if (occupant.getNick().equals(muc.getNickname())) {
					System.out.println("Ignoring own message");
				}
				messageHandler.processMessage(msg.getBody(), new User(occupant), false, new RoomReplier(muc, msg.getFrom()));
			} else {
				System.out.println("Unhandled packet type from room: " + packet.getClass());
			}
		}
	}

	private static class RoomReplier implements Replier {

		private final MultiUserChat muc;
		private final String user;

		public RoomReplier(MultiUserChat muc, String user) {
			this.muc = muc;
			this.user = user;
		}

		@Override
		public void reply(String text) {
			try {
				muc.sendMessage(text);
			} catch (XMPPException e) {
				System.out.println("Failed to send message to room " + muc.getRoom() + ": " + e);
			}
		}

	}

}

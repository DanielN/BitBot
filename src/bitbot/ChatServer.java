package bitbot;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.Occupant;


public class ChatServer {
	
	private final Map<String, Room> rooms = new HashMap<String, Room>();
	private final ChatServerConfig config;
	private final MessageHandler messageHandler;
	private final PrivateChats privateChats;

	private Connection connection;

	public ChatServer(ChatServerConfig config, MessageHandler messageHandler) {
		this.config = config;
		this.messageHandler = messageHandler;
		privateChats = new PrivateChats();
	}
	
	public void connect() {
		connection = new XMPPConnection(config.getServer());
		try {
			connection.connect();
			System.out.println("Connected to " + connection.getServiceName());
			connection.login(config.getUsername(), config.getPassword(), config.getResource());
			System.out.println("Logged in as " + connection.getUser());
			connection.getChatManager().addChatListener(new ChatHandler());
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
			rooms.put(roomConfig.getRoom(), new Room(roomConfig));
		} catch (XMPPException e) {
			System.out.println("Failed to join " + roomConfig.getRoom() + ": " + e);
		}
	}
	
	private final class ChatHandler implements ChatManagerListener {

		@Override
		public void chatCreated(Chat chat, boolean createdLocally) {
			if (!createdLocally) {
				Room room = rooms.get(StringUtils.parseBareAddress(chat.getParticipant()));
				if (room != null) {
					System.out.println("New room private chat by " + chat.getParticipant());
					chat.addMessageListener(room);
				} else {
					System.out.println("New private chat by " + chat.getParticipant());
					chat.addMessageListener(privateChats);
				}
			} else {
				System.out.println("Ignore chat created locally: " + chat.getParticipant());
			}
		}
	}

	private class Room implements PacketListener, MessageListener {
		
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
				messageHandler.processMessage(msg.getBody(), User.fromOccupant(occupant), false, new RoomReplier(this, muc, msg.getFrom()));
			} else {
				System.out.println("Unhandled packet type from room: " + packet.getClass());
			}
		}

		@Override
		public void processMessage(Chat chat, Message msg) {
			System.out.println("Room PM: " + chat.getParticipant() + ": " + msg.getBody());
			messageHandler.processMessage(msg.getBody(), User.fromRoomJID(chat.getParticipant()), true, new PrivateReplier(chat));
		}
	}

	private class RoomReplier implements Replier {

		private final Room room;
		private final MultiUserChat muc;
		private final String user;

		private Chat chat;

		public RoomReplier(Room room, MultiUserChat muc, String user) {
			this.room = room;
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

		@Override
		public void replyPrivately(String text) {
			try {
				if (chat == null) {
					System.out.println("Starting private chat with " + user);
					chat = muc.createPrivateChat(user, room);
				}
				System.out.println("Sending to " + chat.getParticipant() + ": " + text);
				chat.sendMessage(text);
			} catch (XMPPException e) {
				System.out.println("Failed to send message to occupant " + chat.getParticipant() + ": " + e);
			}
		}

	}

	private class PrivateChats implements MessageListener {

		public PrivateChats() {
		}

		@Override
		public void processMessage(Chat chat, Message msg) {
			System.out.println("PM: " + chat.getParticipant() + ": " + msg.getBody());
			messageHandler.processMessage(msg.getBody(), User.fromRealJID(chat.getParticipant()), true, new PrivateReplier(chat));
		}
	}

	private class PrivateReplier implements Replier {

		private final Chat chat;

		public PrivateReplier(Chat chat) {
			this.chat = chat;
		}

		@Override
		public void reply(String text) {
			try {
				System.out.println("PM to " + chat.getParticipant() + ": " + text);
				chat.sendMessage(text);
			} catch (XMPPException e) {
				System.out.println("Failed to set PM to " + chat.getParticipant() + ": " + e);
			}
		}

		@Override
		public void replyPrivately(String text) {
			reply(text);
		}

	}

}

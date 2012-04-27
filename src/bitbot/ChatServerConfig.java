package bitbot;

import java.util.Properties;

public class ChatServerConfig {

	private final String server;
	private final String username;
	private final String password;
	private final String resource;

	public ChatServerConfig(String server, String username, String password, String resource) {
		this.server = server;
		this.username = username;
		this.password = password;
		this.resource = resource;
	}

	public ChatServerConfig(Properties props) {
		this.server = props.getProperty("server.name");
		this.username = props.getProperty("server.username");
		this.password = props.getProperty("server.password");
		this.resource = props.getProperty("server.resource");
	}

	public String getServer() {
		return server;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getResource() {
		return resource;
	}
}
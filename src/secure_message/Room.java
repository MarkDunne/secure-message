package secure_message;

import java.io.Serializable;

import messages.RoomMessage.TextMessage;
import connectors.RoomConnector;

public class Room implements Serializable {
	
	private final String name;
	private RoomConnector roomConnector;
	private static final long serialVersionUID = 7965805336448973934L;

	public Room(String name) {
		this.name = name;
	}

	public void attachClient(Client client) {
		roomConnector = new RoomConnector(name, client);
	}
	
	public void sendTextMessage(String message) {
		roomConnector.sendMessage(new TextMessage(message.getBytes()));
	}
	
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}

package secure_message;

import java.io.Serializable;

import messages.RoomMessage.TextMessage;
import connectors.RoomConnector;
import connectors.RoomConnector.RoomOutputListener;

public class Room implements Serializable {
	private static final long serialVersionUID = 7965805336448973934L;
	private RoomConnector roomConnector;

	private final String name;

	public Room(String name) {
		this.name = name;
	}

	public void attachClient(RoomOutputListener roomOutputListener) {
		roomConnector = new RoomConnector(name, roomOutputListener);
	}

	public void sendTextMessage(String message) {
		roomConnector.sendMessage(new TextMessage(message));
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}

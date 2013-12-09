package secure_message;

import connectors.ClientCoreConnector;
import connectors.ClientCoreConnector.CoreRoomReplyListener;
import connectors.RoomConnector.RoomOutputListener;

public class Client implements RoomOutputListener {

	private Room chatRoom;
	private final ClientCoreConnector clientCoreConnector;

	public Client() {
		clientCoreConnector = new ClientCoreConnector();
		clientCoreConnector.getRoom("chat_room_1", new CoreRoomReplyListener() {
			@Override
			public void onCoreRoomReply(Room room) {
				chatRoom = room;
				chatRoom.attachClient(Client.this);
				chatRoom.sendTextMessage("hello");
			}
		});
	}

	@Override
	public void onTextMessage(String message) {
		System.out.println("client received message: " + message);
	}

	public static void main(String[] args) {
		new Client();
	}
}

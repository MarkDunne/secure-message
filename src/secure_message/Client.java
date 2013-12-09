package secure_message;

import secure_message.Room.RoomMessageListener;
import secure_message.ClientCoreConnector.CoreRoomReplyListener;

public class Client implements RoomMessageListener {
	
	private Room chatRoom;
	private final ClientCoreConnector clientCoreConnector;
	
	public Client() {
		clientCoreConnector = new ClientCoreConnector();
		clientCoreConnector.getRoom("chat_room_1", new CoreRoomReplyListener() {
			@Override
			public void onCoreRoomReply(Room room) {
				chatRoom = room;
				chatRoom.join(Client.this);
				chatRoom.setMessageListener(Client.this);
			}
		});
	}
	
	public void sendMessage(String message){
		chatRoom.sendMessage(message);
	}
	
	public void onMessage(String message){
		System.out.println(message);
	}
	
	public static void main(String[] args){
		new Client();
	}
}

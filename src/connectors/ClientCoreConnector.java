package connectors;

import java.util.ArrayList;

import messages.CoreMessage.GetRoomReply;
import messages.CoreMessage.GetRoomRequest;
import secure_message.Room;

public class ClientCoreConnector extends CoreConnector {

	private final ArrayList<String> waitingOnRoomReplies;

	public interface CoreRoomReplyListener {
		public void onCoreRoomReply(Room room);
	}

	private CoreRoomReplyListener clientCoreRoomReplyListener;

	public ClientCoreConnector() {
		waitingOnRoomReplies = new ArrayList<String>();
	}

	public void getRoom(String roomName, CoreRoomReplyListener getRoomReplyClient) {
		this.clientCoreRoomReplyListener = getRoomReplyClient;
		waitingOnRoomReplies.add(roomName);
		sendMessage(new GetRoomRequest(roomName));
	}

	@Override
	public void onGetRoomReply(GetRoomReply reply) {
		final Room room = reply.getRoom();
		final String roomName = room.getName();
		if (waitingOnRoomReplies.contains(roomName)) {
			waitingOnRoomReplies.remove(roomName);
			clientCoreRoomReplyListener.onCoreRoomReply(room);
		}
	}

	@Override
	public void onGetRoomRequest(GetRoomRequest request) {
	}
}

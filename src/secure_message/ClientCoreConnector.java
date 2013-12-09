package secure_message;

import java.util.ArrayList;

import javax.jms.JMSException;
import javax.jms.Topic;
import javax.naming.NamingException;

import secure_message.ServerChannelMessage.GetRoomReply;
import secure_message.ServerChannelMessage.GetRoomRequest;

public class ClientCoreConnector extends CoreConnector{
	
	private final ArrayList<String> waitingOnRoomReplies;
	
	public interface CoreRoomReplyListener {
		public void onCoreRoomReply(Room room);
	}
	
	private CoreRoomReplyListener clientCoreRoomReplyListener;
	public ClientCoreConnector() {
		waitingOnRoomReplies = new ArrayList<String>();
	}
	
	public void getRoom(String roomName, CoreRoomReplyListener getRoomReplyClient){
		this.clientCoreRoomReplyListener = getRoomReplyClient;
		waitingOnRoomReplies.add(roomName);
		sendMessage(new GetRoomRequest(roomName));
	}
	
	@Override
	public void onGetRoomReply(GetRoomReply reply) {
		final Room room = reply.getRoom();
		final String roomName = room.getName();
		System.out.println("room reply: " + room);
		System.out.println(waitingOnRoomReplies);
		if(waitingOnRoomReplies.contains(roomName)){
			waitingOnRoomReplies.remove(roomName);
			room.setup(this);
			clientCoreRoomReplyListener.onCoreRoomReply(room);
		}
	}
	
	@Override
	public void onGetRoomRequest(GetRoomRequest request) { }
}

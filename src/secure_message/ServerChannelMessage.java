package secure_message;

import java.io.Serializable;
import java.util.Enumeration;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;


public abstract class ServerChannelMessage implements Serializable {	
	private static final long serialVersionUID = 6762322970216000066L;

	public enum Type {GetRoomRequest, GetRoomReply};
	private final Type type;
	
	public interface ServerChannelListener {
		public void onGetRoomRequest(GetRoomRequest request);
		public void onGetRoomReply(GetRoomReply reply);
	}
	
	public ServerChannelMessage(Type type) {
		this.type = type;
	}
	
	public Type getType(){
		return type;
	}
	
	public static final class GetRoomRequest extends ServerChannelMessage{
		private static final long serialVersionUID = 4773630959853393312L;
		private final String roomName;
		public GetRoomRequest(String roomName) {
			super(Type.GetRoomRequest);
			this.roomName = roomName;
		}
		public String getRoomName(){
			return roomName;
		}
	}
	
	public static final class GetRoomReply extends ServerChannelMessage{
		private static final long serialVersionUID = 4075194815166674027L;
		private final Room room;
		public GetRoomReply(Room room) {
			super(Type.GetRoomReply);
			this.room = room;		
		}
		public Room getRoom() {
			return room;
		}
	}
	
	public static ObjectMessage wrap(ServerChannelMessage serverChannelMessage, Session session){
		try {
			return session.createObjectMessage(serverChannelMessage);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void unwrap(Message message, ServerChannelListener serverChannelListener){
		if(!(message instanceof ObjectMessage))
			return;
		Object messagePayload = null;
		
		try {
			messagePayload = ((ObjectMessage) message).getObject();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
		if(!(messagePayload instanceof ServerChannelMessage))
			return;
		
		final ServerChannelMessage serverChannelMessage = (ServerChannelMessage) messagePayload;
		switch (serverChannelMessage.getType()) {
			case GetRoomRequest:
				serverChannelListener.onGetRoomRequest((GetRoomRequest) serverChannelMessage);
				break;
			case GetRoomReply:
				serverChannelListener.onGetRoomReply((GetRoomReply) serverChannelMessage);
				break;
			default:
				break;
		}
	}
}

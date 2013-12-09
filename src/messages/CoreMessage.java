package messages;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import secure_message.Room;

public abstract class CoreMessage implements Serializable {
	private static final long serialVersionUID = 6762322970216000066L;

	public enum Type {
		GetRoomRequest, GetRoomReply
	};

	private final Type type;

	public interface CoreMessageListener {
		public void onGetRoomRequest(GetRoomRequest request);

		public void onGetRoomReply(GetRoomReply reply);
	}

	public CoreMessage(Type type) {
		this.type = type;
	}

	public Type getType() {
		return type;
	}

	public static final class GetRoomRequest extends CoreMessage {
		private static final long serialVersionUID = 4773630959853393312L;
		private final String roomName;

		public GetRoomRequest(String roomName) {
			super(Type.GetRoomRequest);
			this.roomName = roomName;
		}

		public String getRoomName() {
			return roomName;
		}
	}

	public static final class GetRoomReply extends CoreMessage {
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

	public static ObjectMessage wrap(CoreMessage coreMessage, Session session) {
		try {
			return session.createObjectMessage(coreMessage);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void unwrap(Message message,
			CoreMessageListener coreMessageListener) {
		if (!(message instanceof ObjectMessage))
			return;
		Object messagePayload = null;

		try {
			messagePayload = ((ObjectMessage) message).getObject();
		} catch (JMSException e) {
			e.printStackTrace();
		}

		if (!(messagePayload instanceof CoreMessage))
			return;

		final CoreMessage coreMessage = (CoreMessage) messagePayload;
		switch (coreMessage.getType()) {
		case GetRoomRequest:
			coreMessageListener.onGetRoomRequest((GetRoomRequest) coreMessage);
			break;
		case GetRoomReply:
			coreMessageListener.onGetRoomReply((GetRoomReply) coreMessage);
			break;
		default:
			break;
		}
	}
}

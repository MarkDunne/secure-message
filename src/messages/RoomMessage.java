package messages;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

public class RoomMessage implements Serializable {
	private static final long serialVersionUID = 6762322970216000066L;

	public enum Type {
		TextMessage
	};

	private final Type type;

	public interface RoomMessageListener {
		public void onTextMessage(TextMessage message);
	}

	public RoomMessage(Type type) {
		this.type = type;
	}

	public Type getType() {
		return type;
	}

	public static final class TextMessage extends RoomMessage {
		private static final long serialVersionUID = 4976460046239042843L;
		private final String content;

		public TextMessage(String content) {
			super(Type.TextMessage);
			this.content = content;
		}

		public String getContent() {
			return content;
		}
	}

	public static ObjectMessage wrap(RoomMessage roomMessage, Session session) {
		try {
			return session.createObjectMessage(roomMessage);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void unwrap(Message message,
			RoomMessageListener roomMessageListener) {
		if (!(message instanceof ObjectMessage))
			return;
		Object messagePayload = null;

		try {
			messagePayload = ((ObjectMessage) message).getObject();
		} catch (JMSException e) {
			e.printStackTrace();
		}

		if (!(messagePayload instanceof RoomMessage))
			return;

		final RoomMessage roomMessage = (RoomMessage) messagePayload;
		switch (roomMessage.getType()) {
		case TextMessage:
			roomMessageListener.onTextMessage((TextMessage) roomMessage);
			break;
		}
	}
}

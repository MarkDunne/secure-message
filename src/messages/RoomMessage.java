package messages;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import secure_message.Room;

public class RoomMessage implements Serializable {
	private static final long serialVersionUID = 6762322970216000066L;

	public enum Type {
		TextMessage, PartialsPackageRequest, PartialsPackageReply, NewPartialsPackage
	};

	private final Type type;

	public interface RoomMessageListener {
		public void onTextMessage(TextMessage message);
		public void onPartialsPackageRequest(PartialsPackageRequest partialsPackageRequest);
		public void onPartialsPackageReply(PartialsPackageReply partialsPackageReply);
		public void onNewPartialsPackage(NewPartialsPackage newPartialsPackage);
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

	public static final class PartialsPackageRequest extends RoomMessage{

		private static final long serialVersionUID = 5167834235520194642L;
		public PartialsPackageRequest() {
			super(Type.PartialsPackageRequest);
		}	
	}
	
	public static final class PartialsPackageReply extends RoomMessage{

		private static final long serialVersionUID = 3481299120912932942L;
		public PartialsPackageReply() {
			super(Type.PartialsPackageReply);
		}	
	}
	
	public static final class NewPartialsPackage extends RoomMessage{
		private static final long serialVersionUID = 7891429912001210342L;
		public NewPartialsPackage() {
			super(Type.NewPartialsPackage);
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
		case PartialsPackageRequest:
			roomMessageListener.onPartialsPackageRequest((PartialsPackageRequest) roomMessage);
			break;
		case PartialsPackageReply:
			roomMessageListener.onPartialsPackageReply((PartialsPackageReply) roomMessage);
			break;
		case NewPartialsPackage:
			roomMessageListener.onNewPartialsPackage((NewPartialsPackage) roomMessage);
			break;
		}
	}
}

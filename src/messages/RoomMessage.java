package messages;

import java.io.Serializable;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import secure_message.DistributedDHPartial;

public abstract class RoomMessage implements Serializable {
	private static final long serialVersionUID = 6762322970216000066L;

	public static abstract class RoomUserMessage extends RoomMessage{
		private static final long serialVersionUID = 3920292935322079450L;
		public RoomUserMessage(Type type) {
			super(type);
		}
		public abstract byte[] getContent();
		public abstract void setContent(byte[] content);
	}
	
	public static abstract class RoomManagementMessage extends RoomMessage{
		private static final long serialVersionUID = 3920292935322079450L;
		public RoomManagementMessage(Type type) {
			super(type);
		}
	}
	
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

	public static final class TextMessage extends RoomUserMessage {
		private static final long serialVersionUID = 4976460046239042843L;
		private byte[] content;

		public TextMessage(byte[] content) {
			super(Type.TextMessage);
			this.content = content;
		}
		public byte[] getContent() {
			return content;
		}
		public void setContent(byte[] content){
			this.content = content;
		}
	}

	public static final class PartialsPackageRequest extends RoomManagementMessage{

		private static final long serialVersionUID = 5167834235520194642L;
		public PartialsPackageRequest() {
			super(Type.PartialsPackageRequest);
		}	
	}
	
	public static final class PartialsPackageReply extends RoomManagementMessage{

		private static final long serialVersionUID = 3481299120912932942L;
		private final List<DistributedDHPartial> partials;
		public PartialsPackageReply(List<DistributedDHPartial> partials) {
			super(Type.PartialsPackageReply);
			this.partials = partials;
		}	
		public List<DistributedDHPartial> getPartials() {
			return partials;
		}
	}
	
	public static final class NewPartialsPackage extends RoomManagementMessage{
		private static final long serialVersionUID = 7891429912001210342L;
		private final List<DistributedDHPartial> partials;
		public NewPartialsPackage(List<DistributedDHPartial> partials) {
			super(Type.NewPartialsPackage);
			this.partials = partials;
		}	
		public List<DistributedDHPartial> getPartials() {
			return partials;
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

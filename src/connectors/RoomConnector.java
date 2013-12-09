package connectors;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import messages.RoomMessage;
import messages.RoomMessage.RoomMessageListener;
import messages.RoomMessage.TextMessage;

public class RoomConnector extends Connector implements MessageListener,
		RoomMessageListener {

	public interface RoomOutputListener {
		public void onTextMessage(String message);
	}

	private final RoomOutputListener roomOutputListener;

	public RoomConnector(String roomName, RoomOutputListener roomOutputListener) {
		super(roomName);
		this.roomOutputListener = roomOutputListener;
	}

	@Override
	public void onMessage(Message message) {
		RoomMessage.unwrap(message, this);
	}

	public void sendMessage(RoomMessage message) {
		try {
			final ObjectMessage objMessage = RoomMessage.wrap(message, getSession());
			getPublisher().send(objMessage);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTextMessage(TextMessage message) {
		// decrypt

		// send to end user
		roomOutputListener.onTextMessage(message.getContent());
	}
}

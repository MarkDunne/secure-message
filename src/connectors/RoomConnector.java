package connectors;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import messages.RoomMessage;
import messages.RoomMessage.NewPartialsPackage;
import messages.RoomMessage.PartialsPackageReply;
import messages.RoomMessage.PartialsPackageRequest;
import messages.RoomMessage.RoomMessageListener;
import messages.RoomMessage.TextMessage;

public class RoomConnector extends Connector implements MessageListener, RoomMessageListener {

	public interface RoomOutputListener {
		public void onTextMessage(String message);
	}
	
	public interface RoomManagementListener {
		public void onPartialsPackageRequest(PartialsPackageRequest partialsPackageRequest);
		public void onPartialsPackageReply(PartialsPackageReply partialsPackageReply);
		public void onNewPartialsPackage(NewPartialsPackage newPartialsPackage);
	}

	private final RoomOutputListener roomOutputListener;
	private final RoomManagementListener roomManagementListener;
	
	public RoomConnector(String roomName, RoomOutputListener roomOutputListener, RoomManagementListener roomManagementListener) {
		super(roomName);
		this.roomOutputListener = roomOutputListener;
		this.roomManagementListener = roomManagementListener;
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

	@Override
	public void onPartialsPackageRequest(PartialsPackageRequest partialsPackageRequest) {
		roomManagementListener.onPartialsPackageRequest(partialsPackageRequest);
	}

	@Override
	public void onPartialsPackageReply(PartialsPackageReply partialsPackageReply) {
		roomManagementListener.onPartialsPackageReply(partialsPackageReply);
	}

	@Override
	public void onNewPartialsPackage(NewPartialsPackage newPartialsPackage) {
		roomManagementListener.onNewPartialsPackage(newPartialsPackage);
	}
}

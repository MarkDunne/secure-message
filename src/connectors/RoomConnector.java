package connectors;

import java.util.LinkedList;
import java.util.Queue;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import messages.RoomMessage;
import messages.RoomMessage.NewPartialsPackage;
import messages.RoomMessage.PartialsPackageReply;
import messages.RoomMessage.PartialsPackageRequest;
import messages.RoomMessage.RoomManagementMessage;
import messages.RoomMessage.RoomMessageListener;
import messages.RoomMessage.RoomUserMessage;
import messages.RoomMessage.TextMessage;
import secure_message.MessageCrypto;
import distributed_diffe_hellman.DistributedDH;

public class RoomConnector extends Connector implements MessageListener, RoomMessageListener {

	public interface RoomOutputListener {
		public void onTextMessage(String message);
	}

	private boolean communationSafe;
	private final Queue<RoomUserMessage> messageQueue;
	private final DistributedDH distributedDH;
	private final MessageCrypto messageCrypto;
	private final RoomOutputListener roomOutputListener;
	
	
	public RoomConnector(String roomName, boolean isEmpty, RoomOutputListener roomOutputListener) {
		super(roomName);
		this.communationSafe = false;
		this.roomOutputListener = roomOutputListener;	
		this.messageQueue = new LinkedList<RoomUserMessage>();	
		messageCrypto = new MessageCrypto();
		distributedDH = new DistributedDH(this);
		distributedDH.addClientToRoom(isEmpty);
	}

	@Override
	public void onMessage(Message message) {
		RoomMessage.unwrap(message, this);
		messageCrypto.updateIV();
	}

	public void sendMessage(RoomUserMessage message){
		try {
			if(communationSafe){
				message = messageCrypto.encrypt(message);
				final ObjectMessage objMessage = RoomMessage.wrap(message, getSession());
				getPublisher().send(objMessage);
			}else{
				messageQueue.add(message);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}	
	}
	
	public void sendMessage(RoomManagementMessage message){
		try {
			final ObjectMessage objMessage = RoomMessage.wrap(message, getSession());
			getPublisher().send(objMessage);
		} catch (JMSException e) {
			e.printStackTrace();
		}	
	}

	@Override
	public void onTextMessage(TextMessage message) {
		message = messageCrypto.decrypt(message);
		String decryptedMessage = new String(message.getContent());
		roomOutputListener.onTextMessage(decryptedMessage);
	}

	@Override
	public void onPartialsPackageRequest(PartialsPackageRequest partialsPackageRequest) {
		distributedDH.onPartialsPackageRequest(partialsPackageRequest);
	}

	@Override
	public void onPartialsPackageReply(PartialsPackageReply partialsPackageReply) {
		distributedDH.onPartialsPackageReply(partialsPackageReply);
	}

	@Override
	public void onNewPartialsPackage(NewPartialsPackage newPartialsPackage) {
		distributedDH.onNewPartialsPackage(newPartialsPackage);
	}
	
	public void setCommunicationSafe(boolean communicationSafe){
		this.communationSafe = communicationSafe;
		if(communicationSafe){
			messageCrypto.setKey(distributedDH.getSharedKey());
			while(!messageQueue.isEmpty()){
				sendMessage(messageQueue.remove());
			}
		}
	}
}

package connectors;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
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
import secure_message.DistributedDH;

public class RoomConnector extends Connector implements MessageListener, RoomMessageListener {

	public interface RoomOutputListener {
		public void onTextMessage(String message);
	}
	
	private final static BigInteger INIT_AESIV = new BigInteger("76547383930716012190582141440001525853");
	
	private Cipher AEScipher;
	private SecretKeySpec AESKey;
	private BigInteger AESIV;
	private IvParameterSpec AESIVParameterSpec;

	private boolean communationSafe;
	private final Queue<RoomUserMessage> messageQueue;
	private final DistributedDH distributedDH;
	private final RoomOutputListener roomOutputListener;
	
	
	public RoomConnector(String roomName, RoomOutputListener roomOutputListener) {
		super(roomName);
		this.communationSafe = false;
		this.roomOutputListener = roomOutputListener;	
		this.messageQueue = new LinkedList<RoomUserMessage>();		
		try {
			AESIV = INIT_AESIV; 
			AEScipher = Cipher.getInstance("AES/CTR/PKCS5Padding");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}
		distributedDH = new DistributedDH(this);
		distributedDH.addClientToRoom();
	}

	@Override
	public void onMessage(Message message) {
		RoomMessage.unwrap(message, this);
		AESIV.add(BigInteger.ONE);
		AESIVParameterSpec = new IvParameterSpec(AESIV.toByteArray());
	}

	public void sendMessage(RoomUserMessage message){
		try {
			if(communationSafe){
				message = encrypt(message);
				final ObjectMessage objMessage = RoomMessage.wrap(message, getSession());
				getPublisher().send(objMessage);
			}else{
				messageQueue.add(message);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}	
	}
	
	public RoomUserMessage encrypt(RoomUserMessage message){
		try {
			AEScipher.init(Cipher.ENCRYPT_MODE, AESKey, AESIVParameterSpec);
			final byte[] encryptedContent = AEScipher.doFinal(message.getContent());
			message.setContent(encryptedContent);
			return message;
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public <T extends RoomUserMessage> T decrypt(T message){
		try {
			AEScipher.init(Cipher.DECRYPT_MODE, AESKey, AESIVParameterSpec);
			final byte[] encryptedContent = AEScipher.doFinal(message.getContent());
			message.setContent(encryptedContent);
			return message;
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}	
		return null;
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
		message = decrypt(message);
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
			AESIV = new BigInteger("76547383930716012190582141440001525853");
			AESKey = new SecretKeySpec(distributedDH.getSharedKey().toByteArray(), "AES");
			AESIVParameterSpec = new IvParameterSpec(AESIV.toByteArray());
			while(!messageQueue.isEmpty()){
				sendMessage(messageQueue.remove());
			}
		}
	}
}

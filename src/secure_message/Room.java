package secure_message;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Topic;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.NamingException;

public class Room implements Serializable, MessageListener{
	private static final long serialVersionUID = 7965805336448973934L;
	private RoomConnector roomConnector;
	public interface RoomMessageListener{
		public void onMessage(String message);
	}
	
	private final String name;
	private RoomMessageListener roomMessageListener;
	
	public Room(String name) {
		this.name = name;
	}
	
	public boolean join(Client client){
		return true;
	}

	@Override
	public void onMessage(Message message) {
		roomMessageListener.onMessage("hello");
	}
	
	public void sendMessage(String message){
		roomPublisher.send(arg0);
	}
	
	public String getName() {
		return name;
	}
	public void setMessageListener(RoomMessageListener roomMessageListener) {
		this.roomMessageListener = roomMessageListener;
	}
	
	@Override
	public String toString() {
		return name;
	}

	public void setup(ClientCoreConnector clientCoreConnector) {
		Room
	}
}

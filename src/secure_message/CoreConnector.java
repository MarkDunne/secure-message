package secure_message;

import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import secure_message.ServerChannelMessage.ServerChannelListener;

public abstract class CoreConnector implements MessageListener, ServerChannelListener {

	private static final String CORE_SERVER_URL = "tcp://localhost:3035/";
	private static final String CORE_CHANNEL_NAME = "core_channel";
	
	private TopicSession session;
	private InitialContext context;
	private TopicPublisher coreChannelPublisher;
	private TopicSubscriber coreChannelSubscriber;
	
	public CoreConnector() {
		connectToServerChannel();
	}
	
	private void connectToServerChannel(){
		final Hashtable<String, String> properties = new Hashtable<String, String>();
		properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.exolab.jms.jndi.InitialContextFactory");
		properties.put(Context.PROVIDER_URL, CORE_SERVER_URL);
		try{
			context = new InitialContext(properties);
			final TopicConnectionFactory connectionFactory = (TopicConnectionFactory) context.lookup("ConnectionFactory");
			final TopicConnection connection = connectionFactory.createTopicConnection();
			final Topic coreChannel = (Topic) context.lookup(CORE_CHANNEL_NAME);
			connection.start();
			session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			coreChannelPublisher = session.createPublisher(coreChannel);
			coreChannelSubscriber = session.createSubscriber(coreChannel);
			coreChannelSubscriber.setMessageListener(this);
		}catch(JMSException | NamingException e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void onMessage(Message message) {
		ServerChannelMessage.unwrap(message, this);
	}
	
	public void sendMessage(ServerChannelMessage message){	
		try {
			final ObjectMessage objMessage = ServerChannelMessage.wrap(message, session);
			coreChannelPublisher.send(objMessage);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	protected TopicSession getSession(){
		return session;
	}
	
	protected InitialContext getContext(){
		return context;
	}
	
	public static String getCoreServerURL(){
		return CORE_SERVER_URL;
	}
	
	public static String getCoreChannelName(){
		return CORE_CHANNEL_NAME;
	}
}

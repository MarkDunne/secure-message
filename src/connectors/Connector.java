package connectors;

import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.MessageListener;
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

public abstract class Connector implements MessageListener {

	private TopicSession session;
	private InitialContext context;
	private TopicPublisher channelPublisher;
	private TopicSubscriber channelSubscriber;

	private final String channelName;
	private static final String CORE_SERVER_URL = "tcp://localhost:3035/";

	public Connector(String channelName) {
		this.channelName = channelName;
		final Hashtable<String, String> properties = new Hashtable<String, String>();
		properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.exolab.jms.jndi.InitialContextFactory");
		properties.put(Context.PROVIDER_URL, CORE_SERVER_URL);
		try {
			context = new InitialContext(properties);
			final TopicConnectionFactory connectionFactory = (TopicConnectionFactory) context.lookup("ConnectionFactory");
			final TopicConnection connection = connectionFactory.createTopicConnection();
			final Topic coreChannel = (Topic) context.lookup(channelName);
			connection.start();
			session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			channelPublisher = session.createPublisher(coreChannel);
			channelSubscriber = session.createSubscriber(coreChannel);
			channelSubscriber.setMessageListener(this);
		} catch (JMSException | NamingException e) {
			e.printStackTrace();
		}
	}

	public static String getCoreServerURL() {
		return CORE_SERVER_URL;
	}

	public String getChannelName() {
		return channelName;
	}

	public TopicPublisher getPublisher() {
		return channelPublisher;
	}

	public TopicSession getSession() {
		return session;
	}
}

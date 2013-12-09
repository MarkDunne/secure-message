package connectors;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import messages.CoreMessage;
import messages.CoreMessage.CoreMessageListener;

public abstract class CoreConnector extends Connector implements
		CoreMessageListener {

	private static final String CORE_CHANNEL_NAME = "core_channel";

	public CoreConnector() {
		super(CORE_CHANNEL_NAME);
	}

	@Override
	public void onMessage(Message message) {
		CoreMessage.unwrap(message, this);
	}

	public void sendMessage(CoreMessage message) {
		try {
			final ObjectMessage objMessage = CoreMessage.wrap(message,
					getSession());
			getPublisher().send(objMessage);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}

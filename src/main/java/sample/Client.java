package sample;

import java.util.Random;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class Client implements MessageListener {
	private JmsTemplate jmsTemplate;
	private Destination responseDestination;
	Random rnd = new Random();
	Logger LOG = LoggerFactory.getLogger(Client.class);

	public JmsTemplate getJmsTemplate() {
		return jmsTemplate;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public Destination getResponseDestination() {
		return responseDestination;
	}

	public void setResponseDestination(Destination responseDestination) {
		this.responseDestination = responseDestination;
	}

	@Override
	public void onMessage(Message message) {
		if (message instanceof TextMessage) {
			TextMessage tMessage = (TextMessage) message;
			try {
				String request = tMessage.getText();
				// Proccess the request
				Thread.sleep(rnd.nextInt(3000));
				final String response = "Response to: " + request;
				getJmsTemplate().send(getResponseDestination(), new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						TextMessage m = session.createTextMessage();
						m.setText(response);
						return m;
					}
				});
			} catch (JMSException e) {
				LOG.error("Can't process response message: " + e.getMessage());
				e.printStackTrace();
			} catch (InterruptedException e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
		} else {
			LOG.error("Unknown message received by client.");
		}

	}

}

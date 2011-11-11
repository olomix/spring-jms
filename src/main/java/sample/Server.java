package sample;

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

public class Server implements MessageListener{
	
	private JmsTemplate jmsTemplate;
	private int messagesNum = 10;
	private Destination requestDestination;
	Logger LOG = LoggerFactory.getLogger(Server.class);
	
	public Server() {}
	
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}
	
	public JmsTemplate getJmsTemplate() {
		return jmsTemplate;
	}
	
	public void setMessagesNum(int num) {
		messagesNum = num;
	}
	
	public int getMessagesNum() {
		return messagesNum;
	}
	
	public void setRequestDestination(Destination d) {
		requestDestination = d;
	}
	
	public Destination getRequestDestination() {
		return requestDestination;
	}
	
	public void sendMessages() {
		for (int i = 0; i < messagesNum; i++) {
			final String text = "Message #" + i;
			jmsTemplate.send(getRequestDestination(), new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					TextMessage m = session.createTextMessage();
					m.setText(text);
					return m;
				}
			});
			LOG.info("Message sent: " + text);
		}
	}

	@Override
	public void onMessage(Message message) {
		if(message instanceof TextMessage) {
			TextMessage tMessage = (TextMessage) message;
			try {
				LOG.info("Response message received by server: " + tMessage.getText());
			} catch (JMSException e) {
				LOG.error("Can't process response message: " + e.getMessage());
				e.printStackTrace();
			}
		} else {
			LOG.info("Unknown message received by server.");
		}
	}

}

package au.com.mountain_pass.chronicler.activemq;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import au.com.mountain_pass.chronicler.MessageBroker;

@Component
public class ActiveMQMessageBroker implements MessageBroker {
	Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private BrokerService broker;

	@Autowired
	@Qualifier("jmsFactory")
	ConnectionFactory jmsConnectionFactory;

	@Override
	public void sendTo(String clientId, final String msg,
			final String destination) throws JMSException {
		Assert.assertNotNull(msg);
		Assert.assertNotNull(destination);

		Connection conn = getConnection(clientId);

		Destination dest = new ActiveMQQueue(destination);
		Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

		MessageProducer producer = session.createProducer(dest);
		producer.send(session.createTextMessage(msg));
	}

	private Map<String, Connection> connections = new HashMap<>();

	private MessageConsumer consumer;

	private Connection getConnection(String clientId) throws JMSException {
		Connection rval = connections.get(clientId);
		if (rval == null) {
			rval = jmsConnectionFactory.createConnection();
			rval.setClientID(clientId);
			rval.start();
			connections.put(clientId, rval);
		}
		return rval;
	}

	@Override
	public void addListener(String clientId, String destination)
			throws JMSException {
		Connection conn = getConnection(clientId);

		Destination dest = new ActiveMQQueue(destination);
		Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		consumer = session.createConsumer(dest);
		consumer.setMessageListener(new MessageListener() {

			@Override
			public void onMessage(Message message) {
				try {
					LOGGER.info("received message: {}",
							((TextMessage) message).getText());
				} catch (JMSException e) {
					LOGGER.error("could not receive message", e);
				}
			}
		});
		conn.start();
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub

	}
}

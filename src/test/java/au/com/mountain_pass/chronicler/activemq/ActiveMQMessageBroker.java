package au.com.mountain_pass.chronicler.activemq;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import au.com.mountain_pass.chronicler.MessageBroker;

@Component
public class ActiveMQMessageBroker implements MessageBroker {

	@Autowired
	private BrokerService broker;

	@Autowired
	private JmsTemplate jmsTemplate;

	@Override
	public void sendTo(final String msg, final String dest) throws JMSException {
		Assert.assertNotNull(msg);
		Assert.assertNotNull(dest);

		jmsTemplate.send(dest, new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(msg);
			}
		});
	}
}

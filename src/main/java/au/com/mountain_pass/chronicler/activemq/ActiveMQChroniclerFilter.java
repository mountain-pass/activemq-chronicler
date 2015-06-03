package au.com.mountain_pass.chronicler.activemq;

import java.io.IOException;

import javax.jms.TextMessage;

import net.openhft.chronicle.Chronicle;
import net.openhft.chronicle.ChronicleQueueBuilder;
import net.openhft.chronicle.ExcerptAppender;
import net.openhft.lang.model.DataValueClasses;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerFilter;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.command.Message;

public class ActiveMQChroniclerFilter extends BrokerFilter {

	private Chronicle chronicle;
	private ExcerptAppender appender;

	public ActiveMQChroniclerFilter(Broker next, String basePath)
			throws IOException {
		super(next);
		chronicle = ChronicleQueueBuilder.indexed(basePath).synchronous(true)
				.build();

		appender = chronicle.createAppender();

		appender.startExcerpt();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.activemq.broker.BrokerFilter#send(org.apache.activemq.broker
	 * .ProducerBrokerExchange, org.apache.activemq.command.Message)
	 */
	@Override
	public void send(ProducerBrokerExchange producerExchange, Message msg)
			throws Exception {

		final ActiveMQEvent event = DataValueClasses
				.newDirectInstance(ActiveMQEvent.class);

		event.setState(ActiveMQEvent.CAPTURED);
		event.setType(ActiveMQEvent.SEND);
		event.setDestination(msg.getDestination().getQualifiedName());
		event.setMessage(((TextMessage) msg).getText());
		appender.startExcerpt(event.maxSize());
		appender.write(event);
		appender.finish();

		super.send(producerExchange, msg);
	}

	public void shutdown() throws IOException {
		appender.close();
		chronicle.close();
	}

}

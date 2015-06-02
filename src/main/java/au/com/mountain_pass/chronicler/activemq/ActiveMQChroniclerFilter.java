package au.com.mountain_pass.chronicler.activemq;

import java.io.File;
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

import com.fasterxml.jackson.databind.ObjectMapper;

public class ActiveMQChroniclerFilter extends BrokerFilter {

	private Chronicle chronicle;
	private ExcerptAppender appender;
	private ObjectMapper objectMapper;

	public ActiveMQChroniclerFilter(Broker next, ObjectMapper objectMapper)
			throws IOException {
		super(next);
		this.objectMapper = objectMapper;
		File.createTempFile("activemq", "chronicle");
		String basePath = System.getProperty("java.io.tmpdir")
				+ "/getting-startedXXY";
		File base = new File(basePath);
		String[] list = base.list();
		base.deleteOnExit();
		chronicle = ChronicleQueueBuilder.indexed(base).synchronous(true)
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

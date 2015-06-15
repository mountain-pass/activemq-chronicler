package au.com.mountain_pass.chronicler.activemq;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jms.TextMessage;

import net.openhft.chronicle.Chronicle;
import net.openhft.chronicle.ChronicleQueueBuilder;
import net.openhft.chronicle.ExcerptAppender;
import net.openhft.lang.model.DataValueClasses;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerFilter;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.ConsumerBrokerExchange;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.command.MessageDispatch;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class ActiveMQChroniclerFilter extends BrokerFilter {

	private Chronicle chronicle;
	private ExcerptAppender appender;

	private RestTemplate restTemplate = new RestTemplate();

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

		String timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
				.format(new Date());
		final ActiveMQEvent event = DataValueClasses
				.newDirectInstance(ActiveMQEvent.class);

		event.setState(ActiveMQEvent.CAPTURED);
		event.setType(ActiveMQEvent.SEND);
		event.setDestination(msg.getDestination().getQualifiedName());
		event.setMessage(((TextMessage) msg).getText());
		event.setClientId(producerExchange.getConnectionContext().getClientId());
		event.setTimestamp(timestamp);
		appender.startExcerpt(event.maxSize());
		appender.write(event);
		appender.finish();

		// send direct to analyser
		MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
		params.add("eventType", "send");
		params.add("clientId", producerExchange.getConnectionContext()
				.getClientId());
		params.add("destination", msg.getDestination().getQualifiedName());
		params.add("message", ((TextMessage) msg).getText());
		params.add("timestamp", timestamp);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		HttpEntity<?> request = new HttpEntity<>(params, headers);
		URI location = restTemplate.postForLocation(
				"http://localhost:8080/rest/events", request);

		next.send(producerExchange, msg);

		for (Subscription consumer : producerExchange.getRegionDestination()
				.getConsumers()) {
			final ActiveMQEvent recvEvent = DataValueClasses
					.newDirectInstance(ActiveMQEvent.class);

			event.setState(ActiveMQEvent.CAPTURED);
			event.setType(ActiveMQEvent.RECV);
			event.setDestination(msg.getDestination().getQualifiedName());
			event.setMessage(((TextMessage) msg).getText());
			event.setClientId(consumer.getConsumerInfo().getClientId());
			event.setTimestamp(timestamp);
			appender.startExcerpt(event.maxSize());
			appender.write(event);
			appender.finish();

			// send direct to analyser
			MultiValueMap<String, Object> params2 = new LinkedMultiValueMap<String, Object>();
			params2.add("eventType", "receive");
			params2.add("clientId", consumer.getConsumerInfo().getClientId());
			params2.add("destination", msg.getDestination().getQualifiedName());
			params2.add("message", ((TextMessage) msg).getText());
			params2.add("timestamp", timestamp);

			HttpHeaders headers2 = new HttpHeaders();
			headers2.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			HttpEntity<?> request2 = new HttpEntity<>(params2, headers2);
			URI location2 = restTemplate.postForLocation(
					"http://localhost:8080/rest/events", request2);
		}

	}

	@Override
	public void stop() throws Exception {
		appender.close();
		chronicle.close();
		super.stop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.activemq.broker.BrokerFilter#messageExpired(org.apache.activemq
	 * .broker.ConnectionContext,
	 * org.apache.activemq.broker.region.MessageReference,
	 * org.apache.activemq.broker.region.Subscription)
	 */
	@Override
	public void messageExpired(ConnectionContext context,
			MessageReference message, Subscription subscription) {
		// TODO Auto-generated method stub
		super.messageExpired(context, message, subscription);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.activemq.broker.BrokerFilter#messageConsumed(org.apache.activemq
	 * .broker.ConnectionContext,
	 * org.apache.activemq.broker.region.MessageReference)
	 */
	@Override
	public void messageConsumed(ConnectionContext context,
			MessageReference messageReference) {
		// TODO Auto-generated method stub
		super.messageConsumed(context, messageReference);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.activemq.broker.BrokerFilter#messageDelivered(org.apache.activemq
	 * .broker.ConnectionContext,
	 * org.apache.activemq.broker.region.MessageReference)
	 */
	@Override
	public void messageDelivered(ConnectionContext context,
			MessageReference messageReference) {
		// TODO Auto-generated method stub
		super.messageDelivered(context, messageReference);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.activemq.broker.BrokerFilter#messageDiscarded(org.apache.activemq
	 * .broker.ConnectionContext,
	 * org.apache.activemq.broker.region.Subscription,
	 * org.apache.activemq.broker.region.MessageReference)
	 */
	@Override
	public void messageDiscarded(ConnectionContext context, Subscription sub,
			MessageReference messageReference) {
		// TODO Auto-generated method stub
		super.messageDiscarded(context, sub, messageReference);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.activemq.broker.BrokerFilter#acknowledge(org.apache.activemq
	 * .broker.ConsumerBrokerExchange, org.apache.activemq.command.MessageAck)
	 */
	@Override
	public void acknowledge(ConsumerBrokerExchange consumerExchange,
			MessageAck ack) throws Exception {
		// final ActiveMQEvent event = DataValueClasses
		// .newDirectInstance(ActiveMQEvent.class);
		//
		// event.setState(ActiveMQEvent.CAPTURED);
		// event.setType(ActiveMQEvent.RECV);
		// event.setDestination(ack.getDestination().getQualifiedName());
		// // event.setMessage(((TextMessage) ack.get).getText());
		// event.setClientId(consumerExchange.getConnectionContext().getClientId());
		// appender.startExcerpt(event.maxSize());
		// appender.write(event);
		// appender.finish();
		next.acknowledge(consumerExchange, ack);
	}

	@Override
	public void preProcessDispatch(MessageDispatch messageDispatch) {
		next.preProcessDispatch(messageDispatch);
	}

	@Override
	public void postProcessDispatch(MessageDispatch messageDispatch) {
		next.postProcessDispatch(messageDispatch);
	}

}

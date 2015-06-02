package au.com.mountain_pass.chronicler.activemq;

import java.io.IOException;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerPlugin;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ActiveMQChroniclerPlugin implements BrokerPlugin {

	private ActiveMQChroniclerFilter activeMqChroniclerFilter;
	private ObjectMapper objectMapper;

	public ActiveMQChroniclerPlugin() {
		this.objectMapper = new ObjectMapper();
	}

	public ActiveMQChroniclerPlugin(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public Broker installPlugin(Broker broker) throws Exception {
		activeMqChroniclerFilter = new ActiveMQChroniclerFilter(broker,
				objectMapper);
		return activeMqChroniclerFilter;
	}

	public void shutdown() throws IOException {
		activeMqChroniclerFilter.shutdown();
	}

}

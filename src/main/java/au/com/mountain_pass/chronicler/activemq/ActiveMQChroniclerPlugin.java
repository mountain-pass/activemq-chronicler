package au.com.mountain_pass.chronicler.activemq;

import java.io.IOException;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerPlugin;

public class ActiveMQChroniclerPlugin implements BrokerPlugin {

	private ActiveMQChroniclerFilter activeMqChroniclerFilter;
	private String basePath = System.getProperty("java.io.tmpdir")
			+ "/activemq-chronicler";

	public ActiveMQChroniclerPlugin() {
	}

	@Override
	public Broker installPlugin(Broker broker) throws Exception {
		activeMqChroniclerFilter = new ActiveMQChroniclerFilter(broker,
				basePath);
		return activeMqChroniclerFilter;
	}

	public void shutdown() throws IOException {
		activeMqChroniclerFilter.shutdown();
	}

	/**
	 * @param basePath
	 *            the basePath to set
	 */
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

}

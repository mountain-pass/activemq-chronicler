package au.com.mountain_pass.chronicler;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import net.openhft.chronicle.Chronicle;
import net.openhft.chronicle.ChronicleQueueBuilder;
import net.openhft.chronicle.ExcerptTailer;
import net.openhft.lang.model.DataValueClasses;

import org.junit.Assert;

import au.com.mountain_pass.chronicler.activemq.ActiveMQEvent;

import com.fasterxml.jackson.databind.ObjectMapper;

import cucumber.api.PendingException;

public class Chronicler {

	private Chronicle chronicle;
	private ExcerptTailer reader;
	private ObjectMapper objectMapper = new ObjectMapper();

	public void connect() throws IOException {
		String basePath = System.getProperty("java.io.tmpdir")
				+ "/getting-startedXXY";
		chronicle = ChronicleQueueBuilder.indexed(basePath).build();
		// Obtain an ExcerptTailer
		reader = chronicle.createTailer().toEnd();
	}

	public void checkNextEventIs(Map<String, String> expectedEvent)
			throws IOException {
		final ActiveMQEvent event = DataValueClasses
				.newDirectReference(ActiveMQEvent.class);

		Assert.assertTrue("expected a new event", reader.nextIndex());
		try {
			event.bytes(reader, 0);
			if (event.compareAndSwapState(ActiveMQEvent.CAPTURED,
					ActiveMQEvent.PROCESSING)
					|| event.compareAndSwapState(ActiveMQEvent.PROCESSING,
							ActiveMQEvent.PROCESSING)) {
				for (Entry<String, String> entry : expectedEvent.entrySet()) {
					switch (entry.getKey()) {
					case "event":
						checkEventType(entry.getValue(), event.getType());
						break;
					case "desination":
						Assert.assertEquals(entry.getValue(),
								event.getDestination());
						break;
					case "msg":
						Assert.assertEquals(entry.getValue(),
								event.getMessage());
						break;
					default:
						throw new PendingException(entry.getKey());
					}
				}
				event.setState(ActiveMQEvent.PROCESSED);
			}

		} finally {
			reader.finish();
		}

	}

	private void checkEventType(String expected, int actual) {
		switch (expected) {
		case "send":
			Assert.assertEquals(ActiveMQEvent.SEND, actual);
			break;
		default:
			throw new PendingException(expected);
		}
	}

	public void disconnect() throws IOException {
		reader.close();
		chronicle.close();
	}

}

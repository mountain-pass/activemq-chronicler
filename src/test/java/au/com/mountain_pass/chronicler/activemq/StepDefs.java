package au.com.mountain_pass.chronicler.activemq;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import au.com.mountain_pass.chronicler.Chronicler;
import au.com.mountain_pass.chronicler.MessageBroker;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@ContextConfiguration(locations = "classpath:spring-beans.xml")
public class StepDefs {

	@Autowired
	MessageBroker messageBroker;

	Chronicler chronicler;

	@Before
	public void before() throws Exception {
		chronicler = new Chronicler();
		chronicler.connect();
		String basePath = System.getProperty("java.io.tmpdir")
				+ "/getting-started";
		(new File(basePath)).delete();
	}

	@After
	public void after() throws JMSException, IOException {
		messageBroker.disconnect();
		chronicler.disconnect();
	}

	@When("^\"(.*?)\" sends \"(.*?)\" to the ActiveMQ queue \"(.*?)\"$")
	public void sends_to_the_ActiveMQ_queue(String clientId, String msg,
			String destination) throws Throwable {
		messageBroker.sendTo(clientId, msg, destination);
	}

	@Then("^the following event will be chronicled$")
	public void the_following_event_will_be_chronicled(
			List<Map<String, String>> events) throws Throwable {
		for (Map<String, String> event : events) {
			chronicler.checkNextEventIs(event);
		}

	}

	@Given("^\"(.*?)\" is listenting for messages on the ActiveMQ queue \"(.*?)\"$")
	public void is_listenting_for_messages_on_the_ActiveMQ_queue(
			String clientId, String destination) throws Throwable {
		messageBroker.addListener(clientId, destination);
	}
}

package au.com.mountain_pass.chronicler.activemq;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import au.com.mountain_pass.chronicler.Chronicler;
import au.com.mountain_pass.chronicler.MessageBroker;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-beans.xml")
public class StepDefs {

	@Autowired
	MessageBroker messageBroker;

	Chronicler cronicler;

	@Before
	public void before() throws Exception {
		cronicler = new Chronicler();
		cronicler.connect();
		String basePath = System.getProperty("java.io.tmpdir")
				+ "/getting-started";
		(new File(basePath)).delete();
	}

	@After
	public void after() throws JMSException, IOException {
		// messageBroker.disconnect();
		cronicler.disconnect();
	}

	@When("^\"(.*?)\" is sent to the ActiveMQ queue \"(.*?)\"$")
	public void is_sent_to_the_ActiveMQ_queue(String msg, String destination)
			throws Throwable {
		messageBroker.sendTo(msg, destination);
	}

	@Then("^the following event will be chronicled$")
	public void the_following_event_will_be_chronicled(
			List<Map<String, String>> events) throws Throwable {
		for (Map<String, String> event : events) {
			cronicler.checkNextEventIs(event);
		}

	}
}
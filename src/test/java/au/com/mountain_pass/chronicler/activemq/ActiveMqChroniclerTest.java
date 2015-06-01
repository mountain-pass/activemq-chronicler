package au.com.mountain_pass.chronicler.activemq;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(glue = { "au.com.mountain_pass.chronicler.activemq" }, plugin = { "pretty" })
public class ActiveMqChroniclerTest {

}

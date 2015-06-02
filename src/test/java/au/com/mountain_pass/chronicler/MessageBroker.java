package au.com.mountain_pass.chronicler;

import javax.jms.JMSException;

public interface MessageBroker {

	public void sendTo(String msg, String destination) throws JMSException;

}

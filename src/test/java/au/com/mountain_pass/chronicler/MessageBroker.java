package au.com.mountain_pass.chronicler;

import javax.jms.JMSException;

public interface MessageBroker {

	public void sendTo(String clientId, String msg, String destination)
			throws JMSException;

	public void addListener(String clientId, String destination)
			throws JMSException;

	public void disconnect();

}

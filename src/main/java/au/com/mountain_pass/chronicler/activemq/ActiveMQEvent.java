package au.com.mountain_pass.chronicler.activemq;

import net.openhft.lang.model.Byteable;
import net.openhft.lang.model.constraints.MaxSize;

public interface ActiveMQEvent extends Byteable {

	public int SEND = 0;
	public int RECV = 1;

	public int CAPTURED = 0;
	public int PROCESSING = 1;
	public int PROCESSED = 2;

	boolean compareAndSwapState(int expected, int value);

	int getState();

	void setState(int state);

	public int getType();

	public void setType(int type);

	public String getDestination();

	public void setDestination(@MaxSize String destination);

	public String getMessage();

	public void setMessage(@MaxSize String msg);

	public void setClientId(@MaxSize String clientId);

	public String getClientId();

	public void setTimestamp(@MaxSize String timestamp);

	public String getTimestamp();

}

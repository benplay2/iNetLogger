package iNetLogger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/*
 * This class is to hold network connections to check.
 */

public class NetworkConnection {

	private InetAddress[] addresses;
	private int timeout = 5000; //timeout in ms

	public NetworkConnection(String networkAddress) throws UnknownHostException{
		setAddress(networkAddress);
	}

	/*
	 * Return if this NetworkConnection can be reached.
	 */
	public boolean isConnected(){
		boolean connected = false;

		for (InetAddress curAddress : getAddress()){
			try {
				if (curAddress.isReachable(getTimeout())){
					connected = true;
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return connected;
	}


	public InetAddress[] getAddress() {
		return addresses;
	}


	public void setAddress(String networkAddress) throws UnknownHostException {
		this.addresses = InetAddress.getAllByName(networkAddress);
	}


	public int getTimeout() {
		return timeout;
	}


	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}


}

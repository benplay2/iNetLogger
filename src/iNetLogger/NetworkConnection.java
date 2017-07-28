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
	private boolean prevConnected;
	private String addressString;

	public NetworkConnection(String networkAddress) throws UnknownHostException{
		setAddress(networkAddress);
		this.isConnected();
	}

	/*
	 * Return if this NetworkConnection can be reached.
	 */
	public boolean isConnected(){
		boolean connected = false;

		for (InetAddress curAddress : getAddresses()){
			try {
				if (curAddress.isReachable(getTimeout())){
					connected = true;
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.setPrevConnected(connected);
		return connected;
	}

	public boolean wasPrevConnected(){
		return this.prevConnected;
	}
	private void setPrevConnected(boolean connected){
		this.prevConnected = connected;
	}
	
	public InetAddress[] getAddresses() {
		return addresses;
	}


	public void setAddress(String networkAddress) throws UnknownHostException {
		this.setAddressString(networkAddress);
		this.addresses = InetAddress.getAllByName(networkAddress);
	}


	public int getTimeout() {
		return timeout;
	}


	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getAddressString() {
		return addressString;
	}

	private void setAddressString(String addressString) {
		this.addressString = addressString;
	}


}

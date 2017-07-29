package iNetLogger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/*
 * This class is to hold network connections to check.
 */

public class NetworkConnection {

	private InetAddress[] addresses;
	private int timeout = 2000; //timeout in ms
	private boolean prevConnected;
	private String addressString;
	private boolean hostFound;

	public NetworkConnection(String networkAddress) {
		this.setAddressString(networkAddress);
		this.isConnected();
	}

	/*
	 * Return if this NetworkConnection can be reached.
	 */
	public boolean isConnected(){
		if (!this.isHostFound()){
			if (!this.setAddresses()){
				return false;
			}
		}
		
		boolean connected = false;

		for (InetAddress curAddress : getAddresses()){
			try {
				if (curAddress.isReachable(getTimeout())){
					connected = true;
					break;
				}
			} catch (IOException e) {
				//e.printStackTrace();
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


	public boolean setAddresses() {
		try {
			this.addresses = InetAddress.getAllByName(this.getAddressString());
		} catch (UnknownHostException e) {
			//e.printStackTrace();
			//System.out.println("Unknown host");
			return false;
		}
		this.setHostFound(true);
		return true;
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

	private boolean isHostFound() {
		return hostFound;
	}

	private void setHostFound(boolean hostFound) {
		this.hostFound = hostFound;
	}


}

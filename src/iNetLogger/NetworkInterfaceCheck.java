package iNetLogger;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/*
 * This class is used to check if a network interface is
 * connected to the local network.
 */

public class NetworkInterfaceCheck {

	private Enumeration<NetworkInterface> eni;

	public NetworkInterfaceCheck(){
		try {
			setEni(NetworkInterface.getNetworkInterfaces());
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(2);
		}
	}

	/*
	 * Check if we have a connection
	 */
	public boolean isNetworkConnected(){
		//TODO: Write
		return true;
	}

	public Enumeration<NetworkInterface> getEni() {
		return eni;
	}

	public void setEni(Enumeration<NetworkInterface> eni) {
		this.eni = eni;
	}

}

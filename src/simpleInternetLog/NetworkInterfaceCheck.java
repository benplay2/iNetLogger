package simpleInternetLog;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/*
 * This class is used to check if a network interface is
 * connected to the local network.
 * 
 * Created by Ben Brust 2017
 */

public class NetworkInterfaceCheck {

	private boolean prevConnected;
	private boolean autoDeterminedAddress = false;

	private String localAddressString = "";
	private boolean hasLocalAddress;

	public NetworkInterfaceCheck(){
		this.setHasLocalAddress(false);
		this.isNetworkConnected();

	}
	public NetworkInterfaceCheck(String localAddress){
		// If provide localAddress, will determine if interface is working by connecting to localAddress
		this.setAutoDeterminedAddress(false);
		this.setLocalAddressString(localAddress);
		this.isNetworkConnected();

	}

	/*
	 * Check if we have a connection
	 */
	public boolean isNetworkConnected(){
		boolean connected = false;

		if (this.isHasLocalAddress()){
			//See if we can connect to the local address
			try {
				connected = InetAddress.getByName(this.getLocalAddressString()).isReachable(1000);
				if (!connected){
					connected = NetworkInterfaceCheck.isAddressReachable("www.google.com");//InetAddress.getByName("www.google.com").isReachable(500);
					
					if (connected){
						//We have a problem... local is not reachable but internet is
						System.out.println("Problem detected with local network address provided! (\"" + this.getLocalAddressString() + "\")");
						this.setHasLocalAddress(false);
					}
				}
			} catch (IOException e) {
				//e.printStackTrace();
				connected = false;
			}

		}
		else{// We were not provided a local address
			try {
				for (Enumeration<NetworkInterface> eni = getEni(); eni.hasMoreElements();){
					if (eni.nextElement().isUp()){
						connected = true;
						// TODO: Do I want to have something better than this?
					}
				}
			} catch (SocketException e) {
				connected = false;
			}
		}

		this.setPrevConnected(connected);
		return connected;
	}

	public static boolean isAddressReachable(String address) {
		try {
			return InetAddress.getByName(address).isReachable(500);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return false;
		}
	}
	
	private Enumeration<NetworkInterface> getEni() throws SocketException {
		return NetworkInterface.getNetworkInterfaces();
	}


	public boolean isPrevConnected() {
		return prevConnected;
	}

	private void setPrevConnected(boolean prevConnected) {
		this.prevConnected = prevConnected;
	}

	private boolean isHasLocalAddress() {
		return hasLocalAddress;
	}

	private void setHasLocalAddress(boolean hasLocalAddress) {
		this.hasLocalAddress = hasLocalAddress;
	}

	public String getLocalAddressString() {
		return localAddressString;
	}

	public void setLocalAddressString(String localAddressString) {
		if(localAddressString != null && !localAddressString.isEmpty()) {
			this.setHasLocalAddress(true);
			this.localAddressString = localAddressString;
		}
		return;
	}
	public boolean isAutoDeterminedAddress() {
		return autoDeterminedAddress;
	}
	private void setAutoDeterminedAddress(boolean autoDeterminedAddress) {
		this.autoDeterminedAddress = autoDeterminedAddress;
	}

}

package simpleInternetLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.StringTokenizer;


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
		try {
			this.setLocalAddressString(NetworkInterfaceCheck.getDefaultGatewayAddress());
			this.setAutoDeterminedAddress(true);
		} catch (DefaultAddressLookupFailException e) {
			this.setHasLocalAddress(false);
		}
		
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
		//TODO: add some stuff for automatically generated default gateway
		if (this.isHasLocalAddress()){
			//See if we can connect to the local address
			try {
				connected = InetAddress.getByName(this.getLocalAddressString()).isReachable(1000);
				if (!connected){
					//maybe add the logic to determine the local address here...
					
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
			
			//Try to generate one.... would be nice if it works
			
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
			//e.printStackTrace();
			return false;
		} catch (IOException e) {
			//e.printStackTrace();
			return false;
		}
	}
	public static String getDefaultGatewayAddress() throws DefaultAddressLookupFailException {
		boolean defTxtMatch = false;
		boolean foundLine = false;
		String gateway;
		String line;
		try {
			Process result = Runtime.getRuntime().exec("netstat -rn");

			BufferedReader output = new BufferedReader
					(new InputStreamReader(result.getInputStream()));

			line = output.readLine().trim();
			while(line != null){
				if ( line.startsWith("default") == true ) {
					defTxtMatch = true;
					foundLine = true;
					break; 
				} else if(line.startsWith("0.0.0.0")) {
					defTxtMatch = false;
					foundLine = true;
					break;
				}
				line = output.readLine().trim();
			}
		}   catch (Exception e) {
			throw new DefaultAddressLookupFailException();
		}

		if (!foundLine) {
	    	//couldn't find the line
			throw new DefaultAddressLookupFailException();
	    }

	    StringTokenizer st = new StringTokenizer( line );
	    st.nextToken();
	    if (defTxtMatch) {
	    	gateway = st.nextToken();
	    } else {
	    	st.nextToken();
	    	gateway = st.nextToken();
	    }
	    if (gateway.isEmpty()) {
	    	throw new DefaultAddressLookupFailException();
	    } else {
	    	return gateway;
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

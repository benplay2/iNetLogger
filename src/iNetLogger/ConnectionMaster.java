package iNetLogger;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import org.apache.commons.cli.*;
/*
 * This class manages NetworkConnections, checking them at the desired test rate.
 */

public class ConnectionMaster {


	private static int sampleRate = 5; // Sample rate in seconds
	private NetworkInterfaceCheck interfaceCheck;
	private LinkedList<NetworkConnection> connectionList = new LinkedList<NetworkConnection>(); //holds all connections to check
	private LogMaster logger;
	private SysNotificationManager notifMngr;

	public ConnectionMaster(){
		setInterfaceCheck(new NetworkInterfaceCheck());
		setLogger(new LogMaster());
		this.setNotifMngr(new SysNotificationManager());
	}

	public ConnectionMaster(String localInterfaceAddress){
		setInterfaceCheck(new NetworkInterfaceCheck(localInterfaceAddress));
		setLogger(new LogMaster());
		this.setNotifMngr(new SysNotificationManager());
	}
	/*
	 * Main Method.
	 */
	public static void main(String[] args){

		System.out.println("Starting iNetLogger...");
		/*
		// Command line arguments.
		Options options = new Options();

        Option input = new Option("i", "input", true, "input file path");
        //input.setRequired(true);
        options.addOption(input);

        Option output = new Option("o", "output", true, "output file");
        //output.setRequired(true);
        options.addOption(output);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
            return;
        }

        String inputFilePath = cmd.getOptionValue("input");
        String outputFilePath = cmd.getOptionValue("output");

		 */


		ConnectionMaster master = new ConnectionMaster("192.168.1.1");

		master.getConnectionList().add(new NetworkConnection("www.google.com"));
		
		long nextCheckTime = System.currentTimeMillis();
		boolean run = true;
		long pauseTime;
		long startTime;
		boolean iNetConnected;
		boolean previousInetConnected = true;
		boolean previousIfaceConnected;
		boolean tmpLastConnected;
		boolean verbose = true;
		boolean[] connectionChangedArray = new boolean[master.getConnectionList().size()];
		NetworkConnection curConnection;
		Iterator<NetworkConnection> networkConnIter;
		boolean firstRun = true;
		
		master.getLogger().logStartLogging();

		startTime = System.currentTimeMillis();
		while (run){

			pauseTime = nextCheckTime - startTime;
			if (pauseTime>0){
				try {
					Thread.sleep(pauseTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
					run = false;
					break;
				}
			}
			startTime = System.currentTimeMillis();
			previousIfaceConnected = master.getInterfaceCheck().isPrevConnected();
			
			if (master.getInterfaceCheck().isNetworkConnected()){
				if (!previousIfaceConnected){
					master.getLogger().logInterfaceConnected();
					master.getNotifMngr().displayInterfaceConnected();
					System.out.println("Interface Reconnected!");
				}
				if (firstRun){
					firstRun = false;
					previousInetConnected = false; //Want to log even if we have connected on start.
				}
				iNetConnected = false;
				Arrays.fill(connectionChangedArray, false);
				networkConnIter = master.getConnectionList().iterator();
				for (int i=0; i<master.getConnectionList().size(); i++){
					curConnection = networkConnIter.next();
					tmpLastConnected = curConnection.wasPrevConnected();
					if (curConnection.isConnected()){
						iNetConnected = true;
						
						if (!tmpLastConnected){connectionChangedArray[i] = true;}
					}
					else if(tmpLastConnected){connectionChangedArray[i] = true;}
				}
				if (iNetConnected != previousInetConnected){ //Internet connection status change
					if (iNetConnected){
						master.getLogger().logHaveInternetConnection();
						master.getNotifMngr().displayInternetConnected();
						System.out.println("Internet is Connected!");
					}
					else{
						master.getLogger().logNoInternetConnection();
						master.getNotifMngr().displayInternetNotConnected();
						System.out.println("Internet Not Connected!");
					}
				}
				else{//Internet connection status did not change
					if (Arrays.asList(connectionChangedArray).contains(true)){
						//We had an individual connection status change
						for (int i=0; i < master.getConnectionList().size(); i++){
							if (connectionChangedArray[i]){
								if (master.getConnectionList().get(i).wasPrevConnected()){
									//We have reconnected to this connection!
									if (verbose){
										master.getLogger().logConnectionFailed(master.getConnectionList().get(i).getAddressString());
										master.getNotifMngr().displayConnectionConnected(master.getConnectionList().get(i).getAddressString());
										System.out.println("The Connection'"+ master.getConnectionList().get(i).getAddressString() +"' Is Reconnected!");

									}
								}
								else {
									//We have lost connection to this connection!
									if (verbose){
										master.getLogger().logConnectionConnected(master.getConnectionList().get(i).getAddressString());
										master.getNotifMngr().displayConnectionNotConnected(master.getConnectionList().get(i).getAddressString());
										System.out.println("The Connection'"+ master.getConnectionList().get(i).getAddressString() +"' Is Disconnected!");
									}
								}
							}
						}
						
					}
				}
				previousInetConnected = iNetConnected;
			}
			else if(previousIfaceConnected){
				master.getLogger().logInterfaceNotConnected();
				master.getNotifMngr().displayInterfaceNotConnected();
				System.out.println("Interface Disconnected!");
			}

			nextCheckTime = startTime + (getSampleRate() * 1000);
		}
		master.getLogger().logStopLogging();

	}


	public static int getTimeSeconds(){
		return (int)(System.currentTimeMillis() / 1000);
	}

	public static int getSampleRate() {
		return sampleRate;
	}

	public static void setSampleRate(int sampleRate) {
		ConnectionMaster.sampleRate = sampleRate;
	}

	public LinkedList<NetworkConnection> getConnectionList() {
		return connectionList;
	}

	public NetworkInterfaceCheck getInterfaceCheck() {
		return interfaceCheck;
	}
	private void setInterfaceCheck(NetworkInterfaceCheck interfaceCheckIn){
		this.interfaceCheck = interfaceCheckIn;
	}

	public LogMaster getLogger() {
		return logger;
	}

	public void setLogger(LogMaster logger) {
		this.logger = logger;
	}

	public SysNotificationManager getNotifMngr() {
		return notifMngr;
	}

	private void setNotifMngr(SysNotificationManager notifMngr) {
		this.notifMngr = notifMngr;
	}

}

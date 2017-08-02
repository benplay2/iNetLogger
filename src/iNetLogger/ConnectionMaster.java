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


	private int sampleRate = 5; // Sample rate in seconds
	private NetworkInterfaceCheck interfaceCheck;
	private LinkedList<NetworkConnection> connectionList = new LinkedList<NetworkConnection>(); //holds all connections to check
	private LogMaster logger;
	private SysNotificationManager notifMngr;
	private boolean keepRunning = true;
	private boolean verbose = false;

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

		args = new String[] {"-l192.168.1.1","-iwww.google.com,www.yahoo.com"};

		System.out.println("Starting iNetLogger...");
		ConnectionMaster master = new ConnectionMaster();

		// Command line arguments.
		Options options = new Options();

		Option help = new Option( "help", "print this message" );
		options.addOption(help);

		Option verboseInput = new Option("v", "verbose", false, "Be verbose about connection changes");
		//output.setRequired(true);
		options.addOption(verboseInput);

		Option localAddressInput = new Option("l", "localAddress", true, "Local network address (Recommend default gateway)");
		//input.setRequired(true);
		options.addOption(localAddressInput);

		Option iNetAddressInput = new Option("i", "iNetAddress", true, "Internet address (separate multiple by comma)");
		//input.setRequired(true);
		options.addOption(iNetAddressInput);

		Option checkRateInput = new Option("r", "checkRate", true, "Connection check rate (seconds, integer)");
		//output.setRequired(true);
		options.addOption(checkRateInput);

		Option savePathInput = new Option("s", "savePath", true, "Path to save log files (default is current working directory)");
		//output.setRequired(true);
		options.addOption(savePathInput);



		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("iNetLogger", options);

			System.exit(1);
			return;
		}

		if (cmd.hasOption("help")){ 
			formatter.printHelp("iNetLogger", options); 
			System.exit(1); 
			return;
		}
		if (cmd.hasOption("verbose")){
			master.setVerbose(true);
		}
		if (cmd.hasOption("localAddress")){
			String localAddress = cmd.getOptionValue("localAddress");
			master.getInterfaceCheck().setLocalAddressString(localAddress);
			if (master.isVerbose()){
				System.out.println("Local address set to: " + localAddress);
			}
		}
		if (cmd.hasOption("iNetAddress")){
			String iNetAddress = cmd.getOptionValue("iNetAddress");
			String[] iNetAddresses = iNetAddress.split("\\s*,\\s*");
			for (String curAddressIn:iNetAddresses){
				master.getConnectionList().add(new NetworkConnection(curAddressIn));
			}
			if (master.isVerbose()){
				System.out.println("Internet addresses set to: " + Arrays.toString(iNetAddresses));
			}
		}
		else{
			String defaultInetAddress = "www.google.com";
			master.getConnectionList().add(new NetworkConnection(defaultInetAddress));
			if (master.isVerbose()){
				System.out.println("Internet addresses set to: " + defaultInetAddress);

			}
		}
		if (cmd.hasOption("checkRate")){
			String checkRate = cmd.getOptionValue("checkRate");
			master.setSampleRate(Integer.parseInt(checkRate.trim()));

		}
		if (master.isVerbose()){
			System.out.println("Checking connectivity every " + master.getSampleRate() + " seconds");
		}
		String savePath;
		if (cmd.hasOption("savePath")){
			savePath = cmd.getOptionValue("savePath");
		}
		else{
			savePath = System.getProperty("user.dir");
		}
		master.getLogger().setSavePath(savePath);
		if (master.isVerbose()){
			System.out.println("Saving log files to: " + master.getLogger().getSavePath());
		}

		long nextCheckTime = System.currentTimeMillis();
		long pauseTime;
		long startTime;
		boolean iNetConnected;
		boolean previousInetConnected = false;
		boolean previousIfaceConnected;
		boolean tmpLastConnected;

		boolean[] connectionChangedArray = new boolean[master.getConnectionList().size()];
		NetworkConnection curConnection;
		Iterator<NetworkConnection> networkConnIter;
		boolean firstRun = true;
		boolean fileWriteOK = true;

		//master.getLogger().logStartLogging();

		Runtime.getRuntime().addShutdownHook(new iNetLoggerShutdownHook(master));

		startTime = System.currentTimeMillis();
		
		while (master.isKeepRunning()){
			fileWriteOK = master.getLogger().writeQueuedEntriesToFile();
			pauseTime = nextCheckTime - startTime;
			if (pauseTime>0){
				try {
					Thread.sleep(pauseTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
					//master.endProgram();
					//run = false;
					break;
				}
			}
			startTime = System.currentTimeMillis();
			previousIfaceConnected = master.getInterfaceCheck().isPrevConnected();

			if (master.getInterfaceCheck().isNetworkConnected()){
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
				if (iNetConnected != previousInetConnected && !firstRun){ //Internet connection status change
					if (iNetConnected){
						master.notifyInternetStatus(true);
					}
					else{
						master.notifyInternetStatus(false);
					}
				}
				else if (!firstRun){//Internet connection status did not change
					if (Arrays.asList(connectionChangedArray).contains(true)){
						//We had an individual connection status change
						for (int i=0; i < master.getConnectionList().size(); i++){
							if (connectionChangedArray[i]){
								if (master.getConnectionList().get(i).wasPrevConnected()){
									//We have reconnected to this connection!
									master.notifyIndividualConnectionStatus(true, master.getConnectionList().get(i).getAddressString());
								}
								else {
									//We have lost connection to this connection!
									master.notifyIndividualConnectionStatus(false, master.getConnectionList().get(i).getAddressString());
								}
							}
						}
					}
				}
				if (firstRun){
					master.getLogger().logStartLogging(true, iNetConnected);
				}
				if (!previousIfaceConnected && !firstRun){
					master.notifyInterfaceStatus(true,iNetConnected);
				}
				previousInetConnected = iNetConnected;
				if (firstRun){
					master.getLogger().logStartLogging(true, iNetConnected);
					firstRun = false;
				}
			}
			else if(previousIfaceConnected || firstRun){
				if (firstRun){
					master.getLogger().logStartLogging(false, false);
					firstRun = false;
				}
				else{
					master.notifyInterfaceStatus(false,false);
					}
			}

			nextCheckTime = startTime + (master.getSampleRate() * 1000);
		}
		//master.getLogger().logStopLogging();

	}

	public void notifyInternetStatus(boolean internetConnected){
		if (internetConnected){
			this.getLogger().logHaveInternetConnection();
			this.getNotifMngr().displayInternetConnected();
			System.out.println("Internet is Connected!");
		}
		else{
			this.getLogger().logNoInternetConnection();
			this.getNotifMngr().displayInternetNotConnected();
			System.out.println("Internet Not Connected!");
		}

	}
	public void notifyInterfaceStatus(boolean localNetworkConnected, boolean internetConnected){
		if (localNetworkConnected){
			this.getLogger().logInterfaceConnected(internetConnected);
			this.getNotifMngr().displayInterfaceConnected();
			System.out.println("Reconnected to local network!");
		}
		else{
			this.getLogger().logInterfaceNotConnected();
			this.getNotifMngr().displayInterfaceNotConnected();
			System.out.println("Disconnected from local network!");
		}
	}

	public void notifyIndividualConnectionStatus(boolean connectionConnected,String connectionAddress){

		if (connectionConnected){
			this.getLogger().logConnectionConnected(connectionAddress);
			if (this.isVerbose()){
				this.getNotifMngr().displayConnectionConnected(connectionAddress);
				System.out.println("The Connection'"+ connectionAddress +"' Is Reconnected!");
			}

		}
		else{
			this.getLogger().logConnectionFailed(connectionAddress);
			if (this.isVerbose()){
				this.getNotifMngr().displayConnectionNotConnected(connectionAddress);
				System.out.println("The Connection'"+ connectionAddress +"' Is Disconnected!");
			}

		}
	}
	public static int getTimeSeconds(){
		return (int)(System.currentTimeMillis() / 1000);
	}

	public int getSampleRate() {
		return this.sampleRate;
	}

	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
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
	public void endProgram(){
		this.setKeepRunning(false);
		this.getLogger().logStopLogging(this.wasLocalLastConnected(),this.wasInternetLastConnected());
	}

	private boolean isKeepRunning() {
		return keepRunning;
	}

	private void setKeepRunning(boolean keepRunning) {
		this.keepRunning = keepRunning;
	}

	private boolean isVerbose() {
		return verbose;
	}

	private void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

}

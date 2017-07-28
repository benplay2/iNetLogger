package iNetLogger;

import java.net.UnknownHostException;
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

	public ConnectionMaster(){
		setInterfaceCheck(new NetworkInterfaceCheck());
		setLogger(new LogMaster());
		this.getConnectionList().add(new NetworkConnection("www.google.com"));

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






		ConnectionMaster master = new ConnectionMaster();

		long nextCheckTime = System.currentTimeMillis();
		boolean run = true;
		long pauseTime;
		long startTime;
		boolean iNetConnected;
		boolean previousInetConnected = true;
		boolean previousIfaceConnected;
		boolean tmpLastConnected;
		boolean verbose = true;

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
					System.out.println("Interface Connected!");
				}
				iNetConnected = false;
				for(NetworkConnection curConnection: master.getConnectionList()){
					tmpLastConnected = curConnection.wasPrevConnected();
					if (curConnection.isConnected()){
						iNetConnected = true;

						if (!tmpLastConnected){
							if (verbose){
								master.getLogger().logConnectionFailed(curConnection.getAddressString());
								System.out.println("This Connection Is Connected!");
							}
						}
					}
					else if(tmpLastConnected){
						if (verbose){
							master.getLogger().logConnectionConnected(curConnection.getAddressString());
							System.out.println("This Connection Not Connected!");
						}
					}
				}
				if (iNetConnected != previousInetConnected){
					if (iNetConnected){
						master.getLogger().logHaveInternetConnection();
						System.out.println("Internet is Connected!");
					}
					else{
						System.out.println("Internet Not Connected!");
						master.getLogger().logNoInternetConnection();
					}
				}
				previousInetConnected = iNetConnected;
			}
			else if(previousIfaceConnected){
				master.getLogger().logInterfaceNotConnected();
				System.out.println("Interface Not Connected!");
			}

			nextCheckTime = startTime + (getSampleRate() * 1000);
			//run=false;
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

}

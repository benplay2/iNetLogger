package iNetLogger;

import java.text.SimpleDateFormat;
import java.util.LinkedList;

public class LogMaster {

	/*
	 * This class handles multiple files where Internet status is recorded:
	 * 1. CSV file containing entries about connection changes/program starts/stops
	 * 2. CSV file containing entries about individual connection changes
	 * 3. Text file containing last time that iNetLogger was running.
	 */
	
	private LinkedList<InternetCSVEntry> internetEntries = new LinkedList<InternetCSVEntry>();
	private LinkedList<ConnectionCSVEntry> connectionEntries = new LinkedList<ConnectionCSVEntry>();
	
	private String savePath;
	
	private String internetLogFilename;
	private String connectionLogFilename;
	private String timeLogFilename;
	
	private long lastSavedTime;
	
	private boolean writeCSV(String filePath, String newLine){
		//TODO: write
		return false;
	}

	private boolean isFileCreated(String filePath){
		//TODO: write
		return false;
	}
	
	private boolean writeToInternetLog(){
		//TODO: write
		return false;
	}
	
	private boolean writeToConnectionLog(){
		//TODO: write
		return false;
	}
	private boolean writeTimeLog(){
		//TODO: write
		return false;
	}
	
	
	private void writeLastLogTime(){
		/*
		 * This method is intended to be called every once
		 * in a while to log the last time that was monitored
		 * by iNetLogger. This is in case the shutdownHook
		 * does not get called.
		 * 
		 * Maybe call every 5 minutes?
		 */
	}
	
	public void logStartLogging(){

	}

	public void logNoInternetConnection(){

	}
	public void logHaveInternetConnection(){

	}

	public void logStopLogging(){

	}

	public void logConnectionFailed(String connectionAddress){

	}
	public void logConnectionConnected(String connectionAddress){

	}

	public void logInterfaceConnected() {
		// TODO Auto-generated method stub
		
	}

	public void logInterfaceNotConnected() {
		// TODO Auto-generated method stub
		
	}

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}
	
	private LinkedList<InternetCSVEntry> getInternetEntries() {
		return internetEntries;
	}


	private LinkedList<ConnectionCSVEntry> getConnectionEntries() {
		return connectionEntries;
	}
	private static String getCSVTimestamp(){
		return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
	}


	private class InternetCSVEntry implements CSVEntry{
		private String timestamp;
		private boolean localConnected;
		private boolean internetConnected;
		private int programStatus;
		
		public InternetCSVEntry(boolean localConnected, boolean internetConnected, int programStatus){
			this.timestamp = LogMaster.getCSVTimestamp();
			this.localConnected = localConnected;
			this.internetConnected = internetConnected;
			this.programStatus = programStatus;
		}

		@Override
		public String getCSVLine() {
			return this.timestamp + "," + String.valueOf(this.localConnected) + "," + String.valueOf(this.internetConnected) + "," + String.valueOf(this.programStatus);
		}

		@Override
		public String getCSVHeader() {
			return "Timestamp,Connected to Local,Connected to Internet,Program Status (1-starting,0-normal,-1-stopping)";
		}
		
	}
	private class ConnectionCSVEntry implements CSVEntry{
		private String timestamp;
		private String address;
		private boolean connected;
		
		public ConnectionCSVEntry(String connectionAddress, boolean connected){
			this.timestamp = LogMaster.getCSVTimestamp();
			this.address = connectionAddress;
			this.connected = connected;
		}

		@Override
		public String getCSVLine() {
			return this.timestamp + "," + this.address + "," + String.valueOf(this.connected);
		}
		@Override
		public String getCSVHeader() {
			return "Timestamp,Address,Connected";
		}
		
	}
}



package iNetLogger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
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
	private int maxTimeDiffToSave; //Time after which to save last log time
	
	private int fileIOExceptionCount = 0;
	
	private int maxFileIOExceptionCount = 3; //May want to adjust. After hit this number, give up

	public String getInternetLogFullPath(){
		return new File(this.getSavePath(), this.getInternetLogFilename()).toString();
	}
	public String getConnectionLogFullPath(){
		return new File(this.getSavePath(), this.getConnectionLogFilename()).toString();
	}
	public String getTimeLogFullPath(){
		return new File(this.getSavePath(), this.getTimeLogFilename()).toString();
	}
	
	private boolean writeToInternetLog(){
		//TODO: Still need to work on IOException... and what to do after so many fails and such.
		
		Collection<InternetCSVEntry> entries = this.getInternetEntries();
		if (entries.isEmpty()){
			return true;
		}
		
		Collection<String> newLines = new LinkedList<String>();

		for (CSVEntry curEntry: entries){
			newLines.add(curEntry.getCSVLine());
		}
		
		try {
			FileUtils.appendToFilePlusReturn(this.getInternetLogFullPath(), newLines , this.getInternetEntries().get(0).getCSVHeader());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean writeToConnectionLog(){
		//TODO: write
		return false;
	}
	private boolean writeTimeLog(){
		//TODO: write
		//Use FileUtils.replaceFileContents
		return false;
	}
	
	
	public void writeLastLogTime(){
		/*
		 * This method is intended to be called every once
		 * in a while to log the last time that was monitored
		 * by iNetLogger. This is in case the shutdownHook
		 * does not get called.
		 * 
		 * Maybe call every 5 minutes?
		 */
		
	}
	
	/*
	 * This method creates a new CSV entry stating current status and
	 * that iNetLogger is starting again.
	 * 
	 * Note: also needs to check if the last log time was recorded.
	 * Check if the time difference is > maxTimeDiffToSave. If it is,
	 * log an entry for that time saying logging stopped.
	 */
	public void logStartLogging(){
		//TODO: Add a new entry to LinkedList
		writeToInternetLog();

	}

	public void logNoInternetConnection(){
		//TODO: Add a new entry to LinkedList
		writeToInternetLog();

	}
	public void logHaveInternetConnection(){
		//TODO: Add a new entry to LinkedList
		writeToInternetLog();
	}

	public void logStopLogging(){
		//TODO: Add a new entry to LinkedList
		writeToInternetLog();

	}

	public void logConnectionFailed(String connectionAddress){
		//TODO: Add a new entry to LinkedList
		writeToConnectionLog();

	}
	public void logConnectionConnected(String connectionAddress){
		//TODO: Add a new entry to LinkedList
		writeToConnectionLog();

	}

	public void logInterfaceConnected() {
		//TODO: Add a new entry to LinkedList
		// TODO Auto-generated method stub
		writeToInternetLog();
	}

	public void logInterfaceNotConnected() {
		//TODO: Add a new entry to LinkedList
		// TODO Auto-generated method stub
		writeToInternetLog();
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
	public static String getCSVTimestamp(){
		return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
	}

	public String getInternetLogFilename() {
		return internetLogFilename;
	}

	public void setInternetLogFilename(String internetLogFilename) {
		this.internetLogFilename = internetLogFilename;
	}

	public String getConnectionLogFilename() {
		return connectionLogFilename;
	}

	public void setConnectionLogFilename(String connectionLogFilename) {
		this.connectionLogFilename = connectionLogFilename;
	}

	public String getTimeLogFilename() {
		return timeLogFilename;
	}

	public void setTimeLogFilename(String timeLogFilename) {
		this.timeLogFilename = timeLogFilename;
	}



	
}



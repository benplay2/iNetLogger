package iNetLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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
	
	private long lastSavedTime = 0;
	private int maxTimeDiffToSave = 5*60; //Time (s) after which to save last log time
	
	private int fileIOExceptionCountCSV = 0;
	
	private int fileIOExceptionCountTimeLog = 0;
	
	private int maxFileIOExceptionCount = 100; //May want to adjust. After hit this number, give up
	
	/*
	 * Call this file every iteration to write any remaining data to files.
	 */
	public boolean writeQueuedEntriesToFile(){
		boolean connectionCSVSuccess = this.writeToConnectionLog();
		boolean internetCSVSuccess = this.writeToInternetLog();
		boolean timeSuccess = this.writeLastLogTime();
		
		return connectionCSVSuccess && internetCSVSuccess && timeSuccess;
	}
	
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
		return this.writeToCSVFromCSVEntryList(this.getInternetLogFullPath(), this.getInternetEntries());
	}
	
	private boolean writeToConnectionLog(){
		return this.writeToCSVFromCSVEntryList(this.getConnectionLogFullPath(), this.getConnectionEntries());
	}
	
	private boolean writeToCSVFromCSVEntryList(String filePath, AbstractList<? extends CSVEntry> entries){
		if (entries.isEmpty()){
			return true;
		}
		Collection<String> newLines = new LinkedList<String>();

		for (CSVEntry curEntry: entries){
			newLines.add(curEntry.getCSVLine());
		}
		
		boolean success = false;
		try {
			FileUtils.appendToFilePlusReturn(filePath, newLines , entries.get(0).getCSVHeader());
			success = true;
		} catch (IOException e) {
			//e.printStackTrace();
			success = false;
		}
		
		this.updateIOExceptionCountCSV(success);
		
		return success;
	}
	private void updateIOExceptionCountCSV(boolean success){
		if (success){
			this.setFileIOExceptionCountCSV(0);
		}else{
			
			int errorCount = this.incrementFileIOExceptionCountCSV();
			if (errorCount > this.getMaxFileIOExceptionCount()){
				System.out.println("Aborting due to too many failed file IO attempts.");
				System.exit(2);
			}
			
		}
	}
	
	private void updateIOExceptionCountTimeLog(boolean success){
		if (success){
			this.setFileIOExceptionCountTimeLog(0);
		}else{
			
			int errorCount = this.incrementFileIOExceptionCountTimeLog();
			if (errorCount > this.getMaxFileIOExceptionCount()){
				System.out.println("Aborting due to too many failed file IO attempts.");
				System.exit(2);
			}
			
		}
	}
	/*
	 * This method is intended to be called every once
	 * in a while to log the last time that was monitored
	 * by iNetLogger. This is in case the shutdownHook
	 * does not get called.
	 * 
	 * Will write to the file every maxTimeDiffToSave seconds.
	 */
	private boolean writeLastLogTime(){
		long curTime = System.currentTimeMillis();
		boolean success = false;
		
		if ((curTime - this.getLastSavedTime()) > this.getMaxTimeDiffToSave()){
			//Write to file
			try {
				FileUtils.replaceFileContents(this.getTimeLogFullPath(), Long.toString(curTime));
				success = true;
			} catch (IOException e) {
				//e.printStackTrace();
				success = false;
			}
			this.updateIOExceptionCountTimeLog(success);
			if (success){
				this.setLastSavedTime(curTime);
			}
		}
		return success;

	}

	/*
	 * Read the last saved log time from time log.
	 * -1 if does not exist or unable to read.
	 */
	public long readLastSavedLoggedTime(){
		BufferedReader br;
		try {
			br = FileUtils.getFileReader(this.getTimeLogFullPath());
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			//File was not created. Just return -1.
			return -1;
		};
		
		boolean success = false;
		String thisLine = null;
		try {
			thisLine = br.readLine();
			success = true;
		} catch (IOException e) {
			//e.printStackTrace();
			success = false;
		}
		
		this.updateIOExceptionCountTimeLog(success);
		
		if (thisLine != null){ //Had success and file was not empty
			return Long.valueOf(thisLine);
		}else{
			return -1;
		}
	}
	
	/*
	 * This method creates a new CSV entry stating current status and
	 * that iNetLogger is starting again.
	 * 
	 * Note: also needs to check if the last log time was recorded.
	 * Check if the time difference is > maxTimeDiffToSave. If it is,
	 * log an entry for that time saying logging stopped.
	 */
	public void logStartLogging(boolean localConnected, boolean internetConnected){
		this.getInternetEntries().add(new InternetCSVEntry(localConnected, internetConnected, 1));
		writeToInternetLog();
	}

	public void logNoInternetConnection(){
		this.getInternetEntries().add(new InternetCSVEntry(true, false, 0));
		writeToInternetLog();

	}
	public void logHaveInternetConnection(){
		this.getInternetEntries().add(new InternetCSVEntry(true, true, 0));
		writeToInternetLog();
	}

	public void logInterfaceConnected(boolean internetConnected) {
		this.getInternetEntries().add(new InternetCSVEntry(true, internetConnected, 0));
		writeToInternetLog();
	}

	public void logInterfaceNotConnected() {
		this.getInternetEntries().add(new InternetCSVEntry(false, false, 0));
		writeToInternetLog();
	}
	/*
	 * Add an entry of stop logging to the current time
	 */
	public void logStopLogging(boolean localConnected, boolean internetConnected){
		this.getInternetEntries().add(new InternetCSVEntry(localConnected, internetConnected, -1));
		writeToInternetLog();

	}
	/*
	 * Add an entry of stop logging to the stopTime provided.
	 */
	public void logStopLogging(long stopTime, boolean localConnected, boolean internetConnected){
		this.getInternetEntries().add(new InternetCSVEntry(LogMaster.getCSVTimestamp(stopTime),localConnected, internetConnected, -1));
		writeToInternetLog();
	}

	public void logConnectionFailed(String connectionAddress){
		this.getConnectionEntries().add(new ConnectionCSVEntry(connectionAddress,false));
		writeToConnectionLog();

	}
	public void logConnectionConnected(String connectionAddress){
		this.getConnectionEntries().add(new ConnectionCSVEntry(connectionAddress,true));
		writeToConnectionLog();

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
	public static String getCSVTimestamp(long timeMS){
		return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date(timeMS));
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
	private long getLastSavedTime() {
		return lastSavedTime;
	}
	private void setLastSavedTime(long lastSavedTime) {
		this.lastSavedTime = lastSavedTime;
	}
	public int getMaxTimeDiffToSave() {
		return maxTimeDiffToSave;
	}
	public void setMaxTimeDiffToSave(int maxTimeDiffToSave) {
		this.maxTimeDiffToSave = maxTimeDiffToSave;
	}
	private int getFileIOExceptionCountCSV() {
		return fileIOExceptionCountCSV;
	}
	private void setFileIOExceptionCountCSV(int fileIOExceptionCount) {
		this.fileIOExceptionCountCSV = fileIOExceptionCount;
	}
	private int incrementFileIOExceptionCountCSV(){
		this.setFileIOExceptionCountCSV(this.getFileIOExceptionCountCSV() + 1);
		return this.getFileIOExceptionCountCSV();
	}
	private int getMaxFileIOExceptionCount() {
		return maxFileIOExceptionCount;
	}
	private void setMaxFileIOExceptionCount(int maxFileIOExceptionCount) {
		this.maxFileIOExceptionCount = maxFileIOExceptionCount;
	}
	private int getFileIOExceptionCountTimeLog() {
		return fileIOExceptionCountTimeLog;
	}
	private void setFileIOExceptionCountTimeLog(int fileIOExceptionCountTimeLog) {
		this.fileIOExceptionCountTimeLog = fileIOExceptionCountTimeLog;
	}
	private int incrementFileIOExceptionCountTimeLog(){
		this.setFileIOExceptionCountTimeLog(this.getFileIOExceptionCountTimeLog() + 1);
		return this.getFileIOExceptionCountTimeLog();
	}


	
}



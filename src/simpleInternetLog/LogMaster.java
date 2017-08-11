package simpleInternetLog;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.AbstractList;
import java.util.Collection;
import java.util.LinkedList;
/*
 *
 * Created by Ben Brust 2017
 */
public class LogMaster {

	/*
	 * This class handles multiple files where Internet status is recorded:
	 * 1. CSV file containing entries about connection changes/program starts/stops
	 * 2. CSV file containing entries about individual connection changes
	 * 3. Text file containing last time that iNetLogger was running.
	 */

	public LogMaster(){
		this.setInternetLogFilename("iNetLog.csv");
		this.setConnectionLogFilename("connectionLog.csv");
		this.setTimeLogFilename("lastTimeLogged.txt");
		this.addMissingStopEntry();
	}
	public LogMaster(String internetLogFilename, String connectionLogFilename, String timeLogFilename, String appDataPath){
		this.setInternetLogFilename(internetLogFilename);
		this.setConnectionLogFilename(connectionLogFilename);
		this.setTimeLogFilename(timeLogFilename);
		this.setAppDataPath(appDataPath);
		this.addMissingStopEntry();
	}
	
	private LinkedList<InternetCSVEntry> internetEntries = new LinkedList<InternetCSVEntry>();
	private LinkedList<ConnectionCSVEntry> connectionEntries = new LinkedList<ConnectionCSVEntry>();
	
	private String savePath;
	
	private String internetLogFilename;
	private String connectionLogFilename;
	private String timeLogFilename;
	private String appDataPath;
	
	private long lastSavedTime = 0;
	private long maxTimeDiffToSave = 5*60*1000; //Time (ms) after which to save last log time
	
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
	
	public void addMissingStopEntry(){
		/*
		 * If we do have a last time, see if the last time logged in
		 * internet log is within tolerance or if an entry exists that
		 * says logging stopped.
		 * 
		 * If not, add a new entry that says logging stopped at the last time logged.
		 */
		long startTime = System.currentTimeMillis();
		BufferedReader br;
		//Get last logged time in timeLog (If file does not exist or is empty, do nothing and return)
		try {
			br = FileUtils.getFileReader(this.getTimeLogFullPath());
		} catch (FileNotFoundException e) {
			//File not created, simply return
			return;
		}
		String curLine = null;
		try {
			curLine = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error: unable to read last logged time from file. Cannot check to ensure logged last program stop.");
			try {
				br.close();
			} catch (IOException e1) {
				//e1.printStackTrace();
				return;
			}
			return;
		}
		try {
			br.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			//Not sure what to do with this error. Return? Error out?
			e1.printStackTrace();
			System.out.println("Unable to close reader. Exiting.");
			System.exit(3);
		}
		if (curLine == null){
			//Nothing was saved, just return
			return;
		}
		
		long lastLoggedTime;
		try {
			lastLoggedTime = Long.valueOf(curLine);
		} catch (NumberFormatException e){
			//Cannot read time
			return;
		}
		
		//get reader for internet logger. (If file does not exist or is empty, do nothing and return)
		try{
			br = FileUtils.getFileReader(this.getInternetLogFullPath());
		}catch (FileNotFoundException e){
			//File doesn't exist... do nothing
			return;
		}
		
		String lastLine = null;
		//Go through Internet logger, check if the last entry was shutting down and get that time
		try {
			while ((curLine = br.readLine()) != null)
			{
			    lastLine = curLine;
			}
		} catch (IOException e) {
			//e.printStackTrace();
			System.out.println("Error: Unable to read internet connection CSV. Cannot check to ensure logged last program stop.");
			return;
		}
		try {
			br.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			//Not sure what to do with this error. Return? Error out?
			e1.printStackTrace();
			System.out.println("Unable to close reader. Exiting.");
			System.exit(3);
		}
		InternetCSVEntry lastEntry = null;
		if (lastLine == null){
			//We need to add the entry.
			this.logStopLogging(lastLoggedTime, false, false); //not sure what to put for connection here...
		}else{//we have a line
			try {
				lastEntry = new InternetCSVEntry(lastLine);
			} catch (IncompatibleLineException e) {
				//e.printStackTrace();
				//Can't read the line. Could be header or something wrong...
				return;
			}
		}

		//If last entry was not shutting down, add an entry of shutting down for the last logged time and return
		if (lastEntry.isClosing()){
			this.logStopLogging(lastLoggedTime, lastEntry.isLocalConnected(), lastEntry.isInternetConnected());
			return;
		} else{
			//If the last time logged shutting down is within tolerance (maxTimeDiffToSave), do nothing and return
			if (startTime - lastEntry.getTimestamp() < this.getMaxTimeDiffToSave()){
				return;
			}else{
				//Otherwise, add an entry of shutting down for the last logged time and return
				this.logStopLogging(lastLoggedTime, lastEntry.isLocalConnected(), lastEntry.isInternetConnected());
				return;
			}
		}

		
		
		
		
	}
	
	public String getInternetLogFullPath(){
		return FileUtils.fullfile(this.getSavePath(), this.getInternetLogFilename());
	}
	public String getConnectionLogFullPath(){
		return FileUtils.fullfile(this.getSavePath(), this.getConnectionLogFilename());
	}
	public String getTimeLogFullPath(){
		return FileUtils.fullfile(this.getAppDataPath(), this.getTimeLogFilename());
	}
	
	private boolean writeToInternetLog(){
		if ( this.writeToCSVFromCSVEntryList(this.getInternetLogFullPath(), this.getInternetEntries())){
			this.getInternetEntries().clear();
			return true;
		}else{
			return false;
		}
	}
	
	private boolean writeToConnectionLog(){
		if (this.writeToCSVFromCSVEntryList(this.getConnectionLogFullPath(), this.getConnectionEntries())){
			this.getConnectionEntries().clear();
			return true;
		}
		else{
			return false;
		}
			
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
		else{
			success = true; //Not long enough time before writing again
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
		this.getInternetEntries().add(new InternetCSVEntry(stopTime,localConnected, internetConnected, -1));
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
	public long getMaxTimeDiffToSave() {
		return maxTimeDiffToSave;
	}
	public void setMaxTimeDiffToSave(long maxTimeDiffToSave) {
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
	@SuppressWarnings("unused")
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
	public String getAppDataPath() {
		return appDataPath;
	}
	public void setAppDataPath(String appDataPath) {
		this.appDataPath = appDataPath;
	}


	
}



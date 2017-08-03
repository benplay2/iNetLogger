package iNetLogger;

public class InternetCSVEntry extends CSVEntry{
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
	public InternetCSVEntry(String entryTime, boolean localConnected, boolean internetConnected, int programStatus){
		this.timestamp = entryTime;
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
		return "Timestamp,Connected to Local,Connected to Internet,Program Status (1-starting 0-normal -1-stopping)";
	}
	
}

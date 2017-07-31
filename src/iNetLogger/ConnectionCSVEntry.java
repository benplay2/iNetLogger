package iNetLogger;

public class ConnectionCSVEntry implements CSVEntry{
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

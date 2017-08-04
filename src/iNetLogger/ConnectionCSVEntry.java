package iNetLogger;
/*
 * 
 * Created by Ben Brust 2017
 */
public class ConnectionCSVEntry extends CSVEntry{
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
		String connectedStr = this.connected? "1" : "0";
		return this.timestamp + "," + this.address + "," + connectedStr;
	}
	@Override
	public String getCSVHeader() {
		return "Timestamp,Address,Connected";
	}
	
}

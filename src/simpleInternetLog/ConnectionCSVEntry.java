package simpleInternetLog;

import java.text.ParseException;

/*
 * 
 * Created by Ben Brust 2017
 */
public class ConnectionCSVEntry extends CSVEntry{
	private String address;
	private boolean connected;
	
	public ConnectionCSVEntry(String connectionAddress, boolean connected){
		super(System.currentTimeMillis());
		this.address = connectionAddress;
		this.connected = connected;
	}

	public ConnectionCSVEntry(String csvEntryLine) throws IncompatibleLineException{
		String[] entries = csvEntryLine.split(",");

		if (entries.length != 3){
			throw new IncompatibleLineException();
		}

		try {
			this.setTimestamp(CSVEntry.getTimeFromString(entries[0]));
			this.address = entries[1];
			this.connected = Integer.valueOf(entries[2]) == 1 ? true : false;
		} catch (ParseException e) {
			//e.printStackTrace();
			throw new IncompatibleLineException();
		}

	}

	@Override
	public String getCSVLine() {
		String connectedStr = this.connected? "1" : "0";
		return CSVEntry.getCSVTimestamp(this.getTimestamp()) + "," + this.address + "," + connectedStr;
	}
	@Override
	public String getCSVHeader() {
		return "Timestamp,Address,Connected";
	}
	
}

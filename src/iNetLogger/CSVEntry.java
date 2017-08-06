package iNetLogger;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/*
 * 
 * Created by Ben Brust 2017
 */
public abstract class CSVEntry {
	private long timestamp;
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	private static SimpleDateFormat dateFormatNoTime = new SimpleDateFormat("MM/dd/yyyy");
	
	public abstract String getCSVLine();
	
	public abstract String getCSVHeader();
	
	public static String getCurCSVTimestamp(){
		return CSVEntry.getCSVTimestamp(System.currentTimeMillis());
	}
	public static String getCSVTimestamp(long timeMS){
		return CSVEntry.dateFormat.format(new java.util.Date(timeMS));
	}
	
	public static long getTimeFromString(String timestamp) throws ParseException{
		if (timestamp.contains(":")){
			return CSVEntry.dateFormat.parse(timestamp).getTime();
		}else{
			return CSVEntry.dateFormatNoTime.parse(timestamp).getTime();
		}
		

	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public String getCSVTimestamp(){
		return  CSVEntry.getCSVTimestamp(this.getTimestamp());
	}

	protected void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}



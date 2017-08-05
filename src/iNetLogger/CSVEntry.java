package iNetLogger;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/*
 * 
 * Created by Ben Brust 2017
 */
public abstract class CSVEntry {
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
	public abstract String getCSVLine();
	
	public abstract String getCSVHeader();
	
	public static String getCSVTimestamp(){
		return CSVEntry.getCSVTimestamp(System.currentTimeMillis());
	}
	public static String getCSVTimestamp(long timeMS){
		return CSVEntry.dateFormat.format(new java.util.Date(timeMS));
	}
	
	public static long getTimeFromString(String timestamp) throws ParseException{
		return CSVEntry.dateFormat.parse(timestamp).getTime();

	}
}



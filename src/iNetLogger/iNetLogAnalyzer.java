package iNetLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/*
 * This class is intended to analyze a iNetLog Internet csv result and display
 * a summary of the findings to the user.
 * 
 * Created by Ben Brust 2017
 */
public class iNetLogAnalyzer {

	public static void main(String[] args){

		//args = new 	String[] {"-d08/06/2017-8/6/2017","iNetLog.csv"};

		/*
		 * First, take in some inputs
		 */
		
		// Command line arguments.
		Options options = new Options();

		Option help = new Option( "help", "print this message" );
		options.addOption(help);

		Option verboseInput = new Option("v", "verbose", false, "Be extra verbose in analysis result");
		options.addOption(verboseInput);

		Option dateRangeInput = Option.builder("d")
				.desc("date range in format MM/DD/YYYY-MM/DD/YYYY (startDate is 12am, endDate is 11:59pm)")
				.numberOfArgs(2)
				.valueSeparator('-') //Note, not sure if this separator will work
				.build();
		options.addOption(dateRangeInput);


		String helpHeader = "Analyze Internet connection log from iNetLogger.\n\n";

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("iNetLogAnalyzer [-help] [-v] File", helpHeader, options, "", false);

			System.exit(1);
			return;
		}


		if (cmd.hasOption("help")){ 
			formatter.printHelp("iNetLogAnalyzer [-help] [-v] File", helpHeader, options, "", false);
			System.exit(1); 
			return;
		}
		boolean verbose = false;
		if (cmd.hasOption("verbose")){ 
			verbose = true;
			return;
		}

		
		
		long startTime = 0;
		long endTime = System.currentTimeMillis();
		
		if (cmd.hasOption("d")){
			String[] dateRange = cmd.getOptionValues("d");
			try {
				startTime = CSVEntry.getTimeFromString(dateRange[0]); //startDate includes all of first day
				endTime = CSVEntry.getTimeFromString(dateRange[1] + " 24:00:00"); //Make endDate include all of the last day
			} catch (java.text.ParseException e) {
				e.printStackTrace();
				System.out.println("Unable to decode dates provided");
				System.exit(3);
			}
		}
		
		String fileToAnalyze;
		if (cmd.getArgs().length != 1){
			System.out.println("No file to analyze provided. Assuming default 'iNetLog.csv'");
			fileToAnalyze = "iNetLog.csv";
		} else{
			
			fileToAnalyze = cmd.getArgs()[0];
			File file = new File(fileToAnalyze);
			if (file.isDirectory()){
				System.out.println("No filename to analyze provided. Assuming default 'iNetLog.csv'");
				fileToAnalyze = FileUtils.fullfile(fileToAnalyze, "iNetLog.csv");
			}
		}
		
		String analysisTxt = null;
		try {
			analysisTxt = iNetLogAnalyzer.analyzeINetLogger(fileToAnalyze, verbose, startTime, endTime);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Unable to analyze file");
			System.exit(3);
		}
		System.out.print(analysisTxt);
	}
	public static String analyzeINetLogger(String filename, boolean verbose) throws IOException{
		return iNetLogAnalyzer.analyzeINetLogger(filename, verbose, 0, System.currentTimeMillis());
	}
	
	/*
	 * Analyze iNetLogger Internet log assuming that it is correct.
	 * 
	 * Note: Unsure what to do if the last entry is not a program ending, or -1
	 */
	public static String analyzeINetLogger(String fileToAnalyze, boolean verbose, long startTime, long endTime) throws IOException{


		// Read in file:
		BufferedReader br = null;

		br = FileUtils.getFileReader(fileToAnalyze);


		//Create list of entries
		LinkedList<InternetCSVEntry> entryList = new LinkedList<InternetCSVEntry>();
		{
			String curLine = null;
			InternetCSVEntry curEntry = null;
			InternetCSVEntry lastEntry = null;

			while ((curLine = br.readLine()) != null){
				try{
					curEntry = new InternetCSVEntry(curLine);

					if (curEntry.getTimestamp() > startTime){
						if (entryList.isEmpty() && lastEntry != null){
							entryList.add(lastEntry);
						}
						entryList.add(curEntry);
					}

					if (curEntry.getTimestamp() > endTime){
						break;
					}
					lastEntry = curEntry;
				}catch ( IncompatibleLineException e){
					//Do nothing
				}
			}
		}

		try {
			br.close();
		} catch (IOException e) {
			// Not good... but doesn't really cause any problem
			e.printStackTrace();
		}

		
		if (entryList.isEmpty()){
//			System.out.println("No valid Internet log entries found."); //Nothing to analyze
			return "No valid Internet log entries found.";
		}
		
		startTime = Math.max(startTime, entryList.getFirst().getTimestamp());
		
		/*
		 * Design decision for if missing program close entry... might want to change.
		 */
		if (!entryList.getLast().isClosing()){
			endTime = Math.min(endTime, System.currentTimeMillis());
		} else{
			endTime = Math.min(endTime, entryList.getLast().getTimestamp()); //Note that endTime may have to be adjusted in the end

		}
		
		//Analyze entryList
		long logTime = 0;
		long intDisonnectedTime = 0; //Save disconnected time. Errs on side of internet is connected
		long localDisconnectedTime = 0;
		{
			InternetCSVEntry prevEntry = null;
			long prevTime = 0;
			boolean logging = false;
			for (InternetCSVEntry curEntry : entryList){
				long curTimePast;
				if (curEntry.getTimestamp() > endTime){
					break;
				} else if (curEntry.getTimestamp() < startTime){
					curTimePast = 0;
				} else{
					curTimePast = Math.abs(curEntry.getTimestamp() - prevTime);
				}
				if (curEntry.isOpening()){
					//starting
					logging = true;
				} else if (curEntry.isClosing()){
					//Stopping
					logging = false;
					if (logging){
						if (!prevEntry.isLocalConnected()){
							localDisconnectedTime += curTimePast;
						}else if (!prevEntry.isInternetConnected()){
							//else if because we don't really know if Internet is connected or not.
							intDisonnectedTime += curTimePast;
						}
						logTime += curTimePast;
					}
				} else if(logging){
					if (!prevEntry.isLocalConnected()){
						localDisconnectedTime += curTimePast;
					} else if (!prevEntry.isInternetConnected()){
						//else if because we don't really know if Internet is connected or not.
						intDisonnectedTime += curTimePast;
					}
					logTime += curTimePast;
				} else{
					//Not logging, but status is 0, normal. Treat like program status 1, starting
					logging = true;
				}
				prevEntry = curEntry;
				if (curEntry.getTimestamp() < startTime){
					prevTime = startTime;
				} else{
					prevTime = prevEntry.getTimestamp();
				}
			}
			InternetCSVEntry curEntry = entryList.getLast();
			if (curEntry.getTimestamp() < endTime && !curEntry.isClosing()){
				//Assume that nothing has changed from then until now
				if (!curEntry.isLocalConnected()){
					localDisconnectedTime += endTime - prevTime;
				} else if (!curEntry.isInternetConnected()){
					intDisonnectedTime += endTime - prevTime;
				}
			}
		}
		if (verbose){
			System.out.println("Creating analysis from " + entryList.size() + "Internet connection entries.");
		}
		//Print the result
		
		return getAnalysisText(startTime, endTime, logTime, localDisconnectedTime, intDisonnectedTime);
	}
	public static String getAnalysisText(long startTime, long endTime, long logTime, long localDisconnectedTime, long intDisconnectedTime){
		/*
		 * Internet statistics from DATE1 to DATE2:
		 * 
		 * Logged  99.6% of time between DATE1 and DATE2
		 * 
		 * Total Time:
		 * 		Logged:		Local Connected:	Internet Connected:
		 * Days	12.6(0)		12.6(0)				12.3(0.3)
		 * Pct	100%(0%)	100%(0%)			97.6%(2.4%)
		 * 
		 * Logged Time:
		 * 		Logged:		Local Connected:	Internet Connected:
		 * Days	12.6		0					0.3
		 * Pct	100%		0%					2.4%
		 */
		
		long allTime = endTime - startTime;
		
		if (allTime <= 0){
			//System.out.println("Not enough entries to analyze");
			return "Not enough entries to analyze";
		}
		
		
		
		
		
		String totalTable = null;
		String loggedTable = null;
		
		
		long hourThreshold =  1000L * 60L * 60L * 3L; //ms after which communicate in hours
		long dayThreshold =   1000L * 60L * 60L * 24L * 3L; //ms after which communicate in days
		long monthThreshold = 1000L * 60L * 60L * 24L * 30L * 3L; //ms after which communicate in months
		long yearThreshold =  1000L * 60L * 60L * 24L * 365L * 2L; //ms after which communicate in years
		

		long curMSDivider;
		String timeUnit;
		/* Populate the tables */
		if (allTime < hourThreshold){
			//Hours
			timeUnit = "Mins";
			curMSDivider = 1000 * 60; //1 minute
		} else if (allTime < dayThreshold){
			//Hours
			timeUnit = "Hours";
			curMSDivider = 1000 * 60 * 60; //1 hour
		} else if (allTime < monthThreshold){
			//Days
			timeUnit = "Days";
			curMSDivider = 1000 * 60 * 60 * 24; //1 day
		} else if (allTime < yearThreshold) {
			//Months
			timeUnit = "Months";
			curMSDivider = (long) (1000 * 60 * 60 * 24 * 30.4375f); //1 month
		} else{
			//Years
			timeUnit = "Years";
			curMSDivider = (long) (1000 * 60 * 60 * 24 * 365.25); //1 year
		}
		
		double logPctAll = getPct(logTime,allTime);
		double intPctAll = invPct(getPct(intDisconnectedTime,allTime));
		double localPctAll = invPct(getPct(localDisconnectedTime,allTime));
		
		double intPctLog = invPct(getPct(intDisconnectedTime,logTime));
		double localPctLog = invPct(getPct(localDisconnectedTime,logTime));
		
		//String header = "Internet Statistics from " + CSVEntry.getCSVTimestamp(startTime) + " to " + CSVEntry.getCSVTimestamp(endTime) + ":";
		String header = String.format("Internet Statistics from %s to %s (%.1f %s):",
				CSVEntry.getCSVTimestamp(startTime),CSVEntry.getCSVTimestamp(endTime),
				(float)allTime/curMSDivider,timeUnit);
		
		String logPctLine = String.format("Logged %.1f%% of time between %s and %s",
				logPctAll,CSVEntry.getCSVTimestamp(startTime),CSVEntry.getCSVTimestamp(endTime));
		
		String totalHeading = "Total Time: value(inverse)";
		String loggedHeading = "Logged Time: value(inverse)";
		
		{
			String headerLine = "       Logged:           Local Connected:        Internet Connected:";
			String totalTimeTableLine = String.format("%-7s%5.1f(%5.1f)      %5.1f(%5.1f)            %5.1f(%5.1f)", 
					timeUnit,
					(float)logTime / curMSDivider, (float)(allTime - logTime)/curMSDivider,
					(float)(allTime-localDisconnectedTime)/curMSDivider, (float)(localDisconnectedTime)/curMSDivider,
					(float)(allTime-intDisconnectedTime)/curMSDivider, (float)(intDisconnectedTime)/curMSDivider);
			String totalPctTableLine = String.format("%-7s%5.1f%%(%5.1f)     %5.1f%%(%5.1f)           %5.1f%%(%5.1f)", 
					"Pct",
					logPctAll, invPct(logPctAll),
					localPctAll, invPct(localPctAll),
					intPctAll, invPct(intPctAll));
			
			totalTable = totalHeading + System.lineSeparator() +
					headerLine + System.lineSeparator() + 
					totalTimeTableLine + System.lineSeparator() +
					totalPctTableLine;
		}
		{
			String headerLine = "       Logged:           Local Connected:        Internet Connected:";
			String logPctTableLine = String.format("%-7s%5.1f%%(%5.1f)     %5.1f%%(%5.1f)           %5.1f%%(%5.1f)", 
					"Pct",
					100.0, 0.0,
					localPctLog, invPct(localPctLog),
					intPctLog, invPct(intPctLog));
			
			loggedTable = loggedHeading + System.lineSeparator() +
					headerLine + System.lineSeparator() + 
					logPctTableLine;
		}
		
		
//		System.out.println(header);
//		System.out.println();
//		System.out.println(logPctLine);
//		System.out.println();
//		System.out.println(totalTable);
//		System.out.println();
//		System.out.println(loggedTable);
		
		return header + System.lineSeparator() + System.lineSeparator() +
				logPctLine + System.lineSeparator() +
				totalTable + System.lineSeparator() + System.lineSeparator() +
				loggedTable;
		
		
	}
	
	public static long getTimestampOfFirstEntry(String filename) throws IOException{
		// Read in file:
		BufferedReader br = null;

		br = FileUtils.getFileReader(filename);
		long firstTime = 0;

		//Create list of entries
		String curLine = null;

		while ((curLine = br.readLine()) != null){
			try{
				firstTime = new InternetCSVEntry(curLine).getTimestamp();
				break;
			}catch ( IncompatibleLineException e){
				//Do nothing
			}
		}
		return firstTime;
	}

	private static double getPct(long trueTime, long allTime){
		return ((double)trueTime / allTime) * 100;
	}
	
	/*
	 * Get the inverse percentage of pct. pct is a percentage 0-100
	 */
	private static double invPct(double pct){
		return 100-pct;
	}
}

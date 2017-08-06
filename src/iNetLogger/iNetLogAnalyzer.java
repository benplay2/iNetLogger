package iNetLogger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
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

		args = new 	String[] {"iNetLog.csv"};

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
				.desc("date range in format MM/DD/YYYY HH:MM-MM/DD/YYYY HH:MM (hours and minutes optional)")
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

		if (cmd.getArgs().length != 1){
			System.out.println("File to analyze not provided.");
			System.exit(0);
			return;
		}
		if (cmd.hasOption("d")){
			//TODO: deal with date input
		}

		String fileToAnalyze = cmd.getArgs()[0];

		// Read in file:
		BufferedReader br = null;
		try {
			br = FileUtils.getFileReader(fileToAnalyze);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
			System.out.println("Unable to read " + fileToAnalyze);
			System.exit(2);
		}

		//Create list of entries
		String curLine = null;
		LinkedList<InternetCSVEntry> entryList = new LinkedList<InternetCSVEntry>();
		try {
			while ((curLine = br.readLine()) != null){
				try{
					entryList.add(new InternetCSVEntry(curLine));
				}catch ( IncompatibleLineException e){
					//Do nothing
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		try {
			br.close();
		} catch (IOException e) {
			// Not good... but doesn't really cause any problem
			e.printStackTrace();
		}

		//Analyze entryList
		System.out.println("TODO: analyze");
		


		//Print the result
		System.out.println("TODO: print analysis");
	}

}

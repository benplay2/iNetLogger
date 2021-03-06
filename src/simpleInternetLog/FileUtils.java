package simpleInternetLog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.Collection;
/*
 * 
 * Created by Ben Brust 2017
 */
public class FileUtils {

	//private static Logger logger = Logger.getLogger(FileUtils.class);
	
	public static boolean makeDirectory(String directory){
		File file = new File(directory);
		if (!file.exists()){
			return file.mkdirs();
		}
		return true;
	}
	
	/*
	 * Get a BufferedReader for a given filename.
	 */
	public static BufferedReader getFileReader(String filename) throws FileNotFoundException{
		FileReader fileReader = new FileReader(filename);

		return new BufferedReader(fileReader);
	}

	/*
	 * Append newContents to filename and add a return after
	 */
	public static void appendToFilePlusReturn(String filename, String newContents, String fileHeader) throws IOException{
		File file = new File(filename);

		boolean addHeader = false;
		if (!file.exists()){
			file.createNewFile();
			addHeader = true;
			
		}
		else if(file.length() == 0){
			addHeader = true;
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
		BufferedWriter bw = new BufferedWriter(fw);

		if (addHeader){
			bw.write(fileHeader);
			bw.newLine();
		}
		
		bw.write(newContents);
		bw.newLine();

		bw.close();

		return;
	}
	
	/*
	 * Replace filename's contents with newContents.
	 */
	public static void replaceFileContents(String filename, String newContents) throws IOException{
		File file = new File(filename);


		if (!file.exists()){
			file.createNewFile();
			
		}
		else if(file.length() == 0){
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		
		fw.write(newContents);
		fw.close();
	}

	/*
	 * Writes all String contents in newContents with a return after each one.
	 * If the file needs to be created, the fileHeader will be added to the top after creation.
	 */
	public static void appendToFilePlusReturn(String filename, Collection<String> newContents, String fileHeader) throws IOException{
		File file = new File(filename);
		
		boolean addHeader = false;
		if (!file.exists()){
			file.createNewFile();
			addHeader = true;
			
		}
		else if(file.length() == 0){
			addHeader = true;
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
		BufferedWriter bw = new BufferedWriter(fw);

		if (addHeader){
			bw.write(fileHeader);
			bw.newLine();
		}
		
		for (String curLine : newContents){
			bw.write(curLine);
			bw.newLine();
		}
		bw.close();
		return;
	}
	public static String fullfile(String pathName, String fileName){
		return new File(pathName, fileName).toString();
	}
	
	public static boolean lockInstance(final String lockFile) {
	    try {
	        final File file = new File(lockFile);
	        final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
	        final FileLock fileLock = randomAccessFile.getChannel().tryLock();
	        if (fileLock != null) {
	            Runtime.getRuntime().addShutdownHook(new Thread() {
	                public void run() {
	                    try {
	                        fileLock.release();
	                        randomAccessFile.close();
	                        file.delete();
	                    } catch (Exception e) {
	                    	e.printStackTrace();
	                        //logger.error("Unable to remove lock file: " + lockFile, e);
	                    }
	                }
	            });
	            return true;
	        }
	    } catch (Exception e) {
	    	e.printStackTrace();
	        //logger.error("Unable to create and/or lock file: " + lockFile, e);
	    }
	    return false;
	}
}











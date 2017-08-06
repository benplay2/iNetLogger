package iNetLogger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
/*
 * 
 * Created by Ben Brust 2017
 */
public class FileUtils {

	
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
}

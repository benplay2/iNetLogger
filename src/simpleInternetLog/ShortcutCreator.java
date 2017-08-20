package simpleInternetLog;

import java.io.File;
import net.jimmc.jshortcut.JShellLink;

/*
 * This is a class to create shortcuts on Windows machines.
 * 
 * Created by Ben Brust 2017.
 */

public class ShortcutCreator {
	
	
	    JShellLink link;
	    String filePath;
	    String shortcutName;

	    public ShortcutCreator(String targetLocation, String shortcutName) {
	    	
	    	this.link = new JShellLink();
	    	this.filePath = targetLocation;
	    	this.shortcutName = shortcutName;

	    }

	    
	    public void createDesktopShortcut() {
	    	this.createShortcut(JShellLink.getDirectory("desktop"));
	    }
	    public void createStartupShortcut() {
	    	this.createShortcut(JShellLink.getDirectory("programs") + File.separator + "Startup");
	    }
	    public void createStartMenuShortcut() {
	    	this.createShortcut(JShellLink.getDirectory("programs"));
	    }
	    public void createShortcut(String destinationDirectory) {
	    	JShellLink link = this.getLink();
	    	link.setName(this.getShortcutName());
	    	link.setPath(this.getFilePath());
	    	
	    	link.setFolder(destinationDirectory);
	    	link.save();
	    }
	    private JShellLink getLink() {
	    	return this.link;
	    }
	    private String getFilePath() {
	    	return this.filePath;
	    }
	    private String getShortcutName() {
	    	return this.shortcutName;
	    }
	    public static boolean canCreateShortcut() {
	    	String osName = System.getProperty("os.name");
	    	return osName.toLowerCase().contains("windows");
	    }
	
}

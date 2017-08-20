package simpleInternetLog;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import net.jimmc.jshortcut.JShellLink;

public class ShortcutCreator {
	
	
	    JShellLink link;
	    String filePath;
	    String shortcutName;

	    public ShortcutCreator(String targetLocation, String shortcutName) {
	    	
	    	this.link = new JShellLink();
	    	this.filePath = targetLocation;
	    	this.shortcutName = shortcutName;

	    }

	    public static void main(String a[]) {
	    	//ShortcutCreation sc = new ShortcutCreation();
	        //sc.createDesktopShortcut();
	    	ShortcutCreator sc = new ShortcutCreator("simpleInternetLog.jar","SimpleInternetLogger");
	    	try {
				System.out.println(new File(".").getAbsolutePath());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	        System.out.println(System.getProperty("user.dir"));
	        //System.out.println(System.getProperty("java.class.path"));
	    	System.out.println(sc.getClass().getProtectionDomain().getCodeSource().getLocation());
	    	
	    	System.out.println(JShellLink.getDirectory("programs"));
	    	
	    	
	    	File pto = null;
			try {
				pto = new File(ConnectionMaster.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	    	System.out.println(pto.getAbsolutePath());
	    	
	    	//sc.createDesktopShortcut();
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

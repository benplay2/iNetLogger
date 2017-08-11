package iNetLogger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.io.BufferedReader;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import org.apache.commons.cli.Options;

/*
 * GUI to help creating a settings startup file and maybe a shortcut... Likely only for Windows.
 */
public class CreateSettingsGUIControl {
	private String settingsFullPath;//Path to settings *.txt file
	Options options = ConnectionMaster.getCMDOptions();
	private String[] settingsText; //This holds the string[] from the settings file

	public CreateSettingsGUIControl(String settingsFullPath){
		this.setSettingsFullPath(settingsFullPath);
		
		this.createSettingsText();
	}
	//TODO: write!
	//See https://stackoverflow.com/questions/15605715/create-desktop-shortcut
	/*
	 * import net.jimmc.jshortcut.JShellLink;

public class Sc {
    JShellLink link;
    String filePath;

    public Sc() {
        try {
            link = new JShellLink();
            filePath = JShellLink.getDirectory("")
                + "C:\\Program Files\\Internet Explorer\\iexplore.exe";

        } catch (Exception e) {

        }

    }

    public void createDesktopShortcut() {

        try {
            link.setFolder(JShellLink.getDirectory("desktop"));
            link.setName("ie");
            link.setPath(filePath);
            link.save();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static void main(String a[]) {
        Sc sc = new Sc();
        sc.createDesktopShortcut();
    }
}
	 */
	
	/*
	 * Return if verbose is in the settings file. 
	 */
	public boolean getVerboseFromFile(){
		return false;//TODO: write
	}
	public String getLocalAddressFromFile(){
		return "";
		//TODO: write. If it exists, return. Otherwise return empty string
	}
	public String[] getINetAddressFromFile(){
		return new String[] {};
		//TODO: write. If it exists, return
	}
	public int getCheckRateFromFile(){
		return 0; //TODO: write. return 0 unless it is saved.
	}
	
	public String getSavePathFromFile(){
		return "";//TODO: write
	}
	public void createSettingsText(){
		
		
		String fileContents = null;
		try {
			BufferedReader br = FileUtils.getFileReader(this.getSettingsFullPath());
			fileContents = br.readLine();
		} catch (IOException e) {
			// Do nothing. Means go with hard-coded defaults
			//e.printStackTrace();
			this.setSettingsText(new String[] {});
		}
		
		if (fileContents != null){
			this.setSettingsText(fileContents.split("\\s+"));
		} else{
			this.setSettingsText(new String[] {});
		}
	}
	
	public static void createAndShowGUI(){
		JFrame mainFrame = new JFrame("iNetLogger by Ben Brust - Settings");
		mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		//mainFrame.setSize(400, 300);
		mainFrame.setIconImage(SysNotificationManager.getCurIcon());
		
		//mainFrame.getContentPane().setLayout(new BorderLayout()); //This is default.
		addComponentsToPane(mainFrame.getContentPane());
		
		mainFrame.pack(); //auto-size
		mainFrame.setVisible(true);//show

	}


	public static void addComponentsToPane(Container pane){
		pane.setLayout(new GridBagLayout());
				
	}
	public static void main(String[] args){
		//Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
		SysNotificationManager.setUILookAndFeel();
		startGUI();
	}
	
	public static void startGUI(){
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
	}

	public String getSettingsFullPath() {
		return settingsFullPath;
	}

	public void setSettingsFullPath(String settingsFullPath) {
		this.settingsFullPath = settingsFullPath;
	}

	public String[] getSettingsText() {
		return settingsText;
	}

	public void setSettingsText(String[] settingsText) {
		this.settingsText = settingsText;
	}
	
}

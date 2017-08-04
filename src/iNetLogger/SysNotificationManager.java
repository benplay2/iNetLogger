package iNetLogger;

import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/*
 * 
 * Created by Ben Brust 2017
 */
public class SysNotificationManager {

	private TrayIcon trayIcon;
	private boolean traySuppported;

	public SysNotificationManager(){
		this.setTraySuppported(SystemTray.isSupported());
		TrayIcon trayIcon = null;
		if (SystemTray.isSupported()){
			SystemTray tray = SystemTray.getSystemTray();
			Image image = Toolkit.getDefaultToolkit().createImage("icon_v3.png"); //TODO: get an icon...
			trayIcon = new TrayIcon(image,"iNetLogger");
			trayIcon.setImageAutoSize(true);
			
			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				//e.printStackTrace();
				this.setTraySuppported(false);
			}
		}
		if (this.isTraySuppported()){
			this.setTrayIcon(trayIcon);
			this.AddMenu();
		}
	}

	public static void main(String[] args) throws AWTException, java.net.MalformedURLException {
		SysNotificationManager td = new SysNotificationManager();
		td.displayInternetConnected();

	}
	private void AddMenu(){

		PopupMenu menu = new PopupMenu();

		MenuItem closeItem = new MenuItem("Close");
		closeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		menu.add(closeItem);
		
		this.getTrayIcon().setPopupMenu(menu);
	}
    public void displayTrayDemo() throws AWTException, java.net.MalformedURLException {
        //Obtain only one instance of the SystemTray object
        SystemTray tray = SystemTray.getSystemTray();

        //If the icon is a file
        Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
        //Alternative (if the icon is on the classpath):
        //Image image = Toolkit.getToolkit().createImage(getClass().getResource("icon.png"));
        TrayIcon trayIcon = new TrayIcon(image, "Tray Demo Tooltip");
        //Let the system resizes the image if needed
        trayIcon.setImageAutoSize(true);
        //Set tooltip text for the tray icon
        //trayIcon.setToolTip("System tray icon demo");
        tray.add(trayIcon);
        trayIcon.displayMessage("Hello, World", "notification demo", MessageType.INFO);
    }
    
    public void displayStartLogging(){
    	this.displayTray("Starting Logging","iNetLogger has started connection logging.");
    }
    public void displayInterfaceConnected(){
    	this.displayTray("Interface Connected!", "Now connected to local network");
    }
    public void displayInterfaceNotConnected(){
    	this.displayTray("Interface Not Connected!", "No longer connected to local network");
    }
    public void displayInternetConnected(){
    	this.displayTray("Internet Connected!", "Internet is now connected");
    }
    public void displayInternetNotConnected(){
    	this.displayTray("Internet Not Connected!", "Internet is no longer connected");
    }
    public void displayConnectionConnected(String connectionAddress){
    	this.displayTray("Connection Resumed!", "Computer is connected to \"" + connectionAddress + "\"");
    }
    public void displayConnectionNotConnected(String connectionAddress){
    	this.displayTray("Connection Lost!", "Computer no longer connected to \"" + connectionAddress + "\"");
    }
    public void displayErrorWriting(String filename){
    	this.displayTray("Unable to write to file!", "iNetLogger is unable to write to \"" + filename + "\". Close any applications using this file or iNetLogger will quit.");
    }
    public void displayErrorWriting(){
    	this.displayTray("Unable to write to file!", "iNetLogger is unable to write to at least one of the log files. Close any applications using the files or iNetLogger will quit.");
    }
    private void displayTray(String caption, String text){
    	if (!this.isTraySuppported()){
    		return;
    	}

    	TrayIcon trayIcon = this.getTrayIcon();
    	trayIcon.displayMessage(caption, text, MessageType.INFO);
    		
    }

    private TrayIcon getTrayIcon(){
    	return this.trayIcon;
    }
    
    private void setTrayIcon(TrayIcon trayIcon){
    	this.trayIcon = trayIcon;
    }
    



	private boolean isTraySuppported() {
		return traySuppported;
	}

	private void setTraySuppported(boolean traySuppported) {
		this.traySuppported = traySuppported;
	}
	
}

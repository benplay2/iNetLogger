package iNetLogger;

import java.awt.*;
import java.awt.TrayIcon.MessageType;

public class SysNotificationManager {

	
	private SystemTray tray = SystemTray.getSystemTray();
	private Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
	private boolean traySuppported;
	
	public SysNotificationManager(){
		this.setTraySuppported(SystemTray.isSupported());
	}
	
	public static void main(String[] args) throws AWTException, java.net.MalformedURLException {
		SysNotificationManager td = new SysNotificationManager();
		td.displayInternetConnected();
        
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
    
    public void displayInterfaceConnected(){
    	this.displayTray("Interface Connected!", "Now connected to local network", "iNetLogger");
    }
    public void displayInterfaceNotConnected(){
    	this.displayTray("Interface Not Connected!", "No longer connected to local network", "iNetLogger");
    }
    public void displayInternetConnected(){
    	this.displayTray("Internet Connected!", "Internet is now connected", "iNetLogger");
    }
    public void displayInternetNotConnected(){
    	this.displayTray("Internet Not Connected!", "Internet is no longer connected", "iNetLogger");
    }
    public void displayConnectionConnected(String connectionAddress){
    	this.displayTray("Connection Resumed!", "Computer is connected to \"" + connectionAddress + "\"", "iNetLogger");
    }
    public void displayConnectionNotConnected(String connectionAddress){
    	this.displayTray("Connection Lost!", "Computer no longer connected to \"" + connectionAddress + "\"", "iNetLogger");
    }
    
    
    private void displayTray(String caption, String text, String tooltip){
    	if (!this.isTraySuppported()){
    		return;
    	}
    		
    	SystemTray tray = this.getTray();
    	TrayIcon trayIcon = this.getTrayIcon(tooltip);
    	
    	try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			//e.printStackTrace();
		}
    	
    	trayIcon.displayMessage(caption, text, MessageType.INFO);
    	
    }

    private TrayIcon getTrayIcon(String tooltip){
    	Image image = this.getImage();
    	TrayIcon trayIcon = new TrayIcon(image,tooltip);
    	return trayIcon;
    }
    
	private Image getImage() {
		return image;
	}

	private void setImage(Image image) {
		this.image = image;
	}

	private SystemTray getTray() {
		return tray;
	}

	private void setTray(SystemTray tray) {
		this.tray = tray;
	}

	private boolean isTraySuppported() {
		return traySuppported;
	}

	private void setTraySuppported(boolean traySuppported) {
		this.traySuppported = traySuppported;
	}
	
}

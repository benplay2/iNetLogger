package simpleInternetLog;

import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
/*
 * 
 * Created by Ben Brust 2017
 */
public class SysNotificationManager {

	private TrayIcon trayIcon;
	private boolean traySuppported;
	private ConnectionMaster connMaster;
	private static Image curIcon;

	public SysNotificationManager(ConnectionMaster connMaster){
		this.setConnMaster(connMaster);
		this.setTraySuppported(SystemTray.isSupported());
		TrayIcon trayIcon = null;
		if (SystemTray.isSupported()){
			SystemTray tray = SystemTray.getSystemTray();
			Image image = Toolkit.getDefaultToolkit().createImage("dependentFiles" + File.separator + "icon_v6.png");
			SysNotificationManager.setCurIcon(image);
			trayIcon = new TrayIcon(image,"simpleInternetLog");
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
			this.addOnClick();
			this.addMenu();
		}
	}

	/*
	 * Main method for testing some functionality
	 */
	public static void main(String[] args) throws AWTException, java.net.MalformedURLException {
		SysNotificationManager td = new SysNotificationManager(new ConnectionMaster());
		td.displayInternetConnected();

	}
	private void addOnClick(){
		final SysNotificationManager sysManager = this;
		this.getTrayIcon().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				sysManager.startAnalysisGUI();
			}
		});
	}

	private void addMenu(){

		final SysNotificationManager sysManager = this;

		PopupMenu menu = new PopupMenu();

		MenuItem closeItem = new MenuItem("Close");
		closeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PromptCloseGUIControl.startGUI();
			}
		});

		MenuItem aboutItem = new MenuItem("About");
		aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sysManager.startAboutGUI();
			}
		});
		MenuItem analyzeItem = new MenuItem("Analyze");
		analyzeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sysManager.getConnMaster().logPastInternetConnectionStatus();
				sysManager.startAnalysisGUI();
			}
		});

		menu.add(analyzeItem);

		MenuItem settingsItem = new MenuItem("Settings");
		settingsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sysManager.startSettingsGUI();
			}
		});
		
		menu.add(settingsItem);
		
		if (ConnectionMaster.canAddToStartup()){//TODO: remove this.
			//TODO: May want to change to "create startup shortcut"
			final CheckboxMenuItem runOnStartup = new CheckboxMenuItem("Run at Startup",sysManager.getConnMaster().isInStartup());
			runOnStartup.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (runOnStartup.getState()){
						sysManager.getConnMaster().addWinStartup();
					} else{
						sysManager.getConnMaster().removeWinStartup();
					}
				}
			});
			menu.add(runOnStartup);
		}



		menu.add(aboutItem);
		menu.add(closeItem);


		this.getTrayIcon().setPopupMenu(menu);
	}
	public static void setUILookAndFeel(){
		try {
			// Set System L&F
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		} 
		catch (UnsupportedLookAndFeelException e) {
			// handle exception
		}
		catch (ClassNotFoundException e) {
			// handle exception
		}
		catch (InstantiationException e) {
			// handle exception
		}
		catch (IllegalAccessException e) {
			// handle exception
		}
	}
	public void startAnalysisGUI(){
		new AnalysisGUIControl(this.getConnMaster().getLogger().getInternetLogFullPath()).startGUI();
	}
	public void startSettingsGUI(){
		new CreateSettingsGUIControl(this.getConnMaster().getInputFilenameFullPath());
	}
	public void startAboutGUI(){
		AboutGUIControl.startGUI();
	}

	public void displayStartLogging(){
		this.displayTray("Starting Logging","simpleInternetLog has started connection logging.");
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
		this.displayTray("Unable to write to file!", "simpleInternetLog is unable to write to \"" + filename + "\". Close any applications using this file or simpleInternetLog will quit.");
	}
	public void displayErrorWriting(){
		this.displayTray("Unable to write to file!", "simpleInternetLog is unable to write to at least one of the log files. Close any applications using the files.");
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

	public ConnectionMaster getConnMaster() {
		return connMaster;
	}

	private void setConnMaster(ConnectionMaster connMaster) {
		this.connMaster = connMaster;
	}

	public static Image getCurIcon() {
		return SysNotificationManager.curIcon;
	}

	public static void setCurIcon(Image curIcon) {
		SysNotificationManager.curIcon = curIcon;
	}

}

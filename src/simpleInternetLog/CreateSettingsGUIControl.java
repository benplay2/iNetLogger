package simpleInternetLog;

import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang3.StringUtils;

/*
 * GUI to help creating a settings startup file and maybe a shortcut... Likely only for Windows.
 */
public class CreateSettingsGUIControl implements ActionListener{

	private ConnectionMaster master;
	private ShortcutCreator linkCreator = null;

	private JTextField checkRateTextField;
	private Checkbox verboseCheckbox;
	private JTextField localAddressTextField;
	private JTextArea internetAddressTextArea;
	private JTextField savePathTextField;
	
	private Color connectedColor = new Color(193, 239, 143);
	private Color notConnectedColor = new Color(255, 177, 155);
	private Color autoConnectedColor = new Color(147, 217, 249);
	
	private String lastInternetConnectionsString = "";
	private String origNetworkAddressString = "";
	private boolean networkAddressAutomaticallyObtained;
	private boolean lastLocalConnected = false;
	private boolean lastIterAutoConn = false;

	public CreateSettingsGUIControl(ConnectionMaster master){
		this.setMaster(master);
		if (ShortcutCreator.canCreateShortcut()) {
			try {
				this.setLinkCreator(new ShortcutCreator(ConnectionMaster.getBinPath(), "SimpleInternetLogger"));
			} catch (CannotFindPathException e) {

			}
		}
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



	public void saveNewDefaults() {
		String newContents = "";
		
		
		//verbose
		String verboseVal;
		if (this.getVerboseCheckbox().getState()) {
			verboseVal = "-v ";
		}else {
			verboseVal = "";
		}
		
		//local address
		String localVal = this.getLocalAddressTextField().getText().trim();
		if (!localVal.isEmpty()) {
			if (!(this.isNetworkAddressAutomaticallyObtained() && localVal.equals(this.getOrigNetworkAddressString()))) {
				localVal = "-l " + localVal + " ";
			}
		}
		
		//Internet address
		String intVal = this.getInternetAddressTextArea().getText().trim();
		if (!intVal.isEmpty()) {
			intVal = "-i " + intVal + " ";
		}
		
		//check rate
		String rateTxt = this.getCheckRateTextField().getText().trim();
		if (!rateTxt.isEmpty()) {
			try {
				Long.valueOf(rateTxt);
				rateTxt = "-r " + rateTxt + " ";
			} catch(NumberFormatException e){
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, 
	                    "Unable to decode sample rate from entry!", 
	                    "Unable to Decode Rate", 
	                    JOptionPane.WARNING_MESSAGE);
				rateTxt = "";
			}
			
		}
		
		//save path
		String saveTxt = this.getSavePathTextField().getText().trim();
		if (!saveTxt.isEmpty()) {
			saveTxt = "-s \"" + saveTxt + "\" ";
		}
		
		newContents = verboseVal + localVal + intVal + rateTxt + saveTxt;
		
		try {
			FileUtils.replaceFileContents(this.getMaster().getInputFilenameFullPath(), newContents);
			this.setConnectionMasterValues();
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, 
                    "Unable to write defaults to default file!", 
                    "Unable to Write", 
                    JOptionPane.WARNING_MESSAGE);
		}
		
		
	}
	public void setConnectionMasterValues() {
		//verbose

				this.getMaster().setVerbose(this.getVerboseCheckbox().getState());
				
				//local address
				String localVal = this.getLocalAddressTextField().getText().trim();
				if (!localVal.isEmpty()) {
					if (!(this.isNetworkAddressAutomaticallyObtained() && localVal.equals(this.getOrigNetworkAddressString()))) {
						master.getInterfaceCheck().setLocalAddressString(localVal);
					}
				}
				
				//Internet address
				String intVal = this.getInternetAddressTextArea().getText().trim();
				if (!intVal.isEmpty() && !intVal.equals(this.getLastInternetConnectionsString())) {
					this.setLastInternetConnectionsString(intVal);
					String[] iNetAddresses = intVal.split("\\s*,\\s*");
					this.getMaster().setConnectionList(new LinkedList<NetworkConnection>());
					
					for (String curAddressIn:iNetAddresses){
						master.getConnectionList().add(new NetworkConnection(curAddressIn));
					}
				}
				
				//check rate
				String rateTxt = this.getCheckRateTextField().getText().trim();
				if (!rateTxt.isEmpty()) {
					long rateNum;
					try {
						rateNum = Long.valueOf(rateTxt);
						this.getMaster().setSampleRate(rateNum * 1000);
					} catch(NumberFormatException e){
						e.printStackTrace();
						JOptionPane.showMessageDialog(null, 
			                    "Unable to decode sample rate from entry!", 
			                    "Unable to Decode Rate", 
			                    JOptionPane.WARNING_MESSAGE);
					}
					
				}
				
				//save path
				String saveTxt = this.getSavePathTextField().getText().trim();
				if (!saveTxt.isEmpty()) {
					this.getMaster().getLogger().setSavePath(saveTxt);
				}
	}

	private void createAndShowGUI(){
		JFrame mainFrame = new JFrame("iNetLogger by Ben Brust - Settings");
		mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		//mainFrame.setSize(400, 300);
		mainFrame.setIconImage(SysNotificationManager.getCurIcon());

		//mainFrame.getContentPane().setLayout(new BorderLayout()); //This is default.
		addComponentsToPane(mainFrame.getContentPane());

		mainFrame.pack(); //auto-size
		mainFrame.setVisible(true);//show

	}




	private void addComponentsToPane(Container pane){
		pane.setLayout(new GridBagLayout());

		GridBagConstraints c;
		JButton button;
		JLabel label;
		JTextField textField;
		JTextArea textArea;

		final CreateSettingsGUIControl thisControl = this;

		c = new GridBagConstraints();

		label = new JLabel("Settings");
		label.setFont(label.getFont().deriveFont((float)20));
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		c.fill = GridBagConstraints.NONE;
		c.ipady = 10;  
		c.ipadx = 0;
		c.weightx = 1.0;	//request any extra horizontal space
		c.weighty = 1.0;   //request any extra vertical space
		c.anchor = GridBagConstraints.CENTER;
		//c.insets = new Insets(10,0,10,0);
		c.gridx = 0;       //column
		c.gridwidth = 3;   //column width
		c.gridy = 0;       //row
		pane.add(label, c);

		c = new GridBagConstraints();
		Checkbox verboseCheck = new Checkbox("Verbose",getMaster().isVerbose());
		verboseCheck.setFont(label.getFont().deriveFont((float)16));
		setVerboseCheckbox(verboseCheck);
		c.fill = GridBagConstraints.NONE;
		c.ipady = 10;  
		c.ipadx = 0;
		c.weightx = 1.0;	//request any extra horizontal space
		c.weighty = 1.0;   //request any extra vertical space
		c.anchor = GridBagConstraints.CENTER;
		//c.insets = new Insets(10,0,10,0);
		c.gridx = 1;       //column
		c.gridwidth = 1;   //column width
		c.gridy = 1;       //row
		pane.add(verboseCheck, c);

		c = new GridBagConstraints();

		label = new JLabel("Check Rate:");
		label.setFont(label.getFont().deriveFont((float)16));
		c.fill = GridBagConstraints.NONE;
		c.ipady = 10;  
		c.ipadx = 0;
		c.weightx = 1.0;	//request any extra horizontal space
		c.weighty = 1.0;   //request any extra vertical space
		c.anchor = GridBagConstraints.EAST;
		//c.insets = new Insets(10,0,10,0);
		c.gridx = 0;       //column
		c.gridwidth = 1;   //column width
		c.gridy = 2;       //row
		pane.add(label, c);


		c = new GridBagConstraints();
		textField = new JTextField(String.valueOf(CreateSettingsGUIControl.this.getMaster().getSampleRate()/1000));
		CreateSettingsGUIControl.this.setCheckRateTextField(textField);
		textField.setPreferredSize(new Dimension(50,25));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 10;   
		c.ipadx = 0;
		c.weightx = 1.0;	//request any extra horizontal space
		c.weighty = 1.0;   //request any extra vertical space
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(5,15,5,15);
		c.gridx = 1;       //column
		c.gridwidth = 1;   //column width
		c.gridy = 2;       //row
		pane.add(textField, c);

		c = new GridBagConstraints();
		label = new JLabel("seconds");
		label.setFont(label.getFont().deriveFont((float)16));
		c.fill = GridBagConstraints.NONE;
		c.ipady = 10;   
		c.ipadx = 20;
		c.weightx = 1.0;	//request any extra horizontal space
		c.weighty = 1.0;   //request any extra vertical space
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0,0,0,10);
		c.gridx = 2;       //column
		c.gridwidth = 1;   //column width
		c.gridy = 2;       //row
		pane.add(label, c);


		c = new GridBagConstraints();
		label = new JLabel("Local Address:");
		label.setFont(label.getFont().deriveFont((float)16));
		c.fill = GridBagConstraints.NONE;
		c.ipady = 10;  
		c.ipadx = 0;
		c.weightx = 1.0;	//request any extra horizontal space
		c.weighty = 1.0;   //request any extra vertical space
		c.anchor = GridBagConstraints.EAST;
		//c.insets = new Insets(10,0,10,0);
		c.gridx = 0;       //column
		c.gridwidth = 1;   //column width
		c.gridy = 3;       //row
		pane.add(label, c);

		c = new GridBagConstraints();
		NetworkInterfaceCheck intCheck = this.getMaster().getInterfaceCheck();
		this.setNetworkAddressAutomaticallyObtained(intCheck.isAutoDeterminedAddress());
		this.setOrigNetworkAddressString(intCheck.getLocalAddressString());
		textField = new JTextField(intCheck.getLocalAddressString());
		this.setLastLocalConnected(intCheck.isPrevConnected());

		textField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				//thisControl.updateNetworkConnectionColor();
			}
			public void removeUpdate(DocumentEvent e) {
				thisControl.updateNetworkConnectionColor();
			}
			public void insertUpdate(DocumentEvent e) {
				thisControl.updateNetworkConnectionColor();
			}

		}
				);
		CreateSettingsGUIControl.this.setLocalAddressTextField(textField);
		thisControl.updateNetworkConnectionColor();
		textField.setPreferredSize(new Dimension(150,25));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 10;   
		c.ipadx = 0;
		c.weightx = 1.0;	//request any extra horizontal space
		c.weighty = 1.0;   //request any extra vertical space
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(5,10,5,10);
		c.gridx = 1;       //column
		c.gridwidth = 2;   //column width
		c.gridy = 3;       //row
		pane.add(textField, c);

		c = new GridBagConstraints();
		label = new JLabel("Internet Addresses: (separate by ,)");
		label.setFont(label.getFont().deriveFont((float)16));
		c.fill = GridBagConstraints.NONE;
		c.ipady = 10; 
		c.ipadx = 0;
		c.weightx = 1.0;	//request any extra horizontal space
		c.weighty = 1.0;   //request any extra vertical space
		c.anchor = GridBagConstraints.SOUTHWEST;
		c.insets = new Insets(0,10,0,0);
		c.gridx = 0;       //column
		c.gridwidth = 2;   //column width
		c.gridy = 5;       //row
		pane.add(label, c);

		c = new GridBagConstraints();
		String connectionTxt;
		
		LinkedList<String> connStrs = new LinkedList<String>();
		for (NetworkConnection curConn : getMaster().getConnectionList()) {
			connStrs.add(curConn.getAddressString());
		}
		connectionTxt = StringUtils.join(connStrs,",");
		this.setLastInternetConnectionsString(connectionTxt);
		
		textArea = new JTextArea(connectionTxt);
		CreateSettingsGUIControl.this.setInternetAddressTextArea(textArea);;
		textArea.setPreferredSize(new Dimension(500,75));
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		c.fill = GridBagConstraints.BOTH;
		c.ipady = 10;  
		c.ipadx = 0;
		c.weightx = 1.0;	//request any extra horizontal space
		c.weighty = 1.0;   //request any extra vertical space
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(5,10,5,10);
		c.gridx = 0;       //column
		c.gridwidth = 3;   //column width
		c.gridy = 6;       //row
		pane.add(textArea, c);

		c = new GridBagConstraints();
		label = new JLabel("Save Path:");
		label.setFont(label.getFont().deriveFont((float)16));
		c.fill = GridBagConstraints.NONE;
		c.ipady = 10; 
		c.ipadx = 0;
		c.weightx = 1.0;	//request any extra horizontal space
		c.weighty = 1.0;   //request any extra vertical space
		c.anchor = GridBagConstraints.SOUTHWEST;
		c.insets = new Insets(0,10,0,0);
		c.gridx = 0;       //column
		c.gridwidth = 1;   //column width
		c.gridy = 7;       //row
		pane.add(label, c);

		c = new GridBagConstraints();
		textField = new JTextField(CreateSettingsGUIControl.this.getMaster().getLogger().getSavePath());
		CreateSettingsGUIControl.this.setSavePathTextField(textField);
		textField.setPreferredSize(new Dimension(150,25));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 10; 
		c.ipadx = 0;
		c.weightx = 1.0;	//request any extra horizontal space
		c.weighty = 1.0;   //request any extra vertical space
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(5,10,5,10);
		c.gridx = 0;       //column
		c.gridwidth = 3;   //column width
		c.gridy = 8;       //row
		pane.add(textField, c);

		button = new JButton("Make Desktop Shortcut");
		button.setPreferredSize(new Dimension(100,25));
		button.setActionCommand("makeDesktopShortcut");
		button.addActionListener(thisControl);
		button.setVisible(thisControl.canMakeShortcut());
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 10;  
		c.ipadx = 50;
		c.weightx = 1.0;	//request any extra horizontal space
		c.weighty = 1.0;   //request any extra vertical space
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(5,10,5,10);
		c.gridx = 0;       //column
		c.gridwidth = 1;   //column width
		c.gridy = 9;       //row
		pane.add(button, c);

		button = new JButton("Make Start Shortcut");
		button.setPreferredSize(new Dimension(100,25));
		button.setActionCommand("makeShortcut");
		button.addActionListener(thisControl);
		button.setVisible(thisControl.canMakeShortcut());
		button.setToolTipText("Add shortcut to Start Menu");
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 10;  
		c.ipadx = 30;
		c.weightx = 1.0;	//request any extra horizontal space
		c.weighty = 1.0;   //request any extra vertical space
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(5,10,5,10);
		c.gridx = 1;       //column
		c.gridwidth = 1;   //column width
		c.gridy = 9;       //row
		pane.add(button, c);
		
		button = new JButton("Make Startup Shortcut");
		button.setPreferredSize(new Dimension(150,25));
		button.setActionCommand("makeStartup");
		button.addActionListener(thisControl);
		button.setVisible(thisControl.canMakeShortcut());
		button.setToolTipText("Add shortcut to Startup folder (in Start Menu)");
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 10; 
		c.ipadx = 30;
		c.weightx = 1.0;	//request any extra horizontal space
		c.weighty = 1.0;   //request any extra vertical space
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(5,10,5,10);
		c.gridx = 2;       //column
		c.gridwidth = 1;   //column width
		c.gridy = 9;       //row
		pane.add(button, c);

		button = new JButton("Save as Default");
		button.setPreferredSize(new Dimension(125,25));
		button.setActionCommand("saveDefault");
		button.addActionListener(thisControl);
		button.setToolTipText("Save settings as default");
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 10;   
		c.ipadx = 0;
		c.weightx = 1.0;	//request any extra horizontal space
		c.weighty = 1.0;   //request any extra vertical space
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(5,10,5,10);
		c.gridx = 1;       //column
		c.gridwidth = 1;   //column width
		c.gridy = 10;       //row
		pane.add(button, c);

		button = new JButton("Save");
		button.setPreferredSize(new Dimension(40,25));
		button.setActionCommand("save");
		button.addActionListener(thisControl);
		button.setToolTipText("Save settings for this instance");
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 10;  
		c.ipadx = 0;
		c.weightx = 1.0;	//request any extra horizontal space
		c.weighty = 1.0;   //request any extra vertical space
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(5,10,5,10);
		c.gridx = 2;       //column
		c.gridwidth = 1;   //column width
		c.gridy = 10;       //row
		pane.add(button, c);


	}

	private boolean canMakeShortcut() {
		return this.getLinkCreator() != null;
	}

	private void makeStartupShortcut() {
		this.getLinkCreator().createStartupShortcut();
	}
	private void makeDesktopShortcut() {
		this.getLinkCreator().createDesktopShortcut();
	}
	private void makeStartMenuShortcut() {
		this.getLinkCreator().createStartMenuShortcut();
	}


	public static void main(String[] args){
		//Schedule a job for the event dispatch thread:
		//creating and showing this application's GUI.
		SysNotificationManager.setUILookAndFeel();
		CreateSettingsGUIControl control = new CreateSettingsGUIControl(new ConnectionMaster());
		control.startGUI();
	}

	public void startGUI(){
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	private ConnectionMaster getMaster() {
		return master;
	}

	private void setMaster(ConnectionMaster master) {
		this.master = master;
	}

	private JTextField getCheckRateTextField() {
		return checkRateTextField;
	}

	private void setCheckRateTextField(JTextField checkRateTextField) {
		this.checkRateTextField = checkRateTextField;
	}

	private Checkbox getVerboseCheckbox() {
		return verboseCheckbox;
	}

	private void setVerboseCheckbox(Checkbox verboseCheckbox) {
		this.verboseCheckbox = verboseCheckbox;
	}

	private JTextField getLocalAddressTextField() {
		return localAddressTextField;
	}

	private void setLocalAddressTextField(JTextField localAddressTextField) {
		this.localAddressTextField = localAddressTextField;
	}

	private JTextArea getInternetAddressTextArea() {
		return internetAddressTextArea;
	}

	private void setInternetAddressTextArea(JTextArea internetAddressTextArea) {
		this.internetAddressTextArea = internetAddressTextArea;
	}

	private JTextField getSavePathTextField() {
		return savePathTextField;
	}

	private void setSavePathTextField(JTextField savePathTextField) {
		this.savePathTextField = savePathTextField;
	}

	private void updateNetworkConnectionColor() {
		updateNetworkConnectionColor(this.getLocalAddressTextField().getText().trim());
	}

	private void updateNetworkConnectionColor(String localVal) {
		if (localVal.isEmpty() && this.isLastLocalConnected()) {
			this.setLastLocalConnected(false);
			this.getLocalAddressTextField().setBackground(this.getAutoConnectedColor());
			this.getLocalAddressTextField().setToolTipText("Will attempt to automatically determine");
			return;
		}

		boolean reachable = NetworkInterfaceCheck.isAddressReachable(localVal);

		if (this.isNetworkAddressAutomaticallyObtained() && localVal.equals(this.getOrigNetworkAddressString())) {
			if (!this.isLastIterAutoConn()) {
				this.setLastIterAutoConn(true);
				this.getLocalAddressTextField().setBackground(this.getAutoConnectedColor());
			}
			if (reachable && !this.isLastLocalConnected()) {
				this.setLastLocalConnected(true);
				this.getLocalAddressTextField().setToolTipText("Automatically determined, connected");
				return;
			} else if(!reachable && this.isLastLocalConnected()){
				this.setLastLocalConnected(false);
				this.getLocalAddressTextField().setToolTipText("Automatically determined, not connected");
				return;
			}
		} else if (this.isLastIterAutoConn()) {
			this.setLastIterAutoConn(false);
		}
		
		
		
		
		if (reachable && !this.isLastLocalConnected()) {
				this.setLastLocalConnected(true);
				this.getLocalAddressTextField().setBackground(this.getConnectedColor());
				this.getLocalAddressTextField().setToolTipText("Connected");
			
		} else if (!reachable && this.isLastLocalConnected()){
				this.setLastLocalConnected(false);
				this.getLocalAddressTextField().setBackground(this.getNotConnectedColor());
				this.getLocalAddressTextField().setToolTipText("Not Connected");
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ("makeStartup".equals(e.getActionCommand())) {
			this.makeStartupShortcut();
		}else if( "makeShortcut".equals(e.getActionCommand())) {
			this.makeStartMenuShortcut();
		}else if( "makeDesktopShortcut".equals(e.getActionCommand())) {
			this.makeDesktopShortcut();
		}
		else if( "saveDefault".equals(e.getActionCommand())) {
			this.saveNewDefaults();
		}
		else if( "save".equals(e.getActionCommand())) {
			this.setConnectionMasterValues();
		}

	}



	private String getLastInternetConnectionsString() {
		return lastInternetConnectionsString;
	}



	private void setLastInternetConnectionsString(String lastInternetConnectionsString) {
		this.lastInternetConnectionsString = lastInternetConnectionsString;
	}



	private String getOrigNetworkAddressString() {
		return origNetworkAddressString;
	}



	private void setOrigNetworkAddressString(String origNetworkAddressString) {
		this.origNetworkAddressString = origNetworkAddressString;
	}



	private boolean isNetworkAddressAutomaticallyObtained() {
		return networkAddressAutomaticallyObtained;
	}



	private void setNetworkAddressAutomaticallyObtained(boolean networkAddressAutomaticallyObtained) {
		this.networkAddressAutomaticallyObtained = networkAddressAutomaticallyObtained;
	}



	private Color getConnectedColor() {
		return connectedColor;
	}


	private Color getNotConnectedColor() {
		return notConnectedColor;
	}



	private boolean isLastLocalConnected() {
		return lastLocalConnected;
	}



	private void setLastLocalConnected(boolean lastLocalConnected) {
		this.lastLocalConnected = lastLocalConnected;
	}



	private Color getAutoConnectedColor() {
		return autoConnectedColor;
	}



	private void setAutoConnectedColor(Color autoConnectedColor) {
		this.autoConnectedColor = autoConnectedColor;
	}



	private boolean isLastIterAutoConn() {
		return lastIterAutoConn;
	}



	private void setLastIterAutoConn(boolean lastIterAutoConn) {
		this.lastIterAutoConn = lastIterAutoConn;
	}



	private ShortcutCreator getLinkCreator() {
		return linkCreator;
	}



	private void setLinkCreator(ShortcutCreator linkCreator) {
		this.linkCreator = linkCreator;
	}



}

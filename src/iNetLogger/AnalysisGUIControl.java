package iNetLogger;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class AnalysisGUIControl implements ActionListener{

	private static String logFilename;
	private static long startTime;
	private static long endTime;
	private static JFrame mainFrame;
	private static JTextArea analysisResultsArea; //Store results
	private static JTextField startTimeField;
	private static JTextField endTimeField;
	private static JTextField filenameField;
	
	public AnalysisGUIControl(String logFilename){
		this.setStartTime(0);
		this.setEndTime(System.currentTimeMillis());
		this.setLogFilename(logFilename);
	}
	public AnalysisGUIControl(){
		this.setStartTime(0);
		this.setEndTime(System.currentTimeMillis());
		this.setLogFilename("");
	}
	
	public void createAndShowGUI(){
		JFrame frame = new JFrame("iNetLogger Analysis");
		this.setMainFrame(frame);
		frame.setIconImage(SysNotificationManager.getCurIcon());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		addComponentsToPane(mainFrame.getContentPane());
		
		frame.pack(); //auto-size
		frame.setVisible(true);//show
		
		
	}


	public void addComponentsToPane(Container pane){
		pane.setLayout(new GridBagLayout());

		
//		pane.setComponentOrientation(
//				java.awt.ComponentOrientation.RIGHT_TO_LEFT);
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 0.5;
		c.weighty = 0;
		JButton button;
		JLabel label;
		JTextField textField;
		JTextArea textArea;
		
		label = new JLabel("File to Analyze:");
		label.setFont(label.getFont().deriveFont((float)16));
		c.fill = GridBagConstraints.BOTH;
		c.ipadx = 0;
	    c.ipady = 0;       
	    //c.weighty = 1.0;   //request any extra vertical space
	    c.anchor = GridBagConstraints.SOUTHWEST; //bottom of space
	    //c.insets = new Insets(10,0,0,0);  //top padding
	    c.gridx = 0;       //column
	    c.gridwidth = 2;   //column width
	    c.gridy = 0;       //row
	    pane.add(label, c);
	    
	    textField = new JTextField(this.getLogFilename());
	    this.setFilenameField(textField);
	    textField.setPreferredSize(new Dimension(600,25));
	    c.fill = GridBagConstraints.HORIZONTAL;
		c.ipadx = 0;
	    c.ipady = 0;       
	    //c.weighty = 1.0;   //request any extra vertical space
	    //c.anchor = GridBagConstraints.PAGE_END; //bottom of space
	    //c.insets = new Insets(10,0,0,0);  //top padding
	    c.gridx = 0;       //column
	    c.gridwidth = 2;   //column width
	    c.gridy = 1;       //row
	    textField.setActionCommand("manualFilePath");
	    textField.addActionListener(new AnalysisGUIControl());
	    pane.add(textField, c);
		
	    button = new JButton("Browse");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipadx = 0;
	    c.ipady = 0;       
	    //c.weighty = 1.0;   //request any extra vertical space
	    //c.anchor = GridBagConstraints.PAGE_END; //bottom of space
	    //c.insets = new Insets(10,0,0,0);  //top padding
	    c.gridx = 2;       //column
	    c.gridwidth = 1;   //column width
	    c.gridy = 1;       //row
	    button.setActionCommand("browse");
	    button.addActionListener(new AnalysisGUIControl());
	    pane.add(button, c);
	    
		label = new JLabel("From (D/M/YYYY):");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipadx = 10;
	    c.ipady = 0;       
	    //c.weighty = 1.0;   //request any extra vertical space
	    c.anchor = GridBagConstraints.SOUTHWEST; //bottom of space
	    //c.insets = new Insets(10,0,0,0);  //top padding
	    c.gridx = 0;       //column
	    c.gridwidth = 1;   //column width
	    c.gridy = 2;       //row
	    pane.add(label, c);
		
	    label = new JLabel("To (D/M/YYYY):");
		c.fill = GridBagConstraints.HORIZONTAL;
	    c.ipady = 0; 
		c.ipadx = 0;
	    //c.weighty = 1.0;   //request any extra vertical space
	    c.anchor = GridBagConstraints.SOUTHWEST; //bottom of space
	    //c.insets = new Insets(10,0,0,0);  //top padding
	    c.gridx = 1;       //column
	    c.gridwidth = 1;   //column width
	    c.gridy = 2;       //row
	    pane.add(label, c);
		
	    textField = new JTextField(CSVEntry.getCSVTimestamp(this.getStartTime()));
	    this.setStartTimeField(textField);
	    textField.setPreferredSize(new Dimension(300,25));
	    c.fill = GridBagConstraints.HORIZONTAL;
		c.ipadx = 10;
	    c.ipady = 0;       
	    //c.weighty = 1.0;   //request any extra vertical space
	    c.anchor = GridBagConstraints.LINE_START;
	    //c.insets = new Insets(10,0,0,0);  //top padding
	    c.gridx = 0;       //column
	    c.gridwidth = 1;   //column width
	    c.gridy = 3;       //row
	    textField.setActionCommand("changeStart");
	    textField.addActionListener(new AnalysisGUIControl());
	    pane.add(textField, c);
	    
	    textField = new JTextField(CSVEntry.getCSVTimestamp(this.getEndTime()));
	    this.setEndTimeField(textField);
	    textField.setPreferredSize(new Dimension(300,25));
	    c.fill = GridBagConstraints.HORIZONTAL;
		c.ipadx = 0;
	    c.ipady = 0;       
	    //c.weighty = 1.0;   //request any extra vertical space
	    c.anchor = GridBagConstraints.LINE_START; 
	    //c.insets = new Insets(10,0,0,0);  //top padding
	    c.gridx = 1;       //column
	    c.gridwidth = 1;   //column width
	    c.gridy = 3;       //row
	    textField.setActionCommand("changeEnd");
	    textField.addActionListener(new AnalysisGUIControl());
	    pane.add(textField, c);
	    
		button = new JButton("Analyze");
		c.fill = GridBagConstraints.HORIZONTAL;
	    c.ipady = 0;  
	    c.ipadx = 0;
	    //c.weighty = 1.0;   //request any extra vertical space
	    c.anchor = GridBagConstraints.CENTER; 
	    //c.insets = new Insets(10,0,0,0);  //top padding
	    c.gridx = 2;       //column
	    c.gridwidth = 1;   //column width
	    c.gridy = 3;       //row
	    button.setActionCommand("analyze");
	    button.addActionListener(new AnalysisGUIControl());
	    pane.add(button, c);
		
		
	    JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
	    separator.setPreferredSize(new Dimension(700,5));
	    c.fill = GridBagConstraints.NONE;
	    c.ipady = 0;  
	    c.ipadx = 0;
	    //c.weighty = 1.0;   //request any extra vertical space
	    c.anchor = GridBagConstraints.CENTER;
	    //c.insets = new Insets(10,0,0,0);  //top padding
	    c.gridx = 0;       //column
	    c.gridwidth = 3;   //column width
	    c.gridy = 4;       //row
		pane.add(separator, c);
		
		textArea = new JTextArea("Results will be displayed here");
		this.setAnalysisResultsArea(textArea);
		textArea.setPreferredSize(new Dimension(800,400));
		textArea.setEditable(false);
		textArea.setLineWrap(false);
		
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.ipadx =0;
	    c.ipady = 0;       
	    //c.weighty = 1.0;   //request any extra vertical space
	    c.anchor = GridBagConstraints.SOUTH; //bottom of space
	    //c.insets = new Insets(10,0,0,0);  //top padding
	    c.gridx = 0;       //column
	    c.gridwidth = 3;   //column width
	    c.gridy = 5;       //row
	    pane.add(textArea, c);
		
				
	}
	
	public static void main(String[] args){
		//Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        new AnalysisGUIControl().startGUI();
	}
	
	public void startGUI(){
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if ("analyze".equals(e.getActionCommand())) {
			String result = null;
			try {
				result = iNetLogAnalyzer.analyzeINetLogger(this.getLogFilename(), false, this.getStartTime(), this.getEndTime());
			} catch (IOException e1) {
				//e1.printStackTrace();
				result = e1.toString();
			}
			this.getAnalysisResultsArea().setText(result);
        } else if ("browse".equals(e.getActionCommand())){
        	//Create a file chooser
            JFileChooser fc = new JFileChooser();
            
            int returnVal = fc.showOpenDialog(this.getMainFrame());
            
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                //This is where a real application would open the file.
                this.setLogFilename(file.getAbsoluteFile().toString());
                this.getFilenameField().setText(file.getAbsolutePath().toString());
            }
        } else if ("manualFilePath".equals(e.getActionCommand())){
        	this.setLogFilename(this.getFilenameField().getText());
            
        }else if ("changeStart".equals(e.getActionCommand())){
        	try {
				this.setStartTime(CSVEntry.getTimeFromString(this.getStartTimeField().getText()));
				this.getStartTimeField().setText(CSVEntry.getCSVTimestamp(this.getStartTime()));
			} catch (ParseException e1) {
				//e1.printStackTrace();
				this.getAnalysisResultsArea().setText(e1.toString());
			}
        } else if ("changeEnd".equals(e.getActionCommand())){
        	try {
				this.setEndTime(CSVEntry.getTimeFromString(this.getEndTimeField().getText()));
				this.getEndTimeField().setText(CSVEntry.getCSVTimestamp(this.getEndTime()));
			} catch (ParseException e1) {
				//e1.printStackTrace();
				this.getAnalysisResultsArea().setText(e1.toString());
			}
        }
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	getMainFrame().repaint();
            }
        });
		
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		AnalysisGUIControl.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		AnalysisGUIControl.endTime = endTime;
	}

	public JTextArea getAnalysisResultsArea() {
		return analysisResultsArea;
	}

	public void setAnalysisResultsArea(JTextArea analysisResultsArea) {
		AnalysisGUIControl.analysisResultsArea = analysisResultsArea;
	}

	public JTextField getStartTimeField() {
		return startTimeField;
	}

	public void setStartTimeField(JTextField startTimeField) {
		AnalysisGUIControl.startTimeField = startTimeField;
	}

	public JTextField getEndTimeField() {
		return endTimeField;
	}

	public void setEndTimeField(JTextField endTimeField) {
		AnalysisGUIControl.endTimeField = endTimeField;
	}

	public JFrame getMainFrame() {
		return mainFrame;
	}

	public void setMainFrame(JFrame mainFrame) {
		AnalysisGUIControl.mainFrame = mainFrame;
	}

	public String getLogFilename() {
		return logFilename;
	}

	public void setLogFilename(String logFilename) {
		AnalysisGUIControl.logFilename = logFilename;
	}
	public JTextField getFilenameField() {
		return filenameField;
	}
	public void setFilenameField(JTextField filenameField) {
		AnalysisGUIControl.filenameField = filenameField;
	}

}

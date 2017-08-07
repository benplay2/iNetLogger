package iNetLogger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class AnalysisGUIControl implements ActionListener{

	private String logFilename;
	private long startTime;
	private long endTime;
	private JFrame mainFrame;
	private JTextArea analysisResultsArea; //Store results
	private JTextField startTimeField;
	private JTextField endTimeField;
	private JTextField filenameField;
	
	public AnalysisGUIControl(String logFilename){
		this.setStartTime(0);
		this.setEndTime(System.currentTimeMillis());
		this.setLogFilename(logFilename);
	}
	public AnalysisGUIControl(){
		this.setStartTime(0);
		this.setEndTime(System.currentTimeMillis());
		this.setLogFilename("");//TODO: need to have some way to ask user for location...
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
			//TODO: We have some analysis to do!
//			iNetLogAnalyzer.analyzeINetLogger("", verbose, startTime, endTime)
        } else if ("browse".equals(e.getActionCommand())){
        	//TODO: browse for our desired file.
        }
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public JTextArea getAnalysisResultsArea() {
		return analysisResultsArea;
	}

	public void setAnalysisResultsArea(JTextArea analysisResultsArea) {
		this.analysisResultsArea = analysisResultsArea;
	}

	public JTextField getStartTimeField() {
		return startTimeField;
	}

	public void setStartTimeField(JTextField startTimeField) {
		this.startTimeField = startTimeField;
	}

	public JTextField getEndTimeField() {
		return endTimeField;
	}

	public void setEndTimeField(JTextField endTimeField) {
		this.endTimeField = endTimeField;
	}

	public JFrame getMainFrame() {
		return mainFrame;
	}

	public void setMainFrame(JFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	public String getLogFilename() {
		return logFilename;
	}

	public void setLogFilename(String logFilename) {
		this.logFilename = logFilename;
	}
	public JTextField getFilenameField() {
		return filenameField;
	}
	public void setFilenameField(JTextField filenameField) {
		this.filenameField = filenameField;
	}

}

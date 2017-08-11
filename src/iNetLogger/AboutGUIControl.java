package iNetLogger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

public class AboutGUIControl implements ActionListener{
	


	public static void createAndShowGUI(){
		JFrame mainFrame = new JFrame("About simpleInternetLog");
		mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		//mainFrame.setSize(400, 300);
		mainFrame.setIconImage(SysNotificationManager.getCurIcon());
		
		//mainFrame.getContentPane().setLayout(new BorderLayout()); //This is default.
		addComponentsToPane(mainFrame.getContentPane());
		
		mainFrame.pack(); //auto-size
		mainFrame.setVisible(true);//show



	}


	public static void addComponentsToPane(Container pane){
		if (!(pane.getLayout() instanceof BorderLayout)) {
			pane.add(new JLabel("Container doesn't use BorderLayout!"));
			return;
		}

//		pane.setComponentOrientation(
//				java.awt.ComponentOrientation.RIGHT_TO_LEFT);

		JTextArea text = new JTextArea("simpleInternetLog is an Internet logging application released under GNU GPL by Ben Brust. "+
		System.lineSeparator() + System.lineSeparator() +
		"simpleInternetLog is free to use, but if it is useful for you, I would not object to a small donation!"+
		System.lineSeparator() +
		"Maybe... $1/computer?");
		text.setPreferredSize(new Dimension(400,150));
		text.setEditable(false);
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		text.setAlignmentX(Component.CENTER_ALIGNMENT);
		text.setBackground(new Color(224, 224, 224));
		text.setFont(new Font("Sans Sarif",Font.PLAIN,16));
		pane.add(text,BorderLayout.CENTER);
		
		JButton button = new JButton("Donate Here! :)");
		button.setPreferredSize(new Dimension(15,25));
		button.setActionCommand("donate");
		button.addActionListener(new AboutGUIControl());
		pane.add(button, BorderLayout.SOUTH);
				
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


	@Override
	public void actionPerformed(ActionEvent e) {
		if ("donate".equals(e.getActionCommand())) {
			String url = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=FL3XTVVDQ8UK2";
			BrowserUtils.openWebpage(url);
        }
	}
	
	
}

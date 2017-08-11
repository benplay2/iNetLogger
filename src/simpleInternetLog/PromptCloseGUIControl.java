package simpleInternetLog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

/*
 * GUI to prompt if really want want to exit.
 */
public class PromptCloseGUIControl{

	public static void createAndShowGUI(){
		JFrame mainFrame = new JFrame("Really close iNetLogger?");
		mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		//mainFrame.setSize(400, 300);
		mainFrame.setIconImage(SysNotificationManager.getCurIcon());
		
		//mainFrame.getContentPane().setLayout(new BorderLayout()); //This is default.
		addComponentsToPane(mainFrame);
		
		mainFrame.pack(); //auto-size
		mainFrame.setVisible(true);//show



	}


	public static void addComponentsToPane(JFrame mainFrame){
		final JFrame thisFrame = mainFrame;
		Container pane = mainFrame.getContentPane();
		
		if (!(pane.getLayout() instanceof BorderLayout)) {
			pane.add(new JLabel("Container doesn't use BorderLayout!"));
			return;
		}
		//TODO: make this GUI look better...
		
		JLabel label = new JLabel("Are you sure you want to close iNetLogger?");
		label.setFont(label.getFont().deriveFont((float)16));
		pane.add(label,BorderLayout.CENTER);
		
		JButton button = new JButton("Yes");
		button.setPreferredSize(new Dimension(50,30));
		button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		pane.add(button, BorderLayout.WEST);
		
		button = new JButton("No");
		button.setPreferredSize(new Dimension(50,30));
		button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				thisFrame.dispose();
			}
		});
		pane.add(button, BorderLayout.EAST);
		
				
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


	
	
}

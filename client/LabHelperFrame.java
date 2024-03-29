import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
/**
 * Frame for client application
 * @author gcordts
 *
 */
public class LabHelperFrame extends JFrame {
	//Singleton instance
	private static LabHelperFrame instance;
	/**
	 * Get a LabHelperFrame
	 * @return the singleton LabHelperFrame instance
	 */
	public static LabHelperFrame get()
	{
		if(instance==null)
		{
			instance = new LabHelperFrame();
		}
		return instance;
	}
	private JProgressBar progress;
	public void startProgress()
	{  	
		progress.setIndeterminate(true);
		progress.setVisible(true);
		repaint();
	}
	public void stopProgress()
	{
		progress.setVisible(false);
		progress.setIndeterminate(false);
		repaint();
	}
	private LabHelperFrame()
	{
		super("LabHelper - Not logged in");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final LabHelperFrame self = this;
		final JTabbedPane tabs = new JTabbedPane();
		this.add(tabs, BorderLayout.CENTER);
		this.setMinimumSize(new Dimension(300,200));
		
		InputHandler.get().registerInput("loggedin", new InputProcessor(){
			public void processInput(JSONObject input) {
				String fname = (String)input.get("fname");
				String lname = (String)input.get("lname");
				String uname = (String)input.get("uname");
				String activity = (String)input.get("activity");
				Options.get().storeOption("activity", activity);
				UserInfo.create(fname, lname, uname);
				self.setTitle("LabHelper - Logged in as "+uname);
				stopProgress();
				tabs.addTab("Project Info", makeProjectTab());
			}});
		progress = new JProgressBar();
		this.add(progress, BorderLayout.SOUTH); 
		
		this.add(tabs, BorderLayout.CENTER);
		tabs.addTab("User Info",makeLoginTab());
		//tabs.addTab("Debug", makeDebugTab());
		this.pack();
	}
	/**
	 * Get login tab
	 * @return The login Panel
	 */
	private JPanel makeLoginTab()
	{
		return new LoginPanel();
	}
	/**
	 * Get the project data tab
	 * @return The project data tab
	 */
	private static JPanel makeProjectTab()
	{
		return new ProjectTab();
	}
	private static JPanel makeDebugTab()
	{
		JPanel debugTab = new JPanel();
		JButton sendFile = new JButton("Send File");
		debugTab.add(sendFile);
		return debugTab;
	}
}

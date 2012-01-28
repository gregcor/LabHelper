import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.json.simple.JSONObject;

/**
 * Class containing some miscellaneous client functions
 * @author gcordts
 *
 */
public class MiscFunctions {
	static ArrayList<JFrame> lockFrames;
	/**
	 * Call this only once - it sets up listeners for some other
	 * client behaviors (alerts, lock/unlock)
	 */
	public static void registerMiscFunctions()
	{
		//Handle alert from server
		InputHandler.get().registerInput("alert", new InputProcessor(){
			public void processInput(JSONObject input) {
				String text = (String) input.get("text");
				JOptionPane.showMessageDialog(null, text);
			}});
		//URL from server
		InputHandler.get().registerInput("url", new InputProcessor(){
			public void processInput(JSONObject input) {
				final String text = (String) input.get("text");
				JFrame toShow = new JFrame("Link Received");
				toShow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				toShow.setLocationByPlatform(true);
				JButton browseButton = new JButton("Browse URL");
				browseButton.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent arg0) {
						try {
							Desktop.getDesktop().browse(new URI(text));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (URISyntaxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}});
				JLabel link = new JLabel(text);
				toShow.add(link, BorderLayout.NORTH);
				toShow.add(browseButton);
				toShow.pack();
				toShow.setVisible(true);
			}});
		//Unlock requuest from server
		InputHandler.get().registerInput("unlock", new InputProcessor(){
			public void processInput(JSONObject input) {
				if(lockFrames!=null)
				{
					for(JFrame curFrame: lockFrames)
					{
						curFrame.setVisible(false);
						curFrame.dispose();
					}
				}
				lockFrames = new ArrayList<JFrame>();
			}});
		//Lock request from server
		InputHandler.get().registerInput("lock", new InputProcessor(){
			public void processInput(JSONObject input) {
				if(lockFrames!=null)
				{
					for(JFrame curFrame: lockFrames)
					{
						curFrame.setVisible(false);
						curFrame.dispose();
					}
				}
				lockFrames = new ArrayList<JFrame>();
				final JFrame bigFrame = new JFrame("LabHelper Lock");
				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				GraphicsDevice[] gs = ge.getScreenDevices();
				final Dimension bigDim = new Dimension(gs[0].getDisplayMode().getWidth(),
						gs[0].getDisplayMode().getHeight()); 
				bigFrame.setSize(bigDim);
				bigFrame.setResizable(false);
				bigFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				bigFrame.setLocation(0,0);
				bigFrame.addWindowStateListener(new WindowStateListener(){
					public void windowStateChanged(WindowEvent e) {
						if(e.getNewState() == WindowEvent.WINDOW_ICONIFIED)
						{
							bigFrame.setExtendedState(JFrame.NORMAL);
						}
						
					}});
				bigFrame.setAlwaysOnTop(true);
				bigFrame.setVisible(true);
				final Timer timer = new Timer(true); 
				final TimerTask t = new TimerTask()
				{
					public void run() {
						System.out.println("tick");
						if(bigFrame.isVisible())
						{
							bigFrame.setLocation(0, 0);
						}else
						{
							System.out.println("Canceled!");
							timer.cancel();
						}
					}
				};
				timer.scheduleAtFixedRate(t, 0, 1000);
				lockFrames.add(bigFrame);
				
			}});
	}
}

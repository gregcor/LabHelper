import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.json.simple.*;

import javax.swing.*;
/**
 * GUI interface for logging in - also responsible for reading
 * existing config file if one exists
 * @author gcordts
 *
 */
public class LoginPanel extends JPanel{
	
	/**
	 * Construct a new login panel
	 */
	public LoginPanel()
	{
		super();
		readConfigFile();
		if(UserInfo.get()==null)
		{
			setLayout(new GridLayout(0,2));
			add(new JLabel("First Name"));
			final JTextField fname = new JTextField(15);
			add(fname);
			add(new JLabel("Last Name"));
			final JTextField lname = new JTextField(15);
			add(lname);
			add(new JLabel("Course Username"));
			final JTextField uname = new JTextField(15);
			add(uname);
			add(new JLabel(""));
			JButton okayButton = new JButton("OK");
			okayButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					HashMap<String,String> toStore = new HashMap<String,String>();
					toStore.put("fname", fname.getText());
					toStore.put("lname", lname.getText());
					toStore.put("uname", uname.getText());
					Options.get().storeOption(toStore);
					readConfigFile();
					doLogin();
				}});
			add(okayButton);
			
		}else
		{
			doLogin();
		}
	}
	/**
	 * Store contents of UserInfo object in the tab's GUI
	 */
	private void doLogin()
	{
		UserInfo uinfo = UserInfo.get();
		setLayout(new GridLayout(2,1));
		add(new JLabel("Name"));
		add(new JLabel(uinfo.getFname() + " " + uinfo.getLname()));
		add(new JLabel("Username"));
		add(new JLabel(uinfo.getUname()));
		
		JSONObject obj = new JSONObject();
		obj.put("fname", uinfo.getFname());
		obj.put("lname", uinfo.getLname());
		obj.put("uname", uinfo.getUname());
		Communicator.get().request("login", obj);
	}
	/**
	 * Read existing UserInfo from config file.
	 */
	private static void readConfigFile()
	{
		String fname = Options.get().readOption("fname");
		if(fname==null)
		{
			return;
		}
		String lname = Options.get().readOption("lname");
		String uname = Options.get().readOption("uname");
		UserInfo.create(fname, lname, uname);
	}
}

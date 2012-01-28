import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * UI element responsible for launching projects
 * @author gcordts
 *
 */
public class ProjectTab extends JPanel {
	//Panel containing the project selector
	private JPanel projectPanel;
	//Label listing current path location
	private JLabel pathLabel;
	//Label listing current bluej location
	private JLabel bluejLabel; 
	/**
	 * Create and populate tab
	 */
	public ProjectTab()
	{
		super();
		this.setLayout(new BorderLayout());
		JPanel wsPanel = new JPanel();
		wsPanel.setLayout(new GridLayout(2,1));
		JButton browseButton = new JButton("Pick Workspace");
		pathLabel = new JLabel();
		browseButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int res = jfc.showOpenDialog(null);
				if(res==jfc.APPROVE_OPTION)
				{
					File selectedFile = jfc.getSelectedFile();
					Options.get().storeOption("workspace", selectedFile.getAbsolutePath());
					fillWorkspace(selectedFile.getAbsolutePath());
				}
			}});
		
		bluejLabel = new JLabel();
		JButton bluejButton = new JButton("Locate BlueJ");
		bluejButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				int res = jfc.showOpenDialog(null);
				if(res==jfc.APPROVE_OPTION)
				{
					File selectedFile = jfc.getSelectedFile();
					Options.get().storeOption("bluej", selectedFile.getAbsolutePath());
					fillBlueJ(selectedFile.getAbsolutePath());
				}
			}});
		
		wsPanel.add(browseButton);
		wsPanel.add(pathLabel);
		wsPanel.add(bluejButton);
		wsPanel.add(bluejLabel);
		this.add(wsPanel, BorderLayout.NORTH);
		
		String workspace = Options.get().readOption("workspace");
		if(workspace!=null)
		{
			fillWorkspace(workspace);
		}
		String bluej = Options.get().readOption("bluej");
		if(bluej!=null)
		{
			fillBlueJ(bluej);
		}
	}
	/**
	 * Abbreviate a string two 20 chars plus ellipsis
	 * @param text
	 * @return
	 */
	private static String getShortString(String text)
	{
		if(text.length()>20)
		{
			String lastTwenty = text.substring(text.length()-20);
			return "..." + lastTwenty;
		}else
		{
			return text;
		}
	}
	/**
	 * Fill blueJ label with short name
	 * @param path
	 */
	private void fillBlueJ(String path)
	{
		bluejLabel.setText(getShortString(path));
	}
	/**
	 * Fill workspace label with short name
	 * Initialize drop-down list for project selection and attach events
	 * @param path
	 */
	private void fillWorkspace(String path)
	{
		projectPanel = new JPanel();
		this.add(projectPanel, BorderLayout.CENTER);
		projectPanel.setLayout(new BoxLayout(projectPanel,BoxLayout.PAGE_AXIS));
		
		pathLabel.setText(getShortString(path));
		
		File wSpaceFile = new File(path);
		if(wSpaceFile.exists() && wSpaceFile.isDirectory())
		{
			List<ProjectInfo> projects = new ArrayList<ProjectInfo>();
			File[] subFiles = wSpaceFile.listFiles();
			for(File curFile: subFiles)
			{
				if(curFile.isDirectory())
				{
					String[] potentialProjectFiles = curFile.list();
					for(String curMaybeProject: potentialProjectFiles)
					{
						if(curMaybeProject.endsWith(".bluej"))
						{
							projects.add(new ProjectInfo(curFile.getName(), curFile));
							break;
						}
					}
				}
			}
			fillProjects(projects);
		}else
		{
			return;
		}
	}
	/**
	 * Populate project list for current project
	 * @param projects
	 */
	private void fillProjects(List<ProjectInfo> projects)
	{
		Map<String,ProjectInfo> projectList = new HashMap<String,ProjectInfo>();
		String curAct = Options.get().readOption("activity");
		projectList.put(curAct, new ProjectInfo(curAct, null));
		for(ProjectInfo curProj : projects)
		{
			//Modification to only show the current project
			if(curProj.getName().equals(curAct))
			{
				projectList.put(curProj.getName(), curProj);
			}
		}
		Object[] options = projectList.values().toArray(); 
		final JComboBox jcb = new JComboBox(options);
		projectPanel.add(jcb);
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(Options.get().readOption("bluej")==null)
				{
					JOptionPane.showMessageDialog(null, "Select a BlueJ location!");
					return;
				}
				ProjectInfo chosen = (ProjectInfo) jcb.getSelectedItem();
				chosen.launchProject();
			}});
		projectPanel.add(okButton);
	}
}

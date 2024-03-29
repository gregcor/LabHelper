import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.commons.codec.binary.Base64OutputStream;
import org.json.simple.JSONObject;

/**
 * Represents info we currently have about project
 * @author gcordts
 *n
 */
public class ProjectInfo {
	//Project location on disk (null if not downloaded yet)
	private File file;
	//Name of project
	private String name;
	/**
	 * Construct a new project
	 * @param name Project name
	 * @param path Path on disk
	 */
	public ProjectInfo(String name, File path)
	{
		file = path;
		this.name = name;
	}
	/**
	 * Get the project path
	 * @return the project path, or null if not downloaded yet
	 */
	public File getPath()
	{
		return file;
	}
	/**
	 * Get the name of the project
	 * @return
	 */
	public String getName()
	{
		return name;
	}
	/**
	 * Return project name for menus
	 */
	public String toString()
	{
		return getName();
	}
	/**
	 * Unzip project directory in place and delete original
	 * @param toUnzip File to unzip
	 */
	private static void unzipDirectory(File toUnzip)
	{
		try {
			ZipFile zf = new ZipFile(toUnzip);
			Enumeration<ZipEntry> files = (Enumeration<ZipEntry>) zf.entries();
			while(files.hasMoreElements())
			{
				ZipEntry entry = files.nextElement();
				if(entry.isDirectory())
					continue;
				InputStream is = zf.getInputStream(entry);
				File toWrite = new File(toUnzip.getParentFile(), entry.getName());
				toWrite.getParentFile().mkdirs();
				FileOutputStream fos = new FileOutputStream(toWrite);
				byte[] buffer = new byte[1000];
				for(;;)
				{
					int numRead = is.read(buffer);
					if(numRead==-1)
						break;
					fos.write(buffer, 0, numRead);
				}
				is.close();
				fos.close();
			}
			zf.close();
			toUnzip.delete();
			
		} catch (ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * Download project if necessary, then open it.
	 */
	public void launchProject()
	{
		if(file==null)
		{
			//create directory
			File baseDir = new File(Options.get().readOption("workspace"));
			final File newDir = new File(baseDir, name);
			newDir.mkdir();
			
			InputProcessor procDownload = new InputProcessor(){
				public void processInput(JSONObject input) {
					String data = (String) input.get("data");
					File zipFile = new File(newDir, "toUnzip.zip");
					FileOutputStream fos = null;
					try {
						fos = new FileOutputStream(zipFile);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					Base64OutputStream bos = new Base64OutputStream(fos, false);
					OutputStreamWriter osw = new OutputStreamWriter(bos);
					BufferedWriter bw = new BufferedWriter(osw);
					try {
						bw.write(data);
						bw.close();
						osw.close();
						bos.close();
						fos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					unzipDirectory(zipFile);
					file = newDir;
					runFile();
					
				}};
			
			//queue directory download from internet
			InputHandler.get().registerInput("putfile", procDownload, true);
			JSONObject obj = new JSONObject();
			obj.put("activity", getName());
			Communicator.get().request("getactivity", obj);
		}else
		{
			runFile();
		}
		
	}
	/**
	 * Open project file in correct editor
	 */
	private void runFile()
	{
		Runtime runtime = Runtime.getRuntime();
		String bluejLoc = Options.get().readOption("bluej");
		try {
			if(bluejLoc.endsWith(".exe")){
				String toExec = bluejLoc + " \"" + file.getAbsolutePath() + "\\package.bluej\"";
				runtime.exec(toExec);
			}else if (bluejLoc.endsWith(".jar"))
			{
				String toExec = "java -jar " + bluejLoc + " package.bluej";
				runtime.exec(toExec, null, file);
			}
			else
			{
				runtime.exec(bluejLoc + " package.bluej", null, file);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new Uploader(UserInfo.get(), this, 1000*60*2);
		LabHelperFrame.get().setVisible(false);
	}
}

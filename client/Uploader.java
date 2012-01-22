/**
 * Handles uploading and compressing the directory into a zip file.
 * There is a lot of work that needs to be done for error handling.
 */
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


import javax.swing.JOptionPane;

import org.apache.commons.codec.binary.Base64OutputStream;
import org.json.simple.JSONObject;


public class Uploader {
	// Connection string for FTP server. Can be hard coded.
	public static String FTPSERVER = "";
	// Connection string to file containing a connection string for the FTP server.
	public static final String CSTRLOC = "http://dl.dropbox.com/u/1031798/lab-connection.txt";

	
	
	private UserInfo user;
	private ProjectInfo project;
	//Timer that dictates the update interval.
	private Timer timer;
	
	public Uploader(UserInfo user, ProjectInfo project, long interval)
	{
		//Schedule upload tasks.
		this.user = user;
		this.project = project;
		timer = new Timer(true);
		TimerTask uploadTask = new TimerTask(){
			public void run() {
				if(LabHelperClient.checkIfStillUnique())
				{
					uploadDirectory();
				}
			}		
		};
		
		timer.scheduleAtFixedRate(uploadTask, 0, interval);
		
	}
	/**
	 * Makes a ZIP file in temp, then uploads it and prompts its deletion on exit.
	 */
	private void uploadDirectory()
	{
		String username = user.getLname() + user.getFname();
		String toUploadData = zipFileList(project.getPath());

		uploadFile(toUploadData);
	}
	/**
	 * Create a zip file
	 * @param directory top level directory to zip.
	 * @return Base-64 encoded zip file
	 */
	public static String zipFileList(File directory)
	{
		ZipOutputStream zos;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Base64OutputStream bsos = new Base64OutputStream(baos);
		
		zos = new ZipOutputStream(bsos);
		
		ArrayList<File> files = getAllContents(directory);
		for (File currentFile : files)
		{
			ZipEntry ze = new ZipEntry(getRelativePath(currentFile, directory));
			try {
				zos.putNextEntry(ze);
				FileReader fr = new FileReader(currentFile);
				int currentByte = fr.read();
				while(currentByte!=-1)
				{
					zos.write(currentByte);
					currentByte = fr.read();
				}
				fr.close();
				zos.closeEntry();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			zos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			String outStr = baos.toString("ASCII").replace('\r', '\n').replace("\n", "");
			System.out.println(outStr);
			return outStr;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * Recursively build up list of files.
	 * @param toRecurse Top level directory
	 * @return A list of all files in TLD.
	 */
	public static ArrayList<File> getAllContents(File toRecurse)
	{
		File[] toAdd = toRecurse.listFiles();
		ArrayList<File> currentStore = new ArrayList<File>();
		if(toAdd==null)
			return null;
		for (File currentFile : toAdd)
		{
			ArrayList<File> nextFile = getAllContents(currentFile);
			if(nextFile==null)
				currentStore.add(currentFile);
			else
				currentStore.addAll(nextFile);
		}
		return currentStore;
	}
	/**
	 * Java has no good mechanism to work with relative paths for zip files.
	 * This is a substitute to allow shortening for zip file paths.
	 * @param toTrim - Absolute file
	 * @param basePath - Base path of file
	 * @return The relative path to be placed in zip file
	 */
	public static String getRelativePath(File toTrim, File basePath)
	{
		if(toTrim.getPath().startsWith(basePath.getPath()))
		{
			return toTrim.getPath().substring(basePath.getPath().length()+1);
		}else
		{
			return toTrim.getPath();
		}
	}
	private static void uploadFile(String data)
	{
		System.out.println("length: " + data.length());
		JSONObject obj = new JSONObject();
		obj.put("data", data);
		obj.put("type", "zip");
		Communicator.get().request("putfile", obj);
	}
}
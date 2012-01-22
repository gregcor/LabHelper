import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JOptionPane;

import org.json.simple.JSONObject;


public class Communicator {
	private static String IP;
	private static int PORT;
	// Connection string to file containing a connection string for the FTP server.
    public static final String CSTRLOC = "http://dl.dropbox.com/u/1031798/lab-connection.txt";

	private static Communicator instance;
	public static Communicator get()
	{
		if(instance==null)
		{
			instance = new Communicator();
		}
		return instance;
	}
	private Socket mySock;
	private LinkedBlockingQueue<String> outQueue;
	public Communicator()
	{
		fillServerInfo();
		try {
			mySock = new Socket(IP, PORT);
			InputStream is = mySock.getInputStream();
			OutputStream os = mySock.getOutputStream();
			outQueue = new LinkedBlockingQueue<String>();
			new Thread(new InputReader(is, InputHandler.get())).start();
			new Thread(new OutputWriter(os, outQueue)).start();
					
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "Could not connect to the LabHelper server. Inform your instructor.");
			e.printStackTrace();
			System.exit(1);
		}
	}
	private void fillServerInfo()
	{
		 //Far too optimistic error handling. Everything crashes if there's no
        //internet connection.
        URLConnection stringConnection = null;
        try {
                stringConnection = (new URL(CSTRLOC)).openConnection();
        } catch (MalformedURLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
        } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
        }
        //Populate server var.
        try {
                InputStream cStream = stringConnection.getInputStream();
                InputStreamReader isr = new InputStreamReader(cStream);
                BufferedReader br = new BufferedReader(isr);
                IP = br.readLine();
                PORT = Integer.parseInt(br.readLine());
                System.out.println("Found server at: " + IP + ":" + PORT);
        } catch (Exception e) {
                // TODO Auto-generated catch block
        		JOptionPane.showMessageDialog(null, "Error reading server data. Inform your instructor.");
                e.printStackTrace();
                System.exit(1);
        }
	}
	public void request(String command, JSONObject params)
	{
		params.put("request", command);
		send(params.toJSONString());
	}
	public void send(String command)
	{
		outQueue.add(command);
	}
	private static class InputReader implements Runnable
	{
		private InputStream is;
		private InputHandler ih;

		public InputReader(InputStream is, InputHandler ih)
		{
			this.is = is;
			this.ih = ih;
		}
		@Override
		public void run() {
			Character lastOne = null;
			Character lastTwo = null;
			StringBuilder sb = new StringBuilder();
			int byteRead;
			try {
				byteRead = is.read();
				while(byteRead!=-1)
				{
					if(lastOne!=null)
					{
						sb.append(lastOne);
					}
					lastOne = lastTwo;
					lastTwo = (char)byteRead;
					if(lastOne!=null && lastOne.charValue()=='\r' && lastTwo.charValue()=='\n')
					{
						String toProcess = sb.toString();
						System.out.println("Got input: " + toProcess);
						ih.handleInput(toProcess);
						sb = new StringBuilder();
						lastOne = null;
						lastTwo = null;
					}
					byteRead = is.read();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "There was an error in communicating with the LabHelper server. Restart LabHelper.");
				System.exit(2);
			}
			
			
		}
	}
	private static class OutputWriter implements Runnable
	{
		private OutputStream os;
		private BufferedWriter bw;
		private LinkedBlockingQueue<String> queue;
		public OutputWriter(OutputStream os, LinkedBlockingQueue<String> queue)
		{
			this.os = os;
			bw = new BufferedWriter(new OutputStreamWriter(os));
			this.queue = queue;
		}
		public void run()
		{
			BufferedWriter logWriter = null;
			try {
				logWriter = new BufferedWriter(new FileWriter(new File("log.txt")));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			while(true)
			{
				try {
					String toProcess = queue.take();
					System.out.println("Sending: " + toProcess);
					logWriter.write(toProcess + "\r\n");
					bw.write(toProcess + "\r\n");
					bw.flush();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, "There was an error in communicating with the LabHelper server. Restart LabHelper.");
					System.exit(2);
					e.printStackTrace();
				}
				
			}
		}
	}
}

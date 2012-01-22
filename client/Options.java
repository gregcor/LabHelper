import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


public class Options {
	private static final String CONFIGFILE = ".labhelper.settings";
	private static Options instance;
	public static Options get()
	{
		if(instance==null)
		{
			instance = new Options();
		}
		return instance;
	}
	private static File getConfigFile()
	{
		String homePath = System.getProperty("user.home");
		File config = new File(homePath, CONFIGFILE);
		return config;
	}
	public JSONObject getCurrentOptions()
	{
		File config = getConfigFile();
		FileReader fr = null;
		try {
			fr = new FileReader(config);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			return new JSONObject();
		}
		BufferedReader br = new BufferedReader(fr);
		StringBuffer fileContents = new StringBuffer();
		String toRead;
		try {
			toRead = br.readLine();
			while(toRead!=null)
			{
				fileContents.append(toRead);
				toRead = br.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		Object obj = JSONValue.parse(fileContents.toString());
		return (JSONObject) obj;
	}
	public String readOption(String key)
	{
		JSONObject jobj = getCurrentOptions();
		if(jobj==null) return null;
		return (String)jobj.get(key);
	}
	public void storeOption(String key, String value)
	{
		HashMap<String,String> hm = new HashMap<String,String>();
		hm.put(key, value);
		storeOption(hm);
	}
	public void storeOption(Map<String, String> vals)
	{
		JSONObject jobj = getCurrentOptions();
		for(String currentKey : vals.keySet())
		{
			jobj.put(currentKey, vals.get(currentKey));
		}
		File toWrite = getConfigFile();
		
		try {
			FileWriter fw = new FileWriter(toWrite);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(jobj.toJSONString());
			bw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
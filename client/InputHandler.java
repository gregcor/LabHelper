import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
public class InputHandler {
	private static InputHandler instance;
	public static InputHandler get()
	{
		if(instance==null)
		{
			instance = new InputHandler();
		}
		return instance; 
	}
	private HashMap<String, List<InputProcessor>> mappings;
	private InputHandler()
	{
		mappings = new HashMap<String, List<InputProcessor>>();
	}
	public void handleInput(String input)
	{
		if(!LabHelperClient.checkIfStillUnique())
		{
			return;
		}
		JSONObject obj = (JSONObject)JSONValue.parse(input);
		String key = (String)obj.get("action");
		if(mappings.containsKey(key))
		{
			for(InputProcessor curProc: mappings.get(key))
			{
				curProc.processInput(obj);
			}
		}
	}
	public void registerInput(String inString, InputProcessor processor)
	{
		registerInput(inString, processor, false);
	}
	public void registerInput(String inString, InputProcessor processor, boolean clear)
	{
		if(!mappings.containsKey(inString) || clear)
		{
			mappings.put(inString, new ArrayList<InputProcessor>());
		}
		List<InputProcessor> procList = mappings.get(inString);
		procList.add(processor);
	}
}

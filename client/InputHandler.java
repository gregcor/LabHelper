import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
/**
 * Dispatcher for commands received from server
 * @author gcordts
 *
 */
public class InputHandler {
	//Singleton instance
	private static InputHandler instance;
	/**
	 * Get a singleton instance
	 * @return instance of InputHandler
	 */
	public static InputHandler get()
	{
		if(instance==null)
		{
			instance = new InputHandler();
		}
		return instance; 
	}
	//Mappings from commands to what should handle them
	private HashMap<String, List<InputProcessor>> mappings;
	private InputHandler()
	{
		mappings = new HashMap<String, List<InputProcessor>>();
	}
	/**
	 * Process input received from server
	 * @param input data to process
	 */
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
	/**
	 * Register a new InputProcessor with a command
	 * @param inString Data to attach to
	 * @param processor InputProcessor to handle the data
	 */
	public void registerInput(String inString, InputProcessor processor)
	{
		registerInput(inString, processor, false);
	}
	/**
	 * Register a new InputProcessor with a command
	 * @param inString Data to attach to
	 * @param processor InputProcessor to handle the data
	 * @param clear If true, rremove all other InputProcessors for this string
	 */
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

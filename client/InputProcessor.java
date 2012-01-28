import org.json.simple.JSONObject;

/**
 * An InputProcessor can respond to some server
 * action.
 * @author gcordts
 *
 */
public interface InputProcessor {
	/**
	 * Process some JSON data from the server
	 * @param input data to process
	 */
	public void processInput(JSONObject input);
}

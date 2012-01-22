import java.util.UUID;


public class LabHelperClient {
	private static String instanceid = UUID.randomUUID().toString();
	public static void main(String[] args)
	{
		storeInstanceID();
		MiscFunctions.registerMiscFunctions();
		LabHelperFrame.get().setVisible(true);
	}
	/**
	 * We generate an instance ID at startup. If the instance ID at upload
	 * doesn't match the one in the config file, we know this is an old instance
	 * of the program (a newer one was started) and this one should die.
	 */
	private static void storeInstanceID()
	{
		Options.get().storeOption("instanceid", instanceid);
	}
	public static boolean checkIfStillUnique() {
		if(Options.get().readOption("instanceid").equals(instanceid))
		{
			return true;
		}else
		{
			System.exit(0);
			return false;
		}
	}
}

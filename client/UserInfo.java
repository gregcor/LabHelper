/**
 * Container for UserInfo
 * @author gcordts
 *
 */
public class UserInfo {
	//Singleton instance
	private static UserInfo instance = null;
	/**
	 * Get UserInfo instance
	 * @return
	 */
	public static UserInfo get()
	{
		return instance;
	}
	private String fname;
	/***
	 * User first name
	 * @return user's first name
	 */
	public String getFname() {
		return fname;
	}
	/**
	 * User's last name
	 * @return User's last name
	 */
	public String getLname() {
		return lname;
	}
	/**
	 * Course username
	 * @return User's course username
	 */
	public String getUname() {
		return uname;
	}
	private String lname;
	private String uname;
	private UserInfo(String fname, String lname, String uname)
	{
		this.fname = fname;
		this.lname = lname;
		this.uname = uname;
	}
	/**
	 * Construct a new UserInfo object, replacing the older one
	 * @param fname User's first name
	 * @param lname User's last name
	 * @param uname User's user name
	 */
	public static void create(String fname, String lname, String uname)
	{
		instance = new UserInfo(fname, lname, uname);
	}
}

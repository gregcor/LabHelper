
public class UserInfo {
	private static UserInfo instance = null;
	public static UserInfo get()
	{
		return instance;
	}
	private String fname;
	public String getFname() {
		return fname;
	}
	public String getLname() {
		return lname;
	}
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
	public static void create(String fname, String lname, String uname)
	{
		instance = new UserInfo(fname, lname, uname);
	}
}

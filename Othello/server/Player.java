package server;

public class Player
{
	private int color; // 1=black, 0=white
	private String username;
	private long clientID;
	
	
	public Player(int color, String username, long clientID)
	{
		this.color = color;
		this.username = username;
		this.clientID = clientID;
	}
	
	public int getColor()
	{
		return color;
	}
	public void setColor(int color)
	{
		this.color = color;
	}
	public String getUsername()
	{
		return username;
	}
	public void setUsername(String username)
	{
		this.username = username;
	}
	public long getClientID()
	{
		return clientID;
	}
	public void setClientID(long clientID)
	{
		this.clientID = clientID;
	}
}

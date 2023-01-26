package client;

import java.io.Serializable;

import server.Player;

public class LoginData implements Serializable
{
  private String username;
  private String password;
  private Player player; // Player data received once user is successfully logged in
  
  // Getters for the username and password.
  public String getUsername()
  {
    return username;
  }
  public String getPassword()
  {
    return password;
  }
  
  // Setters for the username and password.
  public void setUsername(String username)
  {
    this.username = username;
  }
  public void setPassword(String password)
  {
    this.password = password;
  }
  
  // Constructor that initializes the username and password.
  public LoginData(String username, String password)
  {
    setUsername(username);
    setPassword(password);
  }
  
	public Player getPlayer()
	{
		return player;
	}
	
	public void setPlayer(Player player)
	{
		this.player = player;
	}

}

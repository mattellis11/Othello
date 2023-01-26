package client;

import java.io.Serializable;
import java.util.ArrayList;

import server.Player;

public class GameLobbyData implements Serializable
{
	private Player player1;
	private Player player2;
	private ArrayList<Player> online;
	private ArrayList<Player> waiting;
	
	public GameLobbyData(ArrayList<Player> online, ArrayList<Player> waiting)
	{
		setOnline(online);
		setWaiting(waiting);
	}

	public GameLobbyData(Player player)
	{
		setPlayer1(player);
	}

	public GameLobbyData(Player player, Player player2)
	{
		setPlayer1(player);
		setPlayer2(player2);
	}

	public Player getPlayer1()
	{
		return player1;
	}

	public void setPlayer1(Player player1)
	{
		this.player1 = player1;
	}

	public Player getPlayer2()
	{
		return player2;
	}

	public void setPlayer2(Player player2)
	{
		this.player2 = player2;
	}

	public ArrayList<Player> getOnline()
	{
		return online;
	}

	public void setOnline(ArrayList<Player> online)
	{
		this.online = online;
	}

	public ArrayList<Player> getWaiting()
	{
		return waiting;
	}

	public void setWaiting(ArrayList<Player> waiting)
	{
		this.waiting = waiting;
	}	
	
}

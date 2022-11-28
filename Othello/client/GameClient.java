package client;

import java.util.ArrayList;

import ocsf.client.AbstractClient;
import server.GameData;
import server.Player;

public class GameClient extends AbstractClient
{
	// controllers
	private LoginControl loginControl;
	private CreateAccountControl createAccountControl;
	private GameLobbyControl gameLobbyControl;
	private BoardControl boardControl;
	
	private ArrayList<Player> online;
	private ArrayList<Player> waiting;
		

	GameClient()
	{
		super("localhost", 8300);
	}

	@Override
	protected void handleMessageFromServer(Object arg0)
	{
		// If we received a String, figure out what this event is.
		if (arg0 instanceof String)
		{
			// Get the text of the message.
			String message = (String) arg0;

			// If we successfully logged in, tell the login controller.
			if (message.equals("LoginSuccessful"))
			{
				loginControl.loginSuccess();
			}

			// If we successfully created an account, tell the create account controller.
			else if (message.equals("CreateAccountSuccessful"))
			{
				createAccountControl.createAccountSuccess();
			}
		}
		
		// If we are receiving LoginData, then we have successfully logged in
		else if (arg0 instanceof LoginData)
		{
			LoginData data = (LoginData) arg0;
			
			// Send the player's username to the GameLobbyControl.
			//gameLobbyControl.setUsername(data.getUsername());
			gameLobbyControl.setPlayer(data.getPlayer());
			
			// Successfully logged in, tell the login controller.
			loginControl.loginSuccess();
		}

		// If we received an Error, figure out where to display it.
		else if (arg0 instanceof Error)
		{
			// Get the Error object.
			Error error = (Error) arg0;

			// Display login errors using the login controller.
			if (error.getType().equals("Login"))
			{
				loginControl.displayError(error.getMessage());
			}

			// Display account creation errors using the create account controller.
			else if (error.getType().equals("CreateAccount"))
			{
				createAccountControl.displayError(error.getMessage());
			}
		}
		else if (arg0 instanceof GameLobbyData)
		{
			GameLobbyData gld = (GameLobbyData) arg0;
			setOnline(gld.getOnline());
			setWaiting(gld.getWaiting());
			gameLobbyControl.updateGameLobby(gld);
		}
		else if (arg0 instanceof GameData)
		{			
			// If first time receiving GameData
			if (boardControl.getGameData() == null)
			{
				gameLobbyControl.startGame(); // load game panel
			}
			
			// Get the GameData object.
			GameData data = (GameData) arg0;
			
			// Pass data to BoardControl.
			boardControl.setGameData(data);
			
			// Update the game state.
			boardControl.setGameState();
		}

	}

	public void setLoginControl(LoginControl lc)
	{
		loginControl = lc;
	}

	public void setCreateAccountControl(CreateAccountControl cac)
	{
		createAccountControl = cac;
	}

	public void setGameLobbyControl(GameLobbyControl glc)
	{
		gameLobbyControl = glc;
	}

	public void setBoardControl(BoardControl bc)
	{
		boardControl = bc;
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

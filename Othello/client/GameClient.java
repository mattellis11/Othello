package client;

import ocsf.client.AbstractClient;
import server.GameData;

public class GameClient extends AbstractClient
{
	// controllers
	private LoginControl loginControl;
	private CreateAccountControl createAccountControl;
	private GameLobbyControl gameLobbyControl;
	private BoardControl boardControl;
		

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
		
		// If we receive game data
		else if (arg0 instanceof GameData)
		{
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
	
	

}

package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

public class GameLobbyControl implements ActionListener
{
	// Private data fields for the container and game client.
	private JPanel container;
	private GameClient client;

	public GameLobbyControl(JPanel container, GameClient client)
	{
		this.client = client;
		this.container = container;
	}

	@Override
	public void actionPerformed(ActionEvent ae)
	{
		// Get the name of the button clicked.
		String command = ae.getActionCommand();
		
		if (command == "Start Game")
		{
			/*
			 * add user to waiting for other players list
			 * begin game start requirements
			 */
		}
		
		else if (command == "Join Game")
		{
			/*
			 * get selected user
			 * finalize game start requirements
			 * switch to boardpanel
			 */
		}
		
		else if (command == "Quit")
		{
			/*
			 * remove player from online players
			 * quit the program
			 */
		}

	}

}

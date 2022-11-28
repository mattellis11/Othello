package client;

import java.awt.CardLayout;
import java.awt.GridBagLayout;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameGUI extends JFrame
{
	public GameGUI()
	{
		// Set up game client.
		GameClient client = new GameClient();
		try
		{
			client.openConnection();
		} catch (IOException e)
		{			
			e.printStackTrace();
		}

		// Set the title and default close operation.
		this.setTitle("Othello");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create the card layout container.
		CardLayout cardLayout = new CardLayout();
		JPanel container = new JPanel(cardLayout);

		// Create the Controllers
		InitialControl ic = new InitialControl(container);
		LoginControl lc = new LoginControl(container, client);
		CreateAccountControl cac = new CreateAccountControl(container, client);
		GameLobbyControl glc = new GameLobbyControl(container, client);
		BoardControl bc = new BoardControl(container, client);

		// Pass the controllers to the client.
		client.setLoginControl(lc);
		client.setCreateAccountControl(cac);
		client.setGameLobbyControl(glc);
		client.setBoardControl(bc);

		// Create the views and pass them the controllers.
		JPanel view1 = new InitialPanel(ic);
		JPanel view2 = new LoginPanel(lc);
		JPanel view3 = new CreateAccountPanel(cac);
		JPanel view4 = new GameLobbyPanel(glc);
		JPanel view5 = new BoardPanel(bc);

		// Add the views to the card layout container.
		container.add(view1, "initial");
		container.add(view2, "login");
		container.add(view3, "create");
		container.add(view4, "lobby");
		container.add(view5, "board");
		
	  // Show the initial view in the card layout.
    cardLayout.show(container, "initial");
    
    // Add the card layout container to the JFrame.
    // GridBagLayout makes the container stay centered in the window.
    this.setLayout(new GridBagLayout());
    this.add(container);

    // Show the JFrame.
    this.setSize(750, 625);
    this.setVisible(true);
    this.setLocationRelativeTo(null); // Centers JFrame on display
    
	}
	
 //Main function that creates the client GUI when the program is started.
 public static void main(String[] args)
 {
   new GameGUI();
 }

}

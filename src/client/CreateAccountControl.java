package client;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JPanel;

public class CreateAccountControl implements ActionListener
{
	// Private data fields for the container and game client.
	private JPanel container;
	private GameClient client;

	public CreateAccountControl(JPanel container, GameClient client)
	{
		this.container = container;
		this.client = client;
	}

	@Override
	public void actionPerformed(ActionEvent ae)
	{
		// Get the name of the button clicked.
		String command = ae.getActionCommand();

		// The Cancel button takes the user back to the initial panel.
		if (command == "Cancel")
		{
			CardLayout cardLayout = (CardLayout) container.getLayout();
			cardLayout.show(container, "initial");
		}

		// The Submit button creates a new account.
		else if (command == "Submit")
		{
			// Get the text the user entered in the three fields.
			CreateAccountPanel createAccountPanel = (CreateAccountPanel) container.getComponent(2);
			String username = createAccountPanel.getUsername();
			String password = createAccountPanel.getPassword();
			String passwordVerify = createAccountPanel.getPasswordVerify();

			// Check the validity of the information locally first.
			if (username.equals("") || password.equals(""))
			{
				displayError("You must enter a username and password.");
				return;
			} else if (!password.equals(passwordVerify))
			{
				displayError("The two passwords did not match.");
				return;
			}
			if (password.length() < 6)
			{
				displayError("The password must be at least 6 characters.");
				return;
			}

			// Submit the new account information to the server.
			CreateAccountData data = new CreateAccountData(username, password);
			try
			{
				client.sendToServer(data);
			} catch (IOException e)
			{
				displayError("Error connecting to the server.");
			}
		}

	}

	//After an account is created, set the User object and display the contacts screen.
	public void createAccountSuccess()
	{
		// Successful account creation takes the user to the loginPanel
		CardLayout cardLayout1 = (CardLayout) container.getLayout();
		cardLayout1.show(container, "login");
	}

	// Method that displays a message in the error label.
	public void displayError(String error)
	{
		CreateAccountPanel createAccountPanel = (CreateAccountPanel) container.getComponent(2);
		createAccountPanel.setError(error);
	}

}

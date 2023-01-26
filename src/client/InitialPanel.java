package client;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class InitialPanel extends JPanel
{

	public InitialPanel(InitialControl ic)
	{
		// Create the information label.
		JLabel label = new JLabel("Account Information", JLabel.CENTER);

		// Create the login button.
		JButton loginButton = new JButton("Login");
		loginButton.addActionListener(ic);
		JPanel loginButtonBuffer = new JPanel();
		loginButtonBuffer.add(loginButton);

		// Create the create account button.
		JButton createButton = new JButton("Create");
		createButton.addActionListener(ic);
		JPanel createButtonBuffer = new JPanel();
		createButtonBuffer.add(createButton);

		// Arrange the components in a grid.
		JPanel grid = new JPanel(new GridLayout(3, 1, 5, 5));
		grid.add(label);
		grid.add(loginButtonBuffer);
		grid.add(createButtonBuffer);
		this.add(grid);
	}

}

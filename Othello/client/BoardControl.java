package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

public class BoardControl implements ActionListener
{
  // Private data fields for the container and chat client.
	private JPanel container;
	private GameClient client;

	public BoardControl(JPanel container, GameClient client)
	{
		this.client = client;
		this.container = container;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// TODO Auto-generated method stub

	}

}

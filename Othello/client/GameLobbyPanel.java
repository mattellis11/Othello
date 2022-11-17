package client;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import java.awt.Dimension;
import java.awt.BorderLayout;
import javax.swing.SwingConstants;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.Component;
import javax.swing.Box;

public class GameLobbyPanel extends JPanel
{
	private JList<Object> onlineList; // use Player objects
	private JList<Object> joinList;

	public GameLobbyPanel(GameLobbyControl glc)
	{
		GameLobbyControl controller = glc;		
		
		// Set layout to absolute
		setLayout(null);
		
		
		// Online Players Components
		JPanel onlineLabelPanel = new JPanel();
		onlineLabelPanel.setBounds(-5, 5, 760, 24);
		add(onlineLabelPanel);
		JLabel onlineLabel = new JLabel("Players Online", JLabel.CENTER);
		onlineLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		onlineLabel.setPreferredSize(new Dimension(750, 14));
		onlineLabel.setMinimumSize(new Dimension(750, 14));
		onlineLabelPanel.add(onlineLabel);
		
		JPanel onlineListPanel = new JPanel();
		onlineListPanel.setBounds(245, 34, 260, 135);
		add(onlineListPanel);
		
		JScrollPane onlineScrollPane = new JScrollPane();
		onlineScrollPane.setPreferredSize(new Dimension(250, 125));
		onlineScrollPane.setMinimumSize(new Dimension(250, 75));
		onlineListPanel.add(onlineScrollPane);
		
		onlineList = new JList<Object>(); // use Player objects
		onlineScrollPane.setViewportView(onlineList);
		
		
		
		
		// Join Game Components 
		JPanel joinLabelPanel = new JPanel();
		joinLabelPanel.setBounds(0, 174, 750, 25);
		joinLabelPanel.setPreferredSize(new Dimension(750, 25));
		add(joinLabelPanel);
		
		JLabel joinLabel = new JLabel("Players Waiting For Another Player To Join A Game", JLabel.CENTER);
		joinLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		joinLabel.setPreferredSize(new Dimension(350, 14));
		joinLabel.setMinimumSize(new Dimension(350, 14));
		joinLabelPanel.add(joinLabel);
		
		JPanel joinListPanel = new JPanel();
		joinListPanel.setBounds(245, 204, 260, 135);
		add(joinListPanel);
		
		JScrollPane joinScrollPane = new JScrollPane();
		joinScrollPane.setPreferredSize(new Dimension(250, 125));
		joinListPanel.add(joinScrollPane);
		
		joinList = new JList<Object>();
		joinScrollPane.setViewportView(joinList);
		
		
		// Button Panel Components 
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBounds(0, 344, 750, 120);
		buttonPanel.setPreferredSize(new Dimension(750, 120));
		add(buttonPanel);
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		horizontalStrut.setPreferredSize(new Dimension(750, 20));
		buttonPanel.add(horizontalStrut);
		
		JPanel startButtonPanel = new JPanel();
		buttonPanel.add(startButtonPanel);
		
		JButton startButton = new JButton("Start Game");
		startButton.addActionListener(controller);
		startButtonPanel.add(startButton);
		
		JPanel joinButtonPanel = new JPanel();
		buttonPanel.add(joinButtonPanel);
		
		JButton joinButton = new JButton("Join Game");
		joinButton.addActionListener(controller);
		joinButtonPanel.add(joinButton);
		
		JPanel logoutButtonPanel = new JPanel();
		buttonPanel.add(logoutButtonPanel);
		
		JButton logoutButton = new JButton("Quit");
		logoutButton.addActionListener(controller);
		logoutButtonPanel.add(logoutButton);
		
		//  Finalize JPanel 
    this.setVisible(true);
	}

}

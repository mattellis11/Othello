package client;

import server.Player;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Implements a game lobby view
 *
 * @author Matt Ellis
 */
public class GameLobbyPanel extends JPanel {
    private final DefaultListModel<String> onlineListModel;
    private final DefaultListModel<String> waitingListModel;
    private final JList<String> onlineList;
    private final JList<String> waitingList;

    public GameLobbyPanel(GameLobbyControl glc) {

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

        onlineListModel = new DefaultListModel<>();
        onlineList = new JList<>(onlineListModel);
        JScrollPane onlineScrollPane = new JScrollPane(onlineList);
        onlineScrollPane.setPreferredSize(new Dimension(250, 125));
        onlineScrollPane.setMinimumSize(new Dimension(250, 75));
        onlineListPanel.add(onlineScrollPane);

        // Join Game Components
        JPanel joinLabelPanel = new JPanel();
        joinLabelPanel.setBounds(0, 174, 750, 25);
        joinLabelPanel.setPreferredSize(new Dimension(750, 25));
        add(joinLabelPanel);

        JLabel joinLabel = new JLabel("Players Waiting For Opponents", JLabel.CENTER);
        joinLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        joinLabel.setPreferredSize(new Dimension(350, 14));
        joinLabel.setMinimumSize(new Dimension(350, 14));
        joinLabelPanel.add(joinLabel);

        JPanel joinListPanel = new JPanel();
        joinListPanel.setBounds(245, 204, 260, 135);
        add(joinListPanel);

        waitingListModel = new DefaultListModel<>();
        waitingList = new JList<>(waitingListModel);
        JScrollPane joinScrollPane = new JScrollPane(waitingList);
        joinScrollPane.setPreferredSize(new Dimension(250, 125));
        joinListPanel.add(joinScrollPane);

        // Button Panel Components
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBounds(0, 344, 750, 90);
        buttonPanel.setPreferredSize(new Dimension(750, 120));
        add(buttonPanel);

        Component horizontalStrut = Box.createHorizontalStrut(20);
        horizontalStrut.setPreferredSize(new Dimension(750, 20));
        buttonPanel.add(horizontalStrut);

        JPanel startButtonPanel = new JPanel();
        buttonPanel.add(startButtonPanel);

        JButton startButton = new JButton("Start Game");
        startButton.addActionListener(glc);
        startButtonPanel.add(startButton);

        JPanel joinButtonPanel = new JPanel();
        buttonPanel.add(joinButtonPanel);

        JButton joinButton = new JButton("Join Game");
        joinButton.addActionListener(glc);
        joinButtonPanel.add(joinButton);

        JPanel logoutButtonPanel = new JPanel();
        buttonPanel.add(logoutButtonPanel);

        JButton logoutButton = new JButton("Quit");
        logoutButton.addActionListener(glc);
        logoutButtonPanel.add(logoutButton);

        // Finalize JPanel
        this.setSize(750, 625);

        JPanel instructionsPanel = new JPanel();
        instructionsPanel.setBounds(29, 444, 711, 129);
        add(instructionsPanel);
        instructionsPanel.setLayout(null);

        JLabel lblNewLabel = new JLabel("Instructions:");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 9));
        lblNewLabel.setBounds(10, 0, 139, 13);
        instructionsPanel.add(lblNewLabel);

        JLabel lblNewLabel_1 = new JLabel("Press 'Start Game' to create a new game and add your username to the 'Players Waiting For Opponents' list. Once another player joins, the game will begin.");
        lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 8));
        lblNewLabel_1.setBounds(10, 23, 691, 13);
        instructionsPanel.add(lblNewLabel_1);

        JLabel lblNewLabel_2 = new JLabel("To join a new game, choose the player you would like to challenge from the 'Players Waiting For Opponents' list. Then press 'Join Game'.");
        lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 8));
        lblNewLabel_2.setBounds(10, 46, 663, 13);
        instructionsPanel.add(lblNewLabel_2);

        JLabel lblNewLabel_3 = new JLabel("Press 'Quit' to log out and exit the game.");
        lblNewLabel_3.setFont(new Font("Tahoma", Font.BOLD, 8));
        lblNewLabel_3.setBounds(10, 69, 299, 13);
        instructionsPanel.add(lblNewLabel_3);
        this.setVisible(true);
    }

    public JList<String> getOnlineList() {
        return onlineList;
    }

    public JList<String> getWaitingList() {
        return waitingList;
    }

    public DefaultListModel<String> getOnlineListModel() {
        return onlineListModel;
    }

    public DefaultListModel<String> getWaitingListModel() {
        return waitingListModel;
    }

    public void updateLists(GameLobbyData data) {
        ArrayList<Player> online = data.getOnline();
        ArrayList<Player> waiting = data.getWaiting();

        onlineListModel.clear();
        waitingListModel.clear();

        for (Player player : online) {
            onlineListModel.addElement(player.getUsername());
        }

        for (Player player : waiting) {
            waitingListModel.addElement(player.getUsername());
        }

    }
}

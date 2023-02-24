package client;

import server.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Controller for a game lobby view
 *
 * @author Matt Ellis
 */
public class GameLobbyControl implements ActionListener {
    // Private data fields for the container and game client.
    private final JPanel container;
    private final GameClient client;

    private String username;
    private Player player;

    public GameLobbyControl(JPanel container, GameClient client) {
        this.client = client;
        this.container = container;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        // Get the name of the button clicked.
        String command = ae.getActionCommand();

        if (command.equals("Start Game")) {
            /*
             * add user to waiting for other players list
             * GameLobbyData data = new GameLobbyData(player, null);
             * client.sentToServer(data);
             * send Player to server, server adds player to waitingList, sends waiting list to all
             */
            GameLobbyData data = new GameLobbyData(player);
            try {
                client.sendToServer(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (command.equals("Join Game")) {
            /*
             * get selected user, if selected player == player, show error, return
             * else GameLobbyData data = new GameLobbyData(getSelectedPlayer, player)
             * client.sendToServer(data)
             */
            GameLobbyPanel glp = (GameLobbyPanel) container.getComponent(3);
            String opponent = glp.getWaitingList().getSelectedValue();

            // If the player has chosen themselves, return.
            if (player.getUsername().equals(opponent)) return;

            Player player2 = null;
            for (Player player : client.getWaiting()) {
                if (player.getUsername().equals(opponent)) {
                    player2 = player;
                    break;
                }
            }
            GameLobbyData data = new GameLobbyData(player, player2);
            try {
                client.sendToServer(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // If quit is chosen, exit the application
        else if (command.equals("Quit")) {
            try {
                client.closeConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }

    }

    public void updateGameLobby(GameLobbyData gld) {
        GameLobbyPanel glp = (GameLobbyPanel) container.getComponent(3);
        glp.updateLists(gld);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    // Display the board panel
    public void startGame() {
        CardLayout layout = (CardLayout) container.getLayout();
        layout.show(container, "board");
    }


}

package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Controller for the login panel
 *
 * @author Matt Ellis
 */
public class LoginControl implements ActionListener {
    // Private data fields for the container and chat client.
    private final JPanel container;
    private final GameClient client;

    public LoginControl(JPanel container, GameClient client) {
        this.container = container;
        this.client = client;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        // Get the name of the button clicked.
        String command = ae.getActionCommand();

        // The Cancel button takes the user back to the initial panel.
        if (command.equals("Cancel")) {
            CardLayout cardLayout = (CardLayout) container.getLayout();
            cardLayout.show(container, "initial");
        }

        // The Submit button submits the login information to the server.
        else if (command.equals("Submit")) {
            // Get the username and password the user entered.
            LoginPanel loginPanel = (LoginPanel) container.getComponent(1);
            LoginData data = new LoginData(loginPanel.getUsername(), loginPanel.getPassword());

            // Check the validity of the information locally first.
            if (data.getUsername().equals("") || data.getPassword().equals("")) {
                displayError("You must enter a username and password.");
                return;
            }

            // Submit the login information to the server.
            try {
                client.sendToServer(data);
            } catch (IOException e) {
                displayError("Error connecting to the server.");
            }

        }

    }

    // After the login is successful, load the game lobby panel
    public void loginSuccess() {
        CardLayout cardLayout = (CardLayout) container.getLayout();
        cardLayout.show(container, "lobby");
    }

    // Method that displays a message in the error label.
    public void displayError(String error) {
        LoginPanel loginPanel = (LoginPanel) container.getComponent(1);
        loginPanel.setError(error);
    }

}

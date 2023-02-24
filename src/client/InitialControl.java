package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controller for the initial screen
 *
 * @author Matt Ellis
 */
public class InitialControl implements ActionListener {
    // Private data field for storing the container.
    private final JPanel container;

    public InitialControl(JPanel container) {
        this.container = container;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Get the name of the button clicked.
        String command = e.getActionCommand();

        // The Login button takes the user to the login panel.
        if (command.equals("Login")) {
            CardLayout cardLayout = (CardLayout) container.getLayout(); // grab the layout manager
            cardLayout.show(container, "login"); // show LoginPanel
        }

        // The Create button takes the user to the create account panel.
        else if (command.equals("Create")) {
            CardLayout cardLayout = (CardLayout) container.getLayout();
            cardLayout.show(container, "create");
        }

    }

}

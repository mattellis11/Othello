package server;

import client.CreateAccountData;
import client.GameLobbyData;
import client.LoginData;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Creates the server.
 *
 * @author Matt Ellis
 */
public class GameServer extends AbstractServer {
    private JTextArea log;
    private JLabel status;
    private boolean running = false;
    private Database database;
    private GameMaster master;
    private final ArrayList<Player> online = new ArrayList<>();
    private final ArrayList<Player> waiting = new ArrayList<>();

    public GameServer() {
        super(8300);
        this.setTimeout(500);
    }

    @Override
    protected void handleMessageFromClient(Object arg0, ConnectionToClient arg1) {

        // If we received LoginData, verify the account information.
        if (arg0 instanceof LoginData) {

            // Check the username and password with the database.
            LoginData data = (LoginData) arg0;
            Object result;

            // Create database query to verify Login credentials.
            String query = String.format("select username, aes_decrypt(password, 'passkey') from user where username='%s'",
                    data.getUsername());
            ArrayList<String> queryResults = database.query(query);

            // queryResults will have exactly one element a String in the form "username,password"
            if (queryResults != null) {
                String[] credentials = queryResults.get(0).split(",", 2); // credentials = ["username","password"]

                // Check database password against password supplied by client
                if (credentials[1].equals(data.getPassword())) {
                    log.append("Client " + arg1.getId() + " successfully logged in as " + data.getUsername() + "\n");

                    // Create player object
                    Player player = new Player(data.getUsername(), arg1.getId());
                    data.setPlayer(player); // add player to login data

                    // Send LoginData back to client. Receipt of LoginData by client indicates successful login.
                    result = data;

                    // Add client to online players
                    online.add(player);

                    // Send updated list to all clients.
                    GameLobbyData lobbyData = new GameLobbyData(online, waiting);
                    if (getNumberOfClients() < 2)
                        try {
                            arg1.sendToClient(lobbyData);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    else
                        sendToAllClients(lobbyData);
                } else {
                    result = new Error("The username and password are incorrect.", "Login");
                    log.append("Client " + arg1.getId() + " failed to log in\n");
                }
            }

            // Case: queryResults == null, no results found.
            else {
                result = new Error("The username doesn't exist.", "Login");
                log.append("Client " + arg1.getId() + " attempted to log in with an unknown username.\n");
            }

            // Send the result to the client.
            try {
                arg1.sendToClient(result);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        // If we received CreateAccountData, add the login information into the database
        else if (arg0 instanceof CreateAccountData) {
            CreateAccountData data = (CreateAccountData) arg0;
            Object result;
            String dml = String.format("insert into user values ('%s', aes_encrypt('%s','passkey'))", data.getUsername(),
                    data.getPassword());
            try {
                database.executeDML(dml);
                result = "CreateAccountSuccessful";
                log.append("Client " + arg1.getId() + " created a new account called " + data.getUsername() + "\n");
            } catch (SQLException e1) // Could not add user to database
            {
                result = new Error("The username is already in use.", "CreateAccount");
                log.append("Client " + arg1.getId() + " failed to create a new account\n");
            }

            // Send the result to the client.
            try {
                arg1.sendToClient(result);
            } catch (IOException e) {
                return;
            }
        }

        // If we received GameLobbyData, a player has chosen to start a game or join a game.
        else if (arg0 instanceof GameLobbyData) {
            GameLobbyData data = (GameLobbyData) arg0;

            // Case: A client has pressed 'Start Game'.
            if (data.getPlayer2() == null) {
                // Add client to waitingList
                waiting.add(data.getPlayer1());

                // Send updated list to all clients.
                GameLobbyData lobbyData = new GameLobbyData(online, waiting);
                if (getNumberOfClients() < 2)
                    try {
                        arg1.sendToClient(lobbyData);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                else
                    sendToAllClients(lobbyData);
            }

            // Case: A client has pressed 'Join Game'.
            else if (data.getPlayer2() != null) {
                // Create a new game.
                GameData newGame = master.newGame(data.getPlayer1(), data.getPlayer2());

                // Determine first player (black).
                Player first = newGame.getPlayer1().getColor() == 1 ? newGame.getPlayer1() : newGame.getPlayer2();

                // Get clients' connections.
                Thread player1 = getClient(newGame.getPlayer1().getClientID());
                Thread player2 = getClient(newGame.getPlayer2().getClientID());

                // Initially activePlayer data field is set to false. Send white the new game data.
                if (newGame.getPlayer1() != first) {
                    try {
                        ((ConnectionToClient) player1).sendToClient(newGame);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        ((ConnectionToClient) player2).sendToClient(newGame);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // Prepare GameData for black.
                newGame.setActivePlayer(true);

                // Send GameData to black.
                if (newGame.getPlayer1() == first) {
                    try {
                        ((ConnectionToClient) player1).sendToClient(newGame);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        ((ConnectionToClient) player2).sendToClient(newGame);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        // If GameData is received, a player has placed a new piece on the board. Update the game.
        else if (arg0 instanceof GameData) {
            GameData data = (GameData) arg0;

            // Get the player that sent the move.
            Player sender = data.getPlayer1().getClientID() == arg1.getId() ? data.getPlayer1() : data.getPlayer2();

            // Process the new move and update the game state for the opponent.
            master.placePiece(data, sender.getColor());

            // Get the connection for the opponent.
            long clientId = data.getOpponent(arg1.getId());
            Thread opponent = getClient(clientId);

            // Check if there are available valid moves for the opponent. Update and send GameData to clients.
            if (data.isMoveAvailable()) {

                // Set active player to false, and return GameData to sender.
                data.setActivePlayer(false);
                try {
                    arg1.sendToClient(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Set active player to true, and send GameData to opponent.
                data.setActivePlayer(true);
                try {
                    ((ConnectionToClient) opponent).sendToClient(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Case: Opponent had no legal moves.
            else {

                // Calculate available valid moves for the player who made the last move.
                master.setAvailableMoves(data, sender.getColor());

                // Check if there are available valid moves. Update and send GameData to clients.
                if (data.isMoveAvailable()) {

                    // Set active player to false, and send to opponent.
                    data.setActivePlayer(false);
                    try {
                        ((ConnectionToClient) opponent).sendToClient(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Set active player to true, and send GameData to player who made the last move.
                    data.setActivePlayer(true);
                    try {
                        arg1.sendToClient(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // Case: Neither player can make a move. End game triggers. Update and send GameData to clients.
                else {
                    data.setGameOver(true);
                    try {
                        arg1.sendToClient(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        ((ConnectionToClient) opponent).sendToClient(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


    }


    // Returns ConnectionToClient object given the clientId, or null if the client is not found.
    private Thread getClient(long clientId) {
        Thread[] clients = getClientConnections();
        for (Thread client : clients) {
            if (client.getId() == clientId) return client;
        }
        return null; // client not found
    }


    // When the server starts, update the GUI.
    @Override
    public void serverStarted() {
        running = true;
        status.setText("Listening");
        status.setForeground(Color.GREEN);
        log.append("Server started\n");
    }


    // When the server stops listening, update the GUI.
    @Override
    public void serverStopped() {
        status.setText("Stopped");
        status.setForeground(Color.RED);
        log.append("Server stopped accepting new clients - press Listen to start accepting new clients\n");
    }


    // When the server closes completely, update the GUI.
    @Override
    public void serverClosed() {
        running = false;
        status.setText("Close");
        status.setForeground(Color.RED);
        log.append("Server and all current clients are closed - press Listen to restart\n");
    }


    // When a client connects, display a message in the log.
    @Override
    public void clientConnected(ConnectionToClient client) {
        log.append("Client " + client.getId() + " connected\n");
    }


    // When a client disconnects, remove them from the online list. Then, send updated list to all clients.
    @Override
    public void clientDisconnected(ConnectionToClient client) {
        log.append("Client " + client.getId() + " disconnected\n");
        long clientId = client.getId();
        for (int i = 0; i < online.size(); i++) {
            if (online.get(i).getClientID() == clientId) {
                online.remove(i);
                break;
            }
        }

        // Send updated list to all clients.
        GameLobbyData gld = new GameLobbyData(online, waiting);
        sendToAllClients(gld);
    }


    // Method that handles listening exceptions by displaying exception information.
    @Override
    public void listeningException(Throwable exception) {
        running = false;
        status.setText("Exception occurred while listening");
        status.setForeground(Color.RED);
        log.append("Listening exception: " + exception.getMessage() + "\n");
        log.append("Press Listen to restart server\n");
    }


    // Getter that returns whether the server is currently running.
    public boolean isRunning() {
        return running;
    }


    // Setters for the data fields corresponding to the GUI elements.
    public void setLog(JTextArea log) {
        this.log = log;
    }


    public void setStatus(JLabel status) {
        this.status = status;
    }


    public void setDatabase(Database database) {
        this.database = database;
    }


    public void setGameMaster(GameMaster master) {
        this.master = master;
    }


}

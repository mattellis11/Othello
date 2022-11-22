package server;

import java.awt.Color;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JTextArea;

import client.CreateAccountData;
import client.GameLobbyData;
import client.LoginData;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

public class GameServer extends AbstractServer
{	
	private JTextArea log;
	private JLabel status;
	private boolean running = false;
	private Database database;
	private GameMaster gameMaster;
	private ArrayList<String> onlinePlayers;
	private ArrayList<Player> waitingList;

	public GameServer()
	{
		super(8300);
		this.setTimeout(500);
	}

	@Override
	protected void handleMessageFromClient(Object arg0, ConnectionToClient arg1)
	{
		// If we received LoginData, verify the account information.
		if (arg0 instanceof LoginData)
		{
			// Check the username and password with the database.
			LoginData data = (LoginData) arg0;
			Object result;
			// Create database query to verify Login credentials.
			String query = String.format("select username, aes_decrypt(password, 'passkey') from user where username='%s'",
					data.getUsername());
			ArrayList<String> queryResults = database.query(query);

			// Case queryResults == null: The username was not found in the database.
			// If username is found, queryResults will have exactly one element a String in
			// the form "username,password"
			if (queryResults != null)
			{
				String[] credentals = queryResults.get(0).split(",", 2); // credentials = ["username","password"]

				// Check database password against password supplied by client
				if (credentals[1].equals(data.getPassword()))
				{
					result = "LoginSuccessful";
	        log.append("Client " + arg1.getId() + " successfully logged in as " + data.getUsername() + "\n");
				} else
				{
					result = new Error("The username and password are incorrect.", "Login");
	        log.append("Client " + arg1.getId() + " failed to log in\n");
				}
			} else // queryResults == null
			{
				result = new Error("The username doesn't exist.", "Login");
        log.append("Client " + arg1.getId() + " attempted to log in with an unknown username.\n");
			}
			
			// Send the result to the client.
      try
      {
        arg1.sendToClient(result);
      }
      catch (IOException e)
      {
        return;
      }
			
		} else if (arg0 instanceof CreateAccountData)
		{
			CreateAccountData data = (CreateAccountData) arg0;
			Object result;
			String dml = String.format("insert into user values ('%s', aes_encrypt('%s','passkey'))", data.getUsername(),
					data.getPassword());
			try
			{
				database.executeDML(dml);
        result = "CreateAccountSuccessful";
        log.append("Client " + arg1.getId() + " created a new account called " + data.getUsername() + "\n");
			} catch (SQLException e1) // Could not add user to database
			{
				result = new Error("The username is already in use.", "CreateAccount");
        log.append("Client " + arg1.getId() + " failed to create a new account\n");
			}
			
      // Send the result to the client.
      try
      {
        arg1.sendToClient(result);
      }
      catch (IOException e)
      {
        return;
      }			
		}
		// GameLobbyData is received when a player starts a new game or a player joins a game.
		else if (arg0 instanceof GameLobbyData)
		{
			GameLobbyData data = (GameLobbyData) arg0;
			
			// Case: A client has pressed 'Start Game'.
			if (data.getPlayer2() == null)
			{
				// Store sender's client id in player1 object.
				data.getPlayer1().setClientID(arg1.getId());
				
				// Add client to waitingList
				waitingList.add(data.getPlayer1());
				
				// Send waiting list to all clients.
				
			}
			// Case: A client has pressed 'Join Game'.
			else if (data.getPlayer2() != null)
			{
				// Create a new game.
				GameData newGame = gameMaster.newGame();
				
				// Set the players
				newGame.setPlayer1(data.getPlayer1());
				newGame.setPlayer2(data.getPlayer2());
				
				// Player1 will play black. Player2 will play white.
				newGame.getPlayer1().setColor(1);
				newGame.getPlayer2().setColor(0);
				
				// By default, GameData activePlayer data field is set to false.
				// First send GameData to player2.
				ConnectionToClient player2 = getClient(newGame.getPlayer2().getClientID());
				try
				{
					player2.sendToClient(newGame);
				} catch (IOException e)
				{
					e.printStackTrace();
				}
				
				// Prepare GameData for player1.
				newGame.setActivePlayer(true);
				int[][] availableMoves = gameMaster.availableMoves(newGame.getBoardState(), newGame.getPlayer1().getColor());
				newGame.setValidMoves(availableMoves);
				
				// Send GameData to player1.
				ConnectionToClient player1 = getClient(newGame.getPlayer1().getClientID());
				try
				{
					player1.sendToClient(newGame);
				} catch (IOException e)
				{					
					e.printStackTrace();
				}
			}
		}
		
		// GameData is received when a player places a new piece on the board.		 
		else if (arg0 instanceof GameData)
		{
			GameData data = (GameData) arg0;
			
			// Get player that sent the move.
			Player player = data.getPlayer1().getClientID() == arg1.getId() ? data.getPlayer1() : data.getPlayer2();
						
			// Process new move.
			gameMaster.placePiece(data, player.getColor());
			
			// Set active player to false, and return GameData to sender.
			data.setActivePlayer(false);
			try
			{
				arg1.sendToClient(data);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			
			// Set active player to true, and send GameData to opponent.
			data.setActivePlayer(true);
			
			// Get opponents clientId and ConnectToClient object
			long clientId = data.getOpponent(arg1.getId());
			ConnectionToClient opponent = getClient(clientId);
			
			// Send GameData to opponent.
			try
			{
				opponent.sendToClient(data);
			} catch (IOException e)
			{				
				e.printStackTrace();
			}			
		
			
		}
			
	}
	
	// Returns ConnectionToClient object given the clientId.
	private ConnectionToClient getClient(long clientId)
	{
		ConnectionToClient[] clients = (ConnectionToClient[]) getClientConnections();
		for (ConnectionToClient client: clients)
		{
			if (client.getId() == clientId) return client;
				
		}
		return null;
	}

	// When the server starts, update the GUI.
	public void serverStarted()
	{
		running = true;
		status.setText("Listening");
		status.setForeground(Color.GREEN);
		log.append("Server started\n");
	}

	// When the server stops listening, update the GUI.
	public void serverStopped()
	{
		status.setText("Stopped");
		status.setForeground(Color.RED);
		log.append("Server stopped accepting new clients - press Listen to start accepting new clients\n");
	}

	// When the server closes completely, update the GUI.
	public void serverClosed()
	{
		running = false;
		status.setText("Close");
		status.setForeground(Color.RED);
		log.append("Server and all current clients are closed - press Listen to restart\n");
	}

	// When a client connects or disconnects, display a message in the log.
	public void clientConnected(ConnectionToClient client)
	{
		log.append("Client " + client.getId() + " connected\n");
	}

	// Method that handles listening exceptions by displaying exception information.
	public void listeningException(Throwable exception)
	{
		running = false;
		status.setText("Exception occurred while listening");
		status.setForeground(Color.RED);
		log.append("Listening exception: " + exception.getMessage() + "\n");
		log.append("Press Listen to restart server\n");
	}

	// Getter that returns whether the server is currently running.
	public boolean isRunning()
	{
		return running;
	}

	// Setters for the data fields corresponding to the GUI elements.
	public void setLog(JTextArea log)
	{
		this.log = log;
	}

	public void setStatus(JLabel status)
	{
		this.status = status;
	}
	
	public void setDatabase(Database database)
	{
		this.database = database;
	}

	public void setGameMaster(GameMaster gameMaster)
	{
		this.gameMaster = gameMaster;
	}

}

package client;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import server.GameData;
import server.Position;

public class BoardControl implements ActionListener
{
  // Private data fields for the container and game client.
	private JPanel container;
	private GameClient client;
	
  //Constants for board images.
	private static final ImageIcon WHITE_PIECE = new ImageIcon(BoardControl.class.getResource("/client/images/white.png")); 
	private static final ImageIcon BLACK_PIECE = new ImageIcon(BoardControl.class.getResource("/client/images/black.png"));
	private static final ImageIcon EMPTY_SQUARE = new ImageIcon(BoardControl.class.getResource("/client/images/empty.png"));
	private static final ImageIcon VALID_MOVE = new ImageIcon(BoardControl.class.getResource("/client/images/available.png"));
	
	// Constants for board state.
	private static final int BLACK = 1;
	private static final int WHITE = 0;
	private static final int EMPTY = -99;
	
	// Constant for valid move array.
	private static final int VALID = 99;	
	
	private GameData data = null;
	
	
	public BoardControl(JPanel container, GameClient client)
	{
		this.client = client;
		this.container = container;
	}

	@Override
	public void actionPerformed(ActionEvent ae)
	{
		// Check if active player, return if not active player. Essentially disable buttons. 
		if (!data.isActivePlayer()) return;
				
		String buttonPressed = ae.getActionCommand(); // values 0 - 63		
		
		// Convert button pressed to correlating board position.
		int row = Integer.parseInt(buttonPressed) / 8;
		int col = Integer.parseInt(buttonPressed) % 8;		
		
		// Before sending to server, check if move is valid.
		int[][] validMoves = data.getValidMoves();
		if (validMoves[row][col] != VALID) return;		
		
		// Set move and send GameData to server.
		Position position = new Position(col, row);
		data.setMove(position);
		try
		{
			client.sendToServer(data);
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}
	
	// Sets the game square icons given the board state. If active player, includes available moves.
	public void setGameState()
	{
		// Get board state and list of valid moves from the game data.
		int[][] state = data.getBoardState();
		int[][] validMoves = data.getValidMoves();
		
		// Get the buttons from the panel.
		BoardPanel panel = (BoardPanel) container.getComponent(4);
		JButton[] gameSquares = panel.getSquares();
		
		// Read the board state and apply the appropriate graphics to the buttons.
		int count = 0;		
		for (int row = 0; row < 8; row++)
		{
			for (int col = 0; col < 8; col++)
			{
				if (state[row][col] == BLACK)
				{
					gameSquares[count].setIcon(BLACK_PIECE);
				}
				else if (state[row][col] == WHITE)
				{
					gameSquares[count].setIcon(WHITE_PIECE);
				}
				else if (data.isActivePlayer() && validMoves[row][col] == VALID && state[row][col] == EMPTY)
				{
					gameSquares[count].setIcon(VALID_MOVE);
				}
				else
				{
					gameSquares[count].setIcon(EMPTY_SQUARE);
				}
				count++;
			}
		}
		
		// Set the scores.
		panel.setScore(String.valueOf(data.getBlackScore()), String.valueOf(data.getWhiteScore()));
		
		// Set information message.
		if (data.isActivePlayer())
		{
			panel.setInfo("Your turn.");
		}
		else
		{
			panel.setInfo("Opponent's turn.");
		}
		
	}	

	public void setGameData(GameData data)
	{
		this.data = data;
	}
	
	public GameData getGameData()
	{
		return data;
	}

}

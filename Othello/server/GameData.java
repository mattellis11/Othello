/*
 * This class maintains the game state.
 */
package server;

public class GameData
{
	Player player1;
	Player player2; 	
	int[][] boardState; // position of the pieces on the board
	boolean activePlayer;
	int [][] validMoves; // list of available moves for the active player
	int blackScore;  
	int whiteScore; 
	Position move; // position of piece placed by the active player
	
	
	public int[][] getBoardState()
	{
		return boardState;
	}

	public void setBoardState(int[][] boardState)
	{
		this.boardState = boardState;
	}

	public int[][] getValidMoves()
	{
		return validMoves;
	}

	public void setValidMoves(int[][] validMoves)
	{
		this.validMoves = validMoves;
	}

	public Position getMove()
	{
		return move;
	}

	public void setMove(Position move)
	{
		this.move = move;
	}

	public long getOpponent(long clientId)
	{
		return player1.getClientID() == clientId ? player2.getClientID() : player1.getClientID();
	}
	
	public void setActivePlayer(Boolean bool)
	{
		activePlayer = bool;
	}
	
	public boolean isActivePlayer()
	{
		return activePlayer;
	}

	
	public int getBlackScore()
	{
		return blackScore;
	}
	
	public int getWhiteScore()
	{
		return whiteScore;
	}
	
	public Player getPlayer1()
	{
		return player1;
	}

	public void setPlayer1(Player player1)
	{
		this.player1 = player1;
	}

	public Player getPlayer2()
	{
		return player2;
	}

	public void setPlayer2(Player player2)
	{
		this.player2 = player2;
	}

	public void setBlackScore(int blackScore)
	{
		this.blackScore = blackScore;
	}

	public void setWhiteScore(int whiteScore)
	{
		this.whiteScore = whiteScore;
	}
	
	
}

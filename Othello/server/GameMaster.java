/*
 * Class implements the game rules.
 */
package server;

import java.util.Stack;

public class GameMaster
{
	// Constants for board state.
	private static final int BLACK = 1;
	private static final int WHITE = 0;
	private static final int EMPTY = -99;
	
	// Constant for valid move array.
	private static final int VALID = 99;
	
		
	public GameMaster()
	{
		
	}
	
	// Method for initializing a new game. Sets starting positions and scores.
	public GameData newGame()
	{
		GameData data = new GameData();
		
		// Set up starting board.
		int[][] boardState = new int[8][8];
		for (int y = 1; y < 9; y++)
		{
			for (int x = 1; x < 9; x++)
			{
				if (y == 4 && x == 4 || y == 5 && x == 5)
				{
					boardState[y][x] = WHITE;					
				} 
				else if (y == 5 && x == 4 || y == 4 && x == 5)
				{
					boardState[y][x] = BLACK;
				} 
				else
				{
					boardState[y][x] = EMPTY;
				}
			}			
		}
		
		// Set starting black and white scores.
		data.setBlackScore(2);
		data.setWhiteScore(2);		
		
		return data;
	}
	
	// Method for placing a newly played piece.
	public GameData placePiece(GameData data, int color)
	{
		int playedColor = color;
		int opponentColor = playedColor == BLACK ? WHITE : BLACK;
		Position newPiece = data.getMove();
		Stack<Position> toFlip;
		
		toFlip = check4FlipUpLeft(newPiece, playedColor, opponentColor, data.getBoardState());
		data = flipPieces(toFlip, data, playedColor);
		
		toFlip = check4FlipUp(newPiece, playedColor, opponentColor, data.getBoardState());
		data = flipPieces(toFlip, data, playedColor);
		
		toFlip = check4FlipUpRight(newPiece, playedColor, opponentColor, data.getBoardState());
		data = flipPieces(toFlip, data, playedColor);
		
		toFlip = check4FlipLeft(newPiece, playedColor, opponentColor, data.getBoardState());
		data = flipPieces(toFlip, data, playedColor);
		
		toFlip = check4FlipRight(newPiece, playedColor, opponentColor, data.getBoardState());
		data = flipPieces(toFlip, data, playedColor);
		
		toFlip = check4FlipDownLeft(newPiece, playedColor, opponentColor, data.getBoardState());
		data = flipPieces(toFlip, data, playedColor);
		
		toFlip = check4FlipDown(newPiece, playedColor, opponentColor, data.getBoardState());
		data = flipPieces(toFlip, data, playedColor);
		
		toFlip = check4FlipDownRight(newPiece, playedColor, opponentColor, data.getBoardState());
		data = flipPieces(toFlip, data, playedColor);
		
		return data;
	}
	
	// Flips the opponent's pieces and adjust the scores.
	private GameData flipPieces(Stack<Position> toFlip, GameData data, int playedColor)
	{				
		// Get the number of pieces to be flipped.
		int numOfPiecesFlipped = toFlip.size();
		
		// Get the board state.
		int[][] state = data.getBoardState();
		
		// Flip the opponents pieces.
		while (!toFlip.isEmpty())
		{
			Position piece = toFlip.pop();
			state[piece.y][piece.x] = playedColor; 
		}
		
		// Adjust the scores.
		if (playedColor == BLACK)
		{
			data.setBlackScore(data.getBlackScore() + numOfPiecesFlipped + 1);
			data.setWhiteScore(data.getWhiteScore() - numOfPiecesFlipped);
		}
		else
		{
			data.setWhiteScore(data.getWhiteScore() + numOfPiecesFlipped + 1);
			data.setBlackScore(data.getBlackScore() - numOfPiecesFlipped);
		}
		
		return data;		
		
	}
		
	// Checks for flips in the indicated direction.
	private Stack<Position> check4FlipUpLeft(Position position, int playedColor, int opponentColor, int[][] state)
	{
		Stack<Position> toFlip = new Stack<>();
		
		Position positionToCheck = new Position(position.x - 1, position.y - 1);
		
		// If the position to check is off the board, return null.
		if (chk4OutOfBounds(positionToCheck)) return null;
		
		// While the position to check is the opponents color, check the next position.
		while (state[positionToCheck.y][positionToCheck.x] == opponentColor)
		{
			// Add opponents piece to the stack.
			toFlip.add(positionToCheck);
		 
		 	// Check the next position.
		 	positionToCheck.x = positionToCheck.x - 1;
		 	positionToCheck.y = positionToCheck.y - 1;
		 
		 	// If new position is off of the board or if it is an empty square, then there are no pieces to be flipped.
		 	if (chk4OutOfBounds(positionToCheck) || state[positionToCheck.y][positionToCheck.x] == EMPTY) return null;
		 
		 	// If new position is the played color, then there are pieces to flip. Return the stack.
		 	if (state[positionToCheck.y][positionToCheck.x] == playedColor) return toFlip;
		}		
		
		// Case: The first position to check is not an opponents piece.
		return null;
	}
	
	// Checks for flips in the indicated direction.
	private Stack<Position> check4FlipUp(Position position, int playedColor, int opponentColor, int[][] state)
	{
		Stack<Position> toFlip = new Stack<>();
		
		Position positionToCheck = new Position(position.x, position.y - 1);
		
		// If the position to check is off the board, return null.
		if (chk4OutOfBounds(positionToCheck)) return null;
		
		// While the position to check is the opponents color, check the next position.
		while (state[positionToCheck.y][positionToCheck.x] == opponentColor)
		{
			// Add opponents piece to the stack.
			toFlip.add(positionToCheck);
		 
		 	// Check the next position.
		 	positionToCheck.x = positionToCheck.x;
		 	positionToCheck.y = positionToCheck.y - 1;
		 
		 	// If new position is off of the board or if it is an empty square, then there are no pieces to be flipped.
		 	if (chk4OutOfBounds(positionToCheck) || state[positionToCheck.y][positionToCheck.x] == EMPTY) return null;
		 
		 	// If new position is the played color, then there are pieces to flip. Return the stack.
		 	if (state[positionToCheck.y][positionToCheck.x] == playedColor) return toFlip;
		}		
		
		// Case: The first position to check is not an opponents piece.
		return null;
	}

	// Checks for flips in the indicated direction.
	private Stack<Position> check4FlipUpRight(Position position, int playedColor, int opponentColor, int[][] state)
	{
		Stack<Position> toFlip = new Stack<>();
		
		Position positionToCheck = new Position(position.x + 1, position.y - 1);
		
		// If the position to check is off the board, return null.
		if (chk4OutOfBounds(positionToCheck)) return null;
		
		// While the position to check is the opponents color, check the next position.
		while (state[positionToCheck.y][positionToCheck.x] == opponentColor)
		{
			// Add opponents piece to the stack.
			toFlip.add(positionToCheck);
		 
		 	// Check the next position.
		 	positionToCheck.x = positionToCheck.x + 1;
		 	positionToCheck.y = positionToCheck.y - 1;
		 
		 	// If new position is off of the board or if it is an empty square, then there are no pieces to be flipped.
		 	if (chk4OutOfBounds(positionToCheck) || state[positionToCheck.y][positionToCheck.x] == EMPTY) return null;
		 
		 	// If new position is the played color, then there are pieces to flip. Return the stack.
		 	if (state[positionToCheck.y][positionToCheck.x] == playedColor) return toFlip;
		}		
		
		// Case: The first position to check is not an opponents piece.
		return null;
	}

	// Checks for flips in the indicated direction.
	private Stack<Position> check4FlipLeft(Position position, int playedColor, int opponentColor, int[][] state)
	{
		Stack<Position> toFlip = new Stack<>();
		
		Position positionToCheck = new Position(position.x - 1, position.y);
		
		// If the position to check is off the board, return null.
		if (chk4OutOfBounds(positionToCheck)) return null;
		
		// While the position to check is the opponents color, check the next position.
		while (state[positionToCheck.y][positionToCheck.x] == opponentColor)
		{
			// Add opponents piece to the stack.
			toFlip.add(positionToCheck);
		 
		 	// Check the next position.
		 	positionToCheck.x = positionToCheck.x - 1;
		 	positionToCheck.y = positionToCheck.y;
		 
		 	// If new position is off of the board or if it is an empty square, then there are no pieces to be flipped.
		 	if (chk4OutOfBounds(positionToCheck) || state[positionToCheck.y][positionToCheck.x] == EMPTY) return null;
		 
		 	// If new position is the played color, then there are pieces to flip. Return the stack.
		 	if (state[positionToCheck.y][positionToCheck.x] == playedColor) return toFlip;
		}		
		
		// Case: The first position to check is not an opponents piece.
		return null;
	}

	// Checks for flips in the indicated direction.
	private Stack<Position> check4FlipRight(Position position, int playedColor, int opponentColor, int[][] state)
	{
		Stack<Position> toFlip = new Stack<>();
		
		Position positionToCheck = new Position(position.x + 1, position.y);
		
		// If the position to check is off the board, return null.
		if (chk4OutOfBounds(positionToCheck)) return null;
		
		// While the position to check is the opponents color, check the next position.
		while (state[positionToCheck.y][positionToCheck.x] == opponentColor)
		{
			// Add opponents piece to the stack.
			toFlip.add(positionToCheck);
		 
		 	// Check the next position.
		 	positionToCheck.x = positionToCheck.x + 1;
		 	positionToCheck.y = positionToCheck.y;
		 
		 	// If new position is off of the board or if it is an empty square, then there are no pieces to be flipped.
		 	if (chk4OutOfBounds(positionToCheck) || state[positionToCheck.y][positionToCheck.x] == EMPTY) return null;
		 
		 	// If new position is the played color, then there are pieces to flip. Return the stack.
		 	if (state[positionToCheck.y][positionToCheck.x] == playedColor) return toFlip;
		}		
		
		// Case: The first position to check is not an opponents piece.
		return null;
	}

	// Checks for flips in the indicated direction.
	private Stack<Position> check4FlipDownLeft(Position position, int playedColor, int opponentColor, int[][] state)
	{
		Stack<Position> toFlip = new Stack<>();
		
		Position positionToCheck = new Position(position.x - 1, position.y + 1);
		
		// If the position to check is off the board, return null.
		if (chk4OutOfBounds(positionToCheck)) return null;
		
		// While the position to check is the opponents color, check the next position.
		while (state[positionToCheck.y][positionToCheck.x] == opponentColor)
		{
			// Add opponents piece to the stack.
			toFlip.add(positionToCheck);
		 
		 	// Check the next position.
		 	positionToCheck.x = positionToCheck.x - 1;
		 	positionToCheck.y = positionToCheck.y + 1;
		 
		 	// If new position is off of the board or if it is an empty square, then there are no pieces to be flipped.
		 	if (chk4OutOfBounds(positionToCheck) || state[positionToCheck.y][positionToCheck.x] == EMPTY) return null;
		 
		 	// If new position is the played color, then there are pieces to flip. Return the stack.
		 	if (state[positionToCheck.y][positionToCheck.x] == playedColor) return toFlip;
		}		
		
		// Case: The first position to check is not an opponents piece.
		return null;
	}

	// Checks for flips in the indicated direction.
	private Stack<Position> check4FlipDown(Position position, int playedColor, int opponentColor, int[][] state)
	{
		Stack<Position> toFlip = new Stack<>();
		
		Position positionToCheck = new Position(position.x, position.y + 1);
		
		// If the position to check is off the board, return null.
		if (chk4OutOfBounds(positionToCheck)) return null;
		
		// While the position to check is the opponents color, check the next position.
		while (state[positionToCheck.y][positionToCheck.x] == opponentColor)
		{
			// Add opponents piece to the stack.
			toFlip.add(positionToCheck);
		 
		 	// Check the next position.
		 	positionToCheck.x = positionToCheck.x;
		 	positionToCheck.y = positionToCheck.y + 1;
		 
		 	// If new position is off of the board or if it is an empty square, then there are no pieces to be flipped.
		 	if (chk4OutOfBounds(positionToCheck) || state[positionToCheck.y][positionToCheck.x] == EMPTY) return null;
		 
		 	// If new position is the played color, then there are pieces to flip. Return the stack.
		 	if (state[positionToCheck.y][positionToCheck.x] == playedColor) return toFlip;
		}		
		
		// Case: The first position to check is not an opponents piece.
		return null;
	}

	// Checks for flips in the indicated direction.
	private Stack<Position> check4FlipDownRight(Position position, int playedColor, int opponentColor, int[][] state)
	{
		Stack<Position> toFlip = new Stack<>();
		
		Position positionToCheck = new Position(position.x + 1, position.y + 1);
		
		// If the position to check is off the board, return null.
		if (chk4OutOfBounds(positionToCheck)) return null;
		
		// While the position to check is the opponents color, check the next position.
		while (state[positionToCheck.y][positionToCheck.x] == opponentColor)
		{
			// Add opponents piece to the stack.
			toFlip.add(positionToCheck);
		 
		 	// Check the next position.
		 	positionToCheck.x = positionToCheck.x + 1;
		 	positionToCheck.y = positionToCheck.y + 1;
		 
		 	// If new position is off of the board or if it is an empty square, then there are no pieces to be flipped.
		 	if (chk4OutOfBounds(positionToCheck) || state[positionToCheck.y][positionToCheck.x] == EMPTY) return null;
		 
		 	// If new position is the played color, then there are pieces to flip. Return the stack.
		 	if (state[positionToCheck.y][positionToCheck.x] == playedColor) return toFlip;
		}		
		
		// Case: The first position to check is not an opponents piece.
		return null;
	}
	
	// Returns the available valid moves given the board state and the active player color.
	public int[][] availableMoves(int[][] state, int color)
	{
		int[][] validMoves = new int[8][8]; // to return		
		Position position = new Position(0,0);
		
		for (int row = 0; row < 8; row++)
		{
			for (int col = 0; col < 8; col++)
			{
				// If current board location contains the active players color, look for valid moves
				// created by that piece.
				if (state[row][col] == color)
				{
					// Set position to current location.
					position.x = col;
					position.y = row;
					
					Position upLeft = checkUpLeft(position, color, state);
					if (upLeft != null) validMoves[upLeft.y][upLeft.x] = VALID;
					
					Position up = checkUp(position, color, state);
					if (up != null) validMoves[up.y][up.x] = VALID;
					
					Position upperRight = checkUpRight(position, color, state);
					if (upperRight != null) validMoves[up.y][up.x] = VALID;
					
					Position left = checkLeft(position, color, state);
					if (left != null) validMoves[up.y][up.x] = VALID;
					
					Position right = checkRight(position, color, state);
					if (right != null) validMoves[up.y][up.x] = VALID;
					
					Position downLeft = checkDownLeft(position, color, state);
					if (downLeft != null) validMoves[up.y][up.x] = VALID;
					
					Position down = checkDown(position, color, state);
					if (down != null) validMoves[up.y][up.x] = VALID;
					
					Position downRight = checkDownRight(position, color, state);
					if (downRight != null) validMoves[up.y][up.x] = VALID;
					
				}				
			}
		}		
		
		return validMoves;
	}
	
	// Checks for a valid move in the indicated direction.
	private Position checkDownRight(Position position, int color, int[][] state)
	{
		int oppColor = color == BLACK ? WHITE : BLACK;
		Position chkPos = new Position(position.x + 1, position.y + 1);
		
		// If check position is off the board, return null.
		if (chk4OutOfBounds(chkPos)) return null;
				
		// While check position is opponents color, check next position.
		while(state[chkPos.y][chkPos.x] == oppColor)
		{
			// Check next position.
			chkPos.x = chkPos.x + 1;
			chkPos.y = chkPos.y + 1;
			
			// If new position is an empty square, then it is a valid move.
			if (state[chkPos.y][chkPos.x] == EMPTY)
			{
				return chkPos;
			}
			
			// If new position is the player's color or is off the board, no valid move exists.
			if (state[chkPos.y][chkPos.x] == color || chk4OutOfBounds(chkPos))
			{
				return null;
			}
		}
		
		// Check position wasn't opponents color, and thus could not lead to a valid move.
		return null;
	}

	// Checks for a valid move in the indicated direction.
	private Position checkDown(Position position, int color, int[][] state)
	{
		int oppColor = color == BLACK ? WHITE : BLACK;
		Position chkPos = new Position(position.x, position.y + 1);
		
		// If check position is off the board, return null.
		if (chk4OutOfBounds(chkPos)) return null;
				
		// While check position is opponents color, check next position.
		while(state[chkPos.y][chkPos.x] == oppColor)
		{
			// Check next position.
			chkPos.x = chkPos.x;
			chkPos.y = chkPos.y + 1;
			
			// If new position is an empty square, then it is a valid move.
			if (state[chkPos.y][chkPos.x] == EMPTY)
			{
				return chkPos;
			}
			
			// If new position is the player's color or is off the board, no valid move exists.
			if (state[chkPos.y][chkPos.x] == color || chk4OutOfBounds(chkPos))
			{
				return null;
			}
		}
		
		// Check position wasn't opponents color, and thus could not lead to a valid move.
		return null;
	}

	// Checks for a valid move in the indicated direction.
	private Position checkDownLeft(Position position, int color, int[][] state)
	{
		int oppColor = color == BLACK ? WHITE : BLACK;
		Position chkPos = new Position(position.x - 1, position.y + 1);
		
		// If check position is off the board, return null.
		if (chk4OutOfBounds(chkPos)) return null;
				
		// While check position is opponents color, check next position.
		while(state[chkPos.y][chkPos.x] == oppColor)
		{
			// Check next position.
			chkPos.x = chkPos.x - 1;
			chkPos.y = chkPos.y + 1;
			
			// If new position is an empty square, then it is a valid move.
			if (state[chkPos.y][chkPos.x] == EMPTY)
			{
				return chkPos;
			}
			
			// If new position is the player's color or is off the board, no valid move exists.
			if (state[chkPos.y][chkPos.x] == color || chk4OutOfBounds(chkPos))
			{
				return null;
			}
		}
		
		// Check position wasn't opponents color, and thus could not lead to a valid move.
		return null;
	}

	// Checks for a valid move in the indicated direction.
	private Position checkRight(Position position, int color, int[][] state)
	{
		int oppColor = color == BLACK ? WHITE : BLACK;
		Position chkPos = new Position(position.x + 1, position.y);
		
		// If check position is off the board, return null.
		if (chk4OutOfBounds(chkPos)) return null;
				
		// While check position is opponents color, check next position.
		while(state[chkPos.y][chkPos.x] == oppColor)
		{
			// Check next position.
			chkPos.x = chkPos.x + 1;
			chkPos.y = chkPos.y;
			
			// If new position is an empty square, then it is a valid move.
			if (state[chkPos.y][chkPos.x] == EMPTY)
			{
				return chkPos;
			}
			
			// If new position is the player's color or is off the board, no valid move exists.
			if (state[chkPos.y][chkPos.x] == color || chk4OutOfBounds(chkPos))
			{
				return null;
			}
		}
		
		// Check position wasn't opponents color, and thus could not lead to a valid move.
		return null;
	}

	// Checks for a valid move in the indicated direction.
	private Position checkLeft(Position position, int color, int[][] state)
	{
		int oppColor = color == BLACK ? WHITE : BLACK;
		Position chkPos = new Position(position.x - 1, position.y);
		
		// If check position is off the board, return null.
		if (chk4OutOfBounds(chkPos)) return null;
				
		// While check position is opponents color, check next position.
		while(state[chkPos.y][chkPos.x] == oppColor)
		{
			// Check next position.
			chkPos.x = chkPos.x - 1;
			chkPos.y = chkPos.y;
			
			// If new position is an empty square, then it is a valid move.
			if (state[chkPos.y][chkPos.x] == EMPTY)
			{
				return chkPos;
			}
			
			// If new position is the player's color or is off the board, no valid move exists.
			if (state[chkPos.y][chkPos.x] == color || chk4OutOfBounds(chkPos))
			{
				return null;
			}
		}
		
		// Check position wasn't opponents color, and thus could not lead to a valid move.
		return null;
	}

	// Checks for a valid move in the indicated direction.
	private Position checkUpRight(Position position, int color, int[][] state)
	{
		int oppColor = color == BLACK ? WHITE : BLACK;
		Position chkPos = new Position(position.x + 1, position.y - 1);
		
		// If check position is off the board, return null.
		if (chk4OutOfBounds(chkPos)) return null;
				
		// While check position is opponents color, check next position.
		while(state[chkPos.y][chkPos.x] == oppColor)
		{
			// Check next position.
			chkPos.x = chkPos.x + 1;
			chkPos.y = chkPos.y - 1;
			
			// If new position is an empty square, then it is a valid move.
			if (state[chkPos.y][chkPos.x] == EMPTY)
			{
				return chkPos;
			}
			
			// If new position is the player's color or is off the board, no valid move exists.
			if (state[chkPos.y][chkPos.x] == color || chk4OutOfBounds(chkPos))
			{
				return null;
			}
		}
		
		// Check position wasn't opponents color, and thus could not lead to a valid move.
		return null;
	}

	// Checks for a valid move in the indicated direction.
	private Position checkUp(Position position, int color, int[][] state)
	{
		int oppColor = color == BLACK ? WHITE : BLACK;
		Position chkPos = new Position(position.x, position.y - 1);
		
		// If check position is off the board, return null.
		if (chk4OutOfBounds(chkPos)) return null;
				
		// While check position is opponents color, check next position.
		while(state[chkPos.y][chkPos.x] == oppColor)
		{
			// Check next position.
			chkPos.x = chkPos.x;
			chkPos.y = chkPos.y - 1;
			
			// If new position is an empty square, then it is a valid move.
			if (state[chkPos.y][chkPos.x] == EMPTY)
			{
				return chkPos;
			}
			
			// If new position is the player's color or is off the board, no valid move exists.
			if (state[chkPos.y][chkPos.x] == color || chk4OutOfBounds(chkPos))
			{
				return null;
			}
		}
		
		// Check position wasn't opponents color, and thus could not lead to a valid move.
		return null;
	}

	// Checks for a valid move in the indicated direction.
	private Position checkUpLeft(Position position, int color, int[][] state)
	{
		int oppColor = color == BLACK ? WHITE : BLACK;
		Position chkPos = new Position(position.x - 1, position.y - 1);
		
		// If check position is off the board, return null.
		if (chk4OutOfBounds(chkPos)) return null;
				
		// While check position is opponents color, check next position.
		while(state[chkPos.y][chkPos.x] == oppColor)
		{
			// Check next position.
			chkPos.x = chkPos.x - 1;
			chkPos.y = chkPos.y - 1;
			
			// If new position is an empty square, then it is a valid move.
			if (state[chkPos.y][chkPos.x] == EMPTY)
			{
				return chkPos;
			}
			
			// If new position is the player's color or is off the board, no valid move exists.
			if (state[chkPos.y][chkPos.x] == color || chk4OutOfBounds(chkPos))
			{
				return null;
			}
		}
		
		// Check position wasn't opponents color, and thus could not lead to a valid move.
		return null;
	}
	
  // Checks if the position is off the board.
	private boolean chk4OutOfBounds(Position pos)
	{
		if (pos.x < 0 || pos.x > 7 || pos.y < 0 || pos.y > 7)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
		
	
}



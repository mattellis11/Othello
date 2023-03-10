package server;

import java.util.Stack;

/**
 * Implements the game rules
 *
 * @author Matt Ellis
 */
public class GameMaster {
    // Constants for board state.
    private static final int BLACK = 1;
    private static final int WHITE = 0;
    private static final int EMPTY = -99;

    // Constant for valid move array.
    private static final int VALID = 99;

    /**
     * Method for initializing a new game.
     * <p>
     * Randomly assigns players' colors.
     *
     * @param player1 one of the two players
     * @param player2 one of the two players
     * @return a new GameData object
     */
    public GameData newGame(Player player1, Player player2) {
        // Initialize the starting board positions.
        int[][] boardState = new int[8][8];
        for (int y = 1; y < 9; y++) {
            for (int x = 1; x < 9; x++) {
                if (y == 4 && x == 4 || y == 5 && x == 5) {
                    boardState[y - 1][x - 1] = WHITE;
                } else if (y == 5 && x == 4 || y == 4 && x == 5) {
                    boardState[y - 1][x - 1] = BLACK;
                } else {
                    boardState[y - 1][x - 1] = EMPTY;
                }
            }
        }

        // Randomly assign players their color.
        int player1Color = (int) (Math.random() * 2);
        int player2Color = player1Color == BLACK ? WHITE : BLACK;
        player1.setColor(player1Color);
        player2.setColor(player2Color);

        // Create new game data.
        GameData data = new GameData(player1, player2, boardState);

        // Initialize available moves for black.
        setAvailableMoves(data, BLACK);

        return data;
    }

    /**
     * Method for placing a newly played game piece and updating the game state.
     *
     * @param data        the current game state
     * @param playedColor the color of the new piece played
     */
    public void placePiece(GameData data, int playedColor) {
        int opponentColor = playedColor == BLACK ? WHITE : BLACK;
        Position playedPiece = data.getMove();
        Stack<Position> piecesToFlip;

        // Place the new piece.
        data.getBoardState()[playedPiece.y][playedPiece.x] = playedColor;

        // Adjust the score for that piece
        if (playedColor == BLACK) {
            data.setBlackScore(data.getBlackScore() + 1);
        } else {
            data.setWhiteScore(data.getWhiteScore() + 1);
        }

        // Check for opponent's pieces to flip, and flip them.
        piecesToFlip = check4FlipUpLeft(playedPiece, playedColor, opponentColor, data.getBoardState());
        if (piecesToFlip != null) flipPieces(piecesToFlip, data, playedColor);

        piecesToFlip = check4FlipUp(playedPiece, playedColor, opponentColor, data.getBoardState());
        if (piecesToFlip != null) flipPieces(piecesToFlip, data, playedColor);

        piecesToFlip = check4FlipUpRight(playedPiece, playedColor, opponentColor, data.getBoardState());
        if (piecesToFlip != null) flipPieces(piecesToFlip, data, playedColor);

        piecesToFlip = check4FlipLeft(playedPiece, playedColor, opponentColor, data.getBoardState());
        if (piecesToFlip != null) flipPieces(piecesToFlip, data, playedColor);

        piecesToFlip = check4FlipRight(playedPiece, playedColor, opponentColor, data.getBoardState());
        if (piecesToFlip != null) flipPieces(piecesToFlip, data, playedColor);

        piecesToFlip = check4FlipDownLeft(playedPiece, playedColor, opponentColor, data.getBoardState());
        if (piecesToFlip != null) flipPieces(piecesToFlip, data, playedColor);

        piecesToFlip = check4FlipDown(playedPiece, playedColor, opponentColor, data.getBoardState());
        if (piecesToFlip != null) flipPieces(piecesToFlip, data, playedColor);

        piecesToFlip = check4FlipDownRight(playedPiece, playedColor, opponentColor, data.getBoardState());
        if (piecesToFlip != null) flipPieces(piecesToFlip, data, playedColor);

        // Update the available moves for the next turn.
        setAvailableMoves(data, opponentColor);
    }

    /**
     * Flips the opponent's pieces and updates the scores.
     *
     * @param toFlip      the opponent's pieces to flip
     * @param data        the game state
     * @param playedColor the color of the active player
     */
    private void flipPieces(Stack<Position> toFlip, GameData data, int playedColor) {
        // Get the number of pieces to be flipped.
        int numOfPiecesFlipped = toFlip.size();

        // Get the board state.
        int[][] state = data.getBoardState();

        // Flip the opponents pieces.
        while (!toFlip.isEmpty()) {
            Position piece = toFlip.pop();
            state[piece.y][piece.x] = playedColor;
        }

        // Adjust the scores.
        if (playedColor == BLACK) {
            data.setBlackScore(data.getBlackScore() + numOfPiecesFlipped);
            data.setWhiteScore(data.getWhiteScore() - numOfPiecesFlipped);
        } else {
            data.setWhiteScore(data.getWhiteScore() + numOfPiecesFlipped);
            data.setBlackScore(data.getBlackScore() - numOfPiecesFlipped);
        }
    }

    /**
     * Checks for possible opponent pieces to flip in the indicated direction.
     *
     * @param position      the position of the played piece
     * @param playedColor   the color of the played piece
     * @param opponentColor the opponent's color
     * @param state         the current board state
     * @return the opponent's pieces to flip, or null if there are none
     */
    private Stack<Position> check4FlipUpLeft(Position position, int playedColor, int opponentColor, int[][] state) {
        Stack<Position> toFlip = new Stack<>();

        Position positionToCheck = new Position(position.x - 1, position.y - 1);

        // If the position to check is off the board, return null.
        if (isOutOfBounds(positionToCheck)) return null;

        // While the position to check is the opponents color, check the next position.
        while (state[positionToCheck.y][positionToCheck.x] == opponentColor) {
            // Add opponents piece to the stack.
            toFlip.add(new Position(positionToCheck.x, positionToCheck.y));

            // Check the next position.
            positionToCheck.x = positionToCheck.x - 1;
            positionToCheck.y = positionToCheck.y - 1;

            // If new position is off of the board or if it is an empty square, then there are no pieces to be flipped.
            if (isOutOfBounds(positionToCheck) || state[positionToCheck.y][positionToCheck.x] == EMPTY) return null;

            // If new position is the played color, then there are pieces to flip. Return the stack.
            if (state[positionToCheck.y][positionToCheck.x] == playedColor) return toFlip;
        }

        // Case: The first position to check is not an opponents piece.
        return null;
    }

    /**
     * Checks for possible opponent pieces to flip in the indicated direction.
     *
     * @param position      the position of the played piece
     * @param playedColor   the color of the played piece
     * @param opponentColor the opponent's color
     * @param state         the current board state
     * @return the opponent's pieces to flip, or null if there are none
     */
    private Stack<Position> check4FlipUp(Position position, int playedColor, int opponentColor, int[][] state) {
        Stack<Position> toFlip = new Stack<>();

        Position positionToCheck = new Position(position.x, position.y - 1);

        // If the position to check is off the board, return null.
        if (isOutOfBounds(positionToCheck)) return null;

        // While the position to check is the opponents color, check the next position.
        while (state[positionToCheck.y][positionToCheck.x] == opponentColor) {
            // Add opponents piece to the stack.
            toFlip.add(new Position(positionToCheck.x, positionToCheck.y));

            // Check the next position.
            positionToCheck.x = positionToCheck.x;
            positionToCheck.y = positionToCheck.y - 1;

            // If new position is off of the board or if it is an empty square, then there are no pieces to be flipped.
            if (isOutOfBounds(positionToCheck) || state[positionToCheck.y][positionToCheck.x] == EMPTY) return null;

            // If new position is the played color, then there are pieces to flip. Return the stack.
            if (state[positionToCheck.y][positionToCheck.x] == playedColor) return toFlip;
        }

        // Case: The first position to check is not an opponents piece.
        return null;
    }


    /**
     * Checks for possible opponent pieces to flip in the indicated direction.
     *
     * @param position      the position of the played piece
     * @param playedColor   the color of the played piece
     * @param opponentColor the opponent's color
     * @param state         the current board state
     * @return the opponent's pieces to flip, or null if there are none
     */
    private Stack<Position> check4FlipUpRight(Position position, int playedColor, int opponentColor, int[][] state) {
        Stack<Position> toFlip = new Stack<>();

        Position positionToCheck = new Position(position.x + 1, position.y - 1);

        // If the position to check is off the board, return null.
        if (isOutOfBounds(positionToCheck)) return null;

        // While the position to check is the opponents color, check the next position.
        while (state[positionToCheck.y][positionToCheck.x] == opponentColor) {
            // Add opponents piece to the stack.
            toFlip.add(new Position(positionToCheck.x, positionToCheck.y));

            // Check the next position.
            positionToCheck.x = positionToCheck.x + 1;
            positionToCheck.y = positionToCheck.y - 1;

            // If new position is off of the board or if it is an empty square, then there are no pieces to be flipped.
            if (isOutOfBounds(positionToCheck) || state[positionToCheck.y][positionToCheck.x] == EMPTY) return null;

            // If new position is the played color, then there are pieces to flip. Return the stack.
            if (state[positionToCheck.y][positionToCheck.x] == playedColor) return toFlip;
        }

        // Case: The first position to check is not an opponents piece.
        return null;
    }


    /**
     * Checks for possible opponent pieces to flip in the indicated direction.
     *
     * @param position      the position of the played piece
     * @param playedColor   the color of the played piece
     * @param opponentColor the opponent's color
     * @param state         the current board state
     * @return the opponent's pieces to flip, or null if there are none
     */
    private Stack<Position> check4FlipLeft(Position position, int playedColor, int opponentColor, int[][] state) {
        Stack<Position> toFlip = new Stack<>();

        Position positionToCheck = new Position(position.x - 1, position.y);

        // If the position to check is off the board, return null.
        if (isOutOfBounds(positionToCheck)) return null;

        // While the position to check is the opponents color, check the next position.
        while (state[positionToCheck.y][positionToCheck.x] == opponentColor) {
            // Add opponents piece to the stack.
            toFlip.add(new Position(positionToCheck.x, positionToCheck.y));

            // Check the next position.
            positionToCheck.x = positionToCheck.x - 1;
            positionToCheck.y = positionToCheck.y;

            // If new position is off of the board or if it is an empty square, then there are no pieces to be flipped.
            if (isOutOfBounds(positionToCheck) || state[positionToCheck.y][positionToCheck.x] == EMPTY) return null;

            // If new position is the played color, then there are pieces to flip. Return the stack.
            if (state[positionToCheck.y][positionToCheck.x] == playedColor) return toFlip;
        }

        // Case: The first position to check is not an opponents piece.
        return null;
    }


    /**
     * Checks for possible opponent pieces to flip in the indicated direction.
     *
     * @param position      the position of the played piece
     * @param playedColor   the color of the played piece
     * @param opponentColor the opponent's color
     * @param state         the current board state
     * @return the opponent's pieces to flip, or null if there are none
     */
    private Stack<Position> check4FlipRight(Position position, int playedColor, int opponentColor, int[][] state) {
        Stack<Position> toFlip = new Stack<>();

        Position positionToCheck = new Position(position.x + 1, position.y);

        // If the position to check is off the board, return null.
        if (isOutOfBounds(positionToCheck)) return null;

        // While the position to check is the opponents color, check the next position.
        while (state[positionToCheck.y][positionToCheck.x] == opponentColor) {
            // Add opponents piece to the stack.
            toFlip.add(new Position(positionToCheck.x, positionToCheck.y));

            // Check the next position.
            positionToCheck.x = positionToCheck.x + 1;
            positionToCheck.y = positionToCheck.y;

            // If new position is off of the board or if it is an empty square, then there are no pieces to be flipped.
            if (isOutOfBounds(positionToCheck) || state[positionToCheck.y][positionToCheck.x] == EMPTY) return null;

            // If new position is the played color, then there are pieces to flip. Return the stack.
            if (state[positionToCheck.y][positionToCheck.x] == playedColor) return toFlip;
        }

        // Case: The first position to check is not an opponents piece.
        return null;
    }


    /**
     * Checks for possible opponent pieces to flip in the indicated direction.
     *
     * @param position      the position of the played piece
     * @param playedColor   the color of the played piece
     * @param opponentColor the opponent's color
     * @param state         the current board state
     * @return the opponent's pieces to flip, or null if there are none
     */
    private Stack<Position> check4FlipDownLeft(Position position, int playedColor, int opponentColor, int[][] state) {
        Stack<Position> toFlip = new Stack<>();

        Position positionToCheck = new Position(position.x - 1, position.y + 1);

        // If the position to check is off the board, return null.
        if (isOutOfBounds(positionToCheck)) return null;

        // While the position to check is the opponents color, check the next position.
        while (state[positionToCheck.y][positionToCheck.x] == opponentColor) {
            // Add opponents piece to the stack.
            toFlip.add(new Position(positionToCheck.x, positionToCheck.y));

            // Check the next position.
            positionToCheck.x = positionToCheck.x - 1;
            positionToCheck.y = positionToCheck.y + 1;

            // If new position is off of the board or if it is an empty square, then there are no pieces to be flipped.
            if (isOutOfBounds(positionToCheck) || state[positionToCheck.y][positionToCheck.x] == EMPTY) return null;

            // If new position is the played color, then there are pieces to flip. Return the stack.
            if (state[positionToCheck.y][positionToCheck.x] == playedColor) return toFlip;
        }

        // Case: The first position to check is not an opponents piece.
        return null;
    }


    /**
     * Checks for possible opponent pieces to flip in the indicated direction.
     *
     * @param position      the position of the played piece
     * @param playedColor   the color of the played piece
     * @param opponentColor the opponent's color
     * @param state         the current board state
     * @return the opponent's pieces to flip, or null if there are none
     */
    private Stack<Position> check4FlipDown(Position position, int playedColor, int opponentColor, int[][] state) {
        Stack<Position> toFlip = new Stack<>();

        Position positionToCheck = new Position(position.x, position.y + 1);

        // If the position to check is off the board, return null.
        if (isOutOfBounds(positionToCheck)) return null;

        // While the position to check is the opponents color, check the next position.
        while (state[positionToCheck.y][positionToCheck.x] == opponentColor) {
            // Add opponents piece to the stack.
            toFlip.add(new Position(positionToCheck.x, positionToCheck.y));

            // Check the next position.
            positionToCheck.x = positionToCheck.x;
            positionToCheck.y = positionToCheck.y + 1;

            // If new position is off of the board or if it is an empty square, then there are no pieces to be flipped.
            if (isOutOfBounds(positionToCheck) || state[positionToCheck.y][positionToCheck.x] == EMPTY) return null;

            // If new position is the played color, then there are pieces to flip. Return the stack.
            if (state[positionToCheck.y][positionToCheck.x] == playedColor) return toFlip;
        }

        // Case: The first position to check is not an opponents piece.
        return null;
    }


    /**
     * Checks for possible opponent pieces to flip in the indicated direction.
     *
     * @param position      the position of the played piece
     * @param playedColor   the color of the played piece
     * @param opponentColor the opponent's color
     * @param state         the current board state
     * @return the opponent's pieces to flip, or null if there are none
     */
    private Stack<Position> check4FlipDownRight(Position position, int playedColor, int opponentColor, int[][] state) {
        Stack<Position> toFlip = new Stack<>();

        Position positionToCheck = new Position(position.x + 1, position.y + 1);

        // If the position to check is off the board, return null.
        if (isOutOfBounds(positionToCheck)) return null;

        // While the position to check is the opponents color, check the next position.
        while (state[positionToCheck.y][positionToCheck.x] == opponentColor) {
            // Add opponents piece to the stack.
            toFlip.add(new Position(positionToCheck.x, positionToCheck.y));

            // Check the next position.
            positionToCheck.x = positionToCheck.x + 1;
            positionToCheck.y = positionToCheck.y + 1;

            // If new position is off of the board or if it is an empty square, then there are no pieces to be flipped.
            if (isOutOfBounds(positionToCheck) || state[positionToCheck.y][positionToCheck.x] == EMPTY) return null;

            // If new position is the played color, then there are pieces to flip. Return the stack.
            if (state[positionToCheck.y][positionToCheck.x] == playedColor) return toFlip;
        }

        // Case: The first position to check is not an opponents piece.
        return null;
    }

    /**
     * Calculates and updates the available valid moves for the active player.
     * <p>
     * Also, maintains the moveAvailable state variable of the GameData.
     *
     * @param data              current game state
     * @param activePlayerColor the color of the active player
     */
    public void setAvailableMoves(GameData data, int activePlayerColor) {
        int[][] validMoves = new int[8][8];
        Position position = new Position(0, 0);
        boolean validMoveFound = false;
        int[][] state = data.getBoardState();

        // Check every game board position for active player's color.
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                /*
                 Check if current board location contains the active players color.
                 If so, look for valid moves created by that piece.
                */
                if (state[row][col] == activePlayerColor) {

                    // Set position to current location.
                    position.x = col;
                    position.y = row;

                    /*
                    Check for a valid move in the indicated direction.
                    If found, update the state of the board position.
                     */
                    Position upLeft = checkUpLeft(position, activePlayerColor, state);
                    if (upLeft != null) {
                        validMoves[upLeft.y][upLeft.x] = VALID;
                        validMoveFound = true;
                    }

                    Position up = checkUp(position, activePlayerColor, state);
                    if (up != null) {
                        validMoves[up.y][up.x] = VALID;
                        validMoveFound = true;
                    }

                    Position upperRight = checkUpRight(position, activePlayerColor, state);
                    if (upperRight != null) {
                        validMoves[upperRight.y][upperRight.x] = VALID;
                        validMoveFound = true;
                    }

                    Position left = checkLeft(position, activePlayerColor, state);
                    if (left != null) {
                        validMoves[left.y][left.x] = VALID;
                        validMoveFound = true;
                    }

                    Position right = checkRight(position, activePlayerColor, state);
                    if (right != null) {
                        validMoves[right.y][right.x] = VALID;
                        validMoveFound = true;
                    }

                    Position downLeft = checkDownLeft(position, activePlayerColor, state);
                    if (downLeft != null) {
                        validMoves[downLeft.y][downLeft.x] = VALID;
                        validMoveFound = true;
                    }

                    Position down = checkDown(position, activePlayerColor, state);
                    if (down != null) {
                        validMoves[down.y][down.x] = VALID;
                        validMoveFound = true;
                    }

                    Position downRight = checkDownRight(position, activePlayerColor, state);
                    if (downRight != null) {
                        validMoves[downRight.y][downRight.x] = VALID;
                        validMoveFound = true;
                    }
                }
            }
        }

        data.setValidMoves(validMoves);
        data.setMoveAvailable(validMoveFound);
    }

    /**
     * Checks for a valid move in the indicated direction.
     *
     * @param position the starting position
     * @param color    the color of the active player
     * @param state    the current board state
     * @return a valid move board position, or null if not found
     */
    private Position checkDownRight(Position position, int color, int[][] state) {
        int oppColor = color == BLACK ? WHITE : BLACK;
        Position chkPos = new Position(position.x + 1, position.y + 1);

        // If check position is off the board, return null.
        if (isOutOfBounds(chkPos)) return null;

        // While check position is opponents color, check next position.
        while (state[chkPos.y][chkPos.x] == oppColor) {
            // Check next position.
            chkPos.x = chkPos.x + 1;
            chkPos.y = chkPos.y + 1;

            // If new position is the player's color or is off the board, no valid move exists.
            if (isOutOfBounds(chkPos) || state[chkPos.y][chkPos.x] == color) {
                return null;
            }

            // If new position is an empty square, then it is a valid move.
            if (state[chkPos.y][chkPos.x] == EMPTY) {
                return chkPos;
            }
        }

        // Check position wasn't opponents color, and thus could not lead to a valid move.
        return null;
    }

    /**
     * Checks for a valid move in the indicated direction.
     *
     * @param position the starting position
     * @param color    the color of the active player
     * @param state    the current board state
     * @return a valid move board position, or null if not found
     */
    private Position checkDown(Position position, int color, int[][] state) {
        int oppColor = color == BLACK ? WHITE : BLACK;
        Position chkPos = new Position(position.x, position.y + 1);

        // If check position is off the board, return null.
        if (isOutOfBounds(chkPos)) return null;

        // While check position is opponents color, check next position.
        while (state[chkPos.y][chkPos.x] == oppColor) {
            // Check next position.
            chkPos.x = chkPos.x;
            chkPos.y = chkPos.y + 1;

            // If new position is the player's color or is off the board, no valid move exists.
            if (isOutOfBounds(chkPos) || state[chkPos.y][chkPos.x] == color) {
                return null;
            }

            // If new position is an empty square, then it is a valid move.
            if (state[chkPos.y][chkPos.x] == EMPTY) {
                return chkPos;
            }
        }

        // Check position wasn't opponents color, and thus could not lead to a valid move.
        return null;
    }

    /**
     * Checks for a valid move in the indicated direction.
     *
     * @param position the starting position
     * @param color    the color of the active player
     * @param state    the current board state
     * @return a valid move board position, or null if not found
     */
    private Position checkDownLeft(Position position, int color, int[][] state) {
        int oppColor = color == BLACK ? WHITE : BLACK;
        Position chkPos = new Position(position.x - 1, position.y + 1);

        // If check position is off the board, return null.
        if (isOutOfBounds(chkPos)) return null;

        // While check position is opponents color, check next position.
        while (state[chkPos.y][chkPos.x] == oppColor) {
            // Check next position.
            chkPos.x = chkPos.x - 1;
            chkPos.y = chkPos.y + 1;

            // If new position is the player's color or is off the board, no valid move exists.
            if (isOutOfBounds(chkPos) || state[chkPos.y][chkPos.x] == color) {
                return null;
            }

            // If new position is an empty square, then it is a valid move.
            if (state[chkPos.y][chkPos.x] == EMPTY) {
                return chkPos;
            }
        }

        // Check position wasn't opponents color, and thus could not lead to a valid move.
        return null;
    }

    /**
     * Checks for a valid move in the indicated direction.
     *
     * @param position the starting position
     * @param color    the color of the active player
     * @param state    the current board state
     * @return a valid move board position, or null if not found
     */
    private Position checkRight(Position position, int color, int[][] state) {
        int oppColor = color == BLACK ? WHITE : BLACK;
        Position chkPos = new Position(position.x + 1, position.y);

        // If check position is off the board, return null.
        if (isOutOfBounds(chkPos)) return null;

        // While check position is opponents color, check next position.
        while (state[chkPos.y][chkPos.x] == oppColor) {
            // Check next position.
            chkPos.x = chkPos.x + 1;
            chkPos.y = chkPos.y;

            // If new position is the player's color or is off the board, no valid move exists.
            if (isOutOfBounds(chkPos) || state[chkPos.y][chkPos.x] == color) {
                return null;
            }

            // If new position is an empty square, then it is a valid move.
            if (state[chkPos.y][chkPos.x] == EMPTY) {
                return chkPos;
            }
        }

        // Check position wasn't opponents color, and thus could not lead to a valid move.
        return null;
    }

    /**
     * Checks for a valid move in the indicated direction.
     *
     * @param position the starting position
     * @param color    the color of the active player
     * @param state    the current board state
     * @return a valid move board position, or null if not found
     */
    private Position checkLeft(Position position, int color, int[][] state) {
        int oppColor = color == BLACK ? WHITE : BLACK;
        Position chkPos = new Position(position.x - 1, position.y);

        // If check position is off the board, return null.
        if (isOutOfBounds(chkPos)) return null;

        // While check position is opponents color, check next position.
        while (state[chkPos.y][chkPos.x] == oppColor) {
            // Check next position.
            chkPos.x = chkPos.x - 1;
            chkPos.y = chkPos.y;

            // If new position is the player's color or is off the board, no valid move exists.
            if (isOutOfBounds(chkPos) || state[chkPos.y][chkPos.x] == color) {
                return null;
            }

            // If new position is an empty square, then it is a valid move.
            if (state[chkPos.y][chkPos.x] == EMPTY) {
                return chkPos;
            }
        }

        // Check position wasn't opponents color, and thus could not lead to a valid move.
        return null;
    }

    /**
     * Checks for a valid move in the indicated direction.
     *
     * @param position the starting position
     * @param color    the color of the active player
     * @param state    the current board state
     * @return a valid move board position, or null if not found
     */
    private Position checkUpRight(Position position, int color, int[][] state) {
        int oppColor = color == BLACK ? WHITE : BLACK;
        Position chkPos = new Position(position.x + 1, position.y - 1);

        // If check position is off the board, return null.
        if (isOutOfBounds(chkPos)) return null;

        // While check position is opponents color, check next position.
        while (state[chkPos.y][chkPos.x] == oppColor) {
            // Check next position.
            chkPos.x = chkPos.x + 1;
            chkPos.y = chkPos.y - 1;

            // If new position is the player's color or is off the board, no valid move exists.
            if (isOutOfBounds(chkPos) || state[chkPos.y][chkPos.x] == color) {
                return null;
            }

            // If new position is an empty square, then it is a valid move.
            if (state[chkPos.y][chkPos.x] == EMPTY) {
                return chkPos;
            }
        }

        // Check position wasn't opponents color, and thus could not lead to a valid move.
        return null;
    }

    /**
     * Checks for a valid move in the indicated direction.
     *
     * @param position the starting position
     * @param color    the color of the active player
     * @param state    the current board state
     * @return a valid move board position, or null if not found
     */
    private Position checkUp(Position position, int color, int[][] state) {
        int oppColor = color == BLACK ? WHITE : BLACK;
        Position chkPos = new Position(position.x, position.y - 1);

        // If check position is off the board, return null.
        if (isOutOfBounds(chkPos)) return null;

        // While check position is opponents color, check next position.
        while (state[chkPos.y][chkPos.x] == oppColor) {
            // Check next position.
            chkPos.x = chkPos.x;
            chkPos.y = chkPos.y - 1;

            // If new position is the player's color or is off the board, no valid move exists.
            if (isOutOfBounds(chkPos) || state[chkPos.y][chkPos.x] == color) {
                return null;
            }

            // If new position is an empty square, then it is a valid move.
            if (state[chkPos.y][chkPos.x] == EMPTY) {
                return chkPos;
            }
        }

        // Check position wasn't opponents color, and thus could not lead to a valid move.
        return null;
    }

    /**
     * Checks for a valid move in the indicated direction.
     *
     * @param position the starting position
     * @param color    the color of the active player
     * @param state    the current board state
     * @return a valid move board position, or null if not found
     */
    private Position checkUpLeft(Position position, int color, int[][] state) {
        int oppColor = color == BLACK ? WHITE : BLACK;
        Position chkPos = new Position(position.x - 1, position.y - 1);

        // If check position is off the board, return null.
        if (isOutOfBounds(chkPos)) return null;

        // While check position is opponents color, check next position.
        while (state[chkPos.y][chkPos.x] == oppColor) {
            // Check next position.
            chkPos.x = chkPos.x - 1;
            chkPos.y = chkPos.y - 1;

            // If new position is the player's color or is off the board, no valid move exists.
            if (isOutOfBounds(chkPos) || state[chkPos.y][chkPos.x] == color) {
                return null;
            }

            // If new position is an empty square, then it is a valid move.
            if (state[chkPos.y][chkPos.x] == EMPTY) {
                return chkPos;
            }
        }

        // Check position wasn't opponents color, and thus could not lead to a valid move.
        return null;
    }

    /**
     * Checks if the game board position is off the board.
     *
     * @param pos the position to check
     * @return returns true if the position is off the board
     */
    private boolean isOutOfBounds(Position pos) {
        if (pos.x < 0 || pos.x > 7 || pos.y < 0 || pos.y > 7) {
            return true;
        } else {
            return false;
        }
    }


}

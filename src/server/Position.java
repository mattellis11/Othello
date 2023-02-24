package server;

import java.io.Serializable;

/**
 * Represents a game board position.
 *
 * <li>x correlates to column position.</li>
 * <li>y correlates to row position.</li>
 * 
 */
public class Position implements Serializable {
    int x; // column
    int y; // row

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

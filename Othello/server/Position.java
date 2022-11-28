package server;

import java.io.Serializable;

public class Position implements Serializable
{
	int x; // column
	int y; // row
	
	public Position(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
}

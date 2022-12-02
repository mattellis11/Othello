package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Font;

public class BoardPanel extends JPanel
{
	private JButton[] gameSquares = new JButton[64];
	private JLabel blackScore;
	private JLabel whiteScore;
	private JLabel gameInfo;

	public BoardPanel(BoardControl bc)
	{				
	  // Use BorderLayout to lay out the components in this panel.
		this.setLayout(new BorderLayout());
		
		BoardControl controller = bc;
		
		Dimension buttonSize = new Dimension(50, 50);
		
		// Create score panel in the north.
		JPanel scorePanel = new JPanel();
		JLabel black = new JLabel("Black: ", JLabel.RIGHT);
		black.setFont(new Font("Tahoma", Font.PLAIN, 14));
		blackScore = new JLabel("02", JLabel.LEFT);
		blackScore.setFont(new Font("Tahoma", Font.PLAIN, 14));
		JLabel white = new JLabel("White: ", JLabel.RIGHT);
		white.setFont(new Font("Tahoma", Font.PLAIN, 14));
		whiteScore = new JLabel("02", JLabel.LEFT);
		whiteScore.setFont(new Font("Tahoma", Font.PLAIN, 14));
		JPanel buffer = new JPanel();
		buffer.add(black);
		buffer.add(blackScore);
		buffer.add(white);
		buffer.add(whiteScore);
		scorePanel.add(buffer);
		this.add(scorePanel, BorderLayout.NORTH);

		// Create a 8x8 Grid for the buttons
		JPanel grid = new JPanel();
		grid.setLayout(new GridLayout(8, 8, 2, 2));

		// Create Buttons and add them to the grid.
		int count = 0;
		for (int y = 1; y < 9; y++)
		{
			for (int x = 1; x < 9; x++)
			{
				if (y == 4 && x == 4 || y == 5 && x == 5)
				{
					gameSquares[count] = new JButton(new ImageIcon(BoardPanel.class.getResource("/client/images/white.png")));
					gameSquares[count].setActionCommand(String.valueOf(count));
					gameSquares[count].setPreferredSize(buttonSize);
					gameSquares[count].addActionListener(controller);
					grid.add(gameSquares[count]);
					
				} else if (y == 5 && x == 4 || y == 4 && x == 5)
				{
					gameSquares[count] = new JButton(new ImageIcon(BoardPanel.class.getResource("/client/images/black.png")));
					gameSquares[count].setActionCommand(String.valueOf(count));
					gameSquares[count].setPreferredSize(buttonSize);
					gameSquares[count].addActionListener(controller);
					grid.add(gameSquares[count]);
				} else
				{
					gameSquares[count] = new JButton(new ImageIcon(BoardPanel.class.getResource("/client/images/empty.png")));
					gameSquares[count].setActionCommand(String.valueOf(count));
					gameSquares[count].setPreferredSize(buttonSize);
					gameSquares[count].addActionListener(controller);
					grid.add(gameSquares[count]);
				}
				count++;
			}			
		}
		
		// Create outer panel for buffering
		JPanel outer = new JPanel();
		outer.add(grid);
		this.add(outer, BorderLayout.CENTER);
				
		// Create info panel in the south
		JPanel southPanel = new JPanel();
		gameInfo = new JLabel("", JLabel.CENTER);
		southPanel.add(gameInfo);
		this.add(southPanel, BorderLayout.SOUTH);
		
		this.setVisible(true);		
		
	}
	
	public JButton[] getSquares()
	{
		return gameSquares;
	}
	
	public void setScore(String black, String white)
	{
		blackScore.setText(black);
		whiteScore.setText(white);
	}
	
	public void setInfo(String message)
	{
		gameInfo.setText(message);
	}	
	

}

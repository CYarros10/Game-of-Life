//==========================================================================
// Author : Christian Yarros
// Date : 5/10/2015
// Class: GamePanel.java
//==========================================================================

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class GamePanel extends JFrame {

	//==========================================================================
	// Variables
	//==========================================================================

	public static int ROWS = BoardInfo.ROWS; //Row dimension of game board
	public static int COLS = BoardInfo.COLS; //Column dimension of game board

	public static BoardPanel[][] squares;
	public JPanel container;

	//==========================================================================
	// Visual Panel
	//==========================================================================
	/**
	 * GamePanel ---- Creates the visual aspect of the game through the use of 
	 * JPanel's. 
	 * @param rows,cols = dimensions of the game board.
	 * @return = no return value
	 */ 
	public GamePanel(int rows, int cols) {
		setTitle("Game of Life");
		setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

		JPanel container = new JPanel();
		getContentPane().add(container);

		squares = new BoardPanel[ROWS][COLS];

		container.setLayout(new GridLayout(ROWS,COLS));

		for (int row = 0; row < ROWS; row ++) {
			for (int col = 0; col < COLS; col++) {
				squares[row][col] = new BoardPanel();
				container.add(squares[row][col]);
			}
		}
		container.setPreferredSize(new Dimension(COLS*10,ROWS*10));
		setVisible (true);
		pack();
	}

	//==========================================================================
	// Redraw the board after each moment in time
	//==========================================================================

	/**
	 * redrawGamePanel --- visualizes the character locations on the game board 
	 * by accessing the 2D arrays of BoardInfo class.
	 * @param : bi = the current BoardInfo object being used for the game
	 * @return: none
	 */ 
	public void redrawGamePanel(BoardInfo bi) {

		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				if (BoardInfo.getMonsterLocations()[row][col] > 0) {
					squares[row][col].setColor(new Color(0, 0, 0)); // Monster = Blue

				}
				else if (BoardInfo.getCreatureLocations()[row][col] > 0) {
					squares[row][col].setColor(new Color(0, 255, 0)); // Creature = Green
				}
				else if (BoardInfo.getStrawberryLocations()[row][col] > 0) {
					squares[row][col].setColor(new Color(255, 0, 0)); // Strawberry = Red
				}
				else if (BoardInfo.getMushroomLocations()[row][col] > 0) {
					squares[row][col].setColor(new Color(255, 255, 0)); // Mushroom = Yellow
				}
				else {
					squares[row][col].setColor(new Color(220, 220, 220)); // Nothing = White
				}
			}
		}
		repaint();
	}
}
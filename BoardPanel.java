//==========================================================================
// Author : Christian Yarros
// Date : 5/10/2015
// Class: BoardPanel.java
//==========================================================================


import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class BoardPanel extends JPanel {
	
	//==========================================================================
	// Visual item representations
	//==========================================================================

	Color color;

	/**
	 * BoardPanel--- constructor class, creates a square of a given color to
	 * represent items on the game board. Default = white
	 * @params : none
	 * @return : none
	 */ 
	public BoardPanel() {
		this(Color.white);
	}

	/**
	 * BoardPanel--- constructor class, creates a square of a given color to
	 * represent items on the game board. Default = white
	 * @params color = sets the square to equal the given color
	 * @return : none
	 */ 
	public BoardPanel(Color color) {
		this.color = color;
	}

	/**
	 * setColor --- sets color of given panel to the parameter passed
	 * @params color = the color that the square is being set to
	 * @return : none
	 */ 
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * paintComponent --- paints the square based on the given dimensions and color
	 * @params g = graphics object that is being colored
	 * @return : none
	 */ 
	public void paintComponent(Graphics g) {
		int width = getWidth();
		int height = getHeight();
		g.setColor(color);
		g.fillRect(0, 0, width, height);
	}
}
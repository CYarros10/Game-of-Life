//==========================================================================
// Author : Christian Yarros
// Date : 5/10/2015
// Class: Monster.java
//==========================================================================

import java.util.ArrayList;
import java.util.Random;


public class Monster {

	//==========================================================================
	// Variables
	//==========================================================================

	public static int ROWS = BoardInfo.ROWS;
	public static int COLS = BoardInfo.COLS;
	private int row; //monster row position
	private int col; //monster column position
	private int trackingNumber; // monster tracking number given at creation (testing purposes)
	private static int sight = 2; // monster vision range
	private static ArrayList<Monster> monsterList = new ArrayList<Monster>(); // monster objects
	private static ArrayList<Integer> monsterActionList = new ArrayList<Integer>(); // monster actions 
	private static Random random = new Random();

	//==========================================================================
	// Monster Constructor
	//==========================================================================

	/**
	 * Monster --- monster object constructor method. Monsters eat creatures. 
	 * @param row,col = position on board
	 * @param trackingNumber = number given at creation for testing purposes
	 * @return : none
	 */ 
	public Monster(int row, int col, int trackingNumber) {	
		this.row = row;
		this.col = col;
		this.trackingNumber = trackingNumber;
	}

	//==========================================================================
	// Attempt to Move Monster
	//==========================================================================

	/**
	 * attemptMove --- Monster attempts to move, based on its position. Makes sure
	 * the monster stays within the bounds of the board
	 * @param m = the Monster that is attempting the move
	 * @params targetR, targetC = the row, col that the monster is attempting to 
	 * move towards
	 * @return: none
	 */ 
	public static void attemptMove(Monster m, int targetRow, int targetCol) {
		int currentX = m.getRow();
		int currentY = m.getCol();

		// In bounds
		if (targetRow >= 0 && targetRow < ROWS && targetCol >= 0 && targetCol < COLS) {
			BoardInfo.getMonsterLocations()[currentX][currentY] -= 1; 
			BoardInfo.getMonsterLocations()[targetRow][targetCol] += 1; 
			m.setRow(targetRow);
			m.setCol(targetCol);
		}
	}

	//==========================================================================
	// Monster Movement
	//==========================================================================

	/**
	 * monsterMovement --- decide whether the monster will eat a creature, move 
	 * towards a creature, or move randomly throughout the board
	 * @param m = monster object that will act
	 * @return : none
	 */ 
	public static void monsterMovement(Monster m) {

		// By default, eat a creature if it's present
		if (creaturePresent(m)) {
			BoardInfo.getCreatureLocations()[m.getRow()][m.getCol()] --;

			for (Creature c : Creature.getCreatureList()) {
				if (c.getRow() == m.getRow() && c.getCol() == m.getCol()) {
					BoardInfo.getCreatureLocations()[c.getRow()][c.getCol()] --; //creature died, update board information
					Creature.getGraveyard().add(c); // creature died, add it to graveyard
				}
			}
		}

		// Otherwise, move based on the monster's vision or move randomly
		else {
			int row = m.getRow();
			int col = m.getCol();

			int direction = random.nextInt(4) + 1;

			// Move towards Creature
			if (monsterVision(m, BoardInfo.getCreatureLocations()) != 0) {
				direction = monsterVision(m, BoardInfo.getCreatureLocations());
			}

			// Move randomly
			if (direction == 1) 
				attemptMove(m, row-1, col); // Move Up

			else if (direction == 2) 
				attemptMove(m, row+1, col); // Move Down

			else if (direction == 3) 
				attemptMove(m, row, col+1); // Move Right

			else if (direction == 4) 
				attemptMove(m, row, col-1);	// Move Left
		}
	}

	//==========================================================================
	// Is a creature available to eat?
	//==========================================================================

	/**
	 * creaturePresent --- decide whether creature and monster occupy the same space
	 * @param m = specific monster object to be used
	 * @return boolean (True if present, false if not)
	 */ 
	public static boolean creaturePresent(Monster m) {
		for (Creature c : Creature.getCreatureList()) {
			if (m.getRow() == c.getRow() && m.getCol() == c.getCol()) {

				return true;
			}
		}
		return false;
	}

	//==========================================================================
	// Implement the monster's vision range
	//==========================================================================

	/**
	 * monsterVision --- Simulate the vision range of a monster to an 8-square space
	 * around the monster, then use manhattan distance formulat to decide which 
	 * creature (if any) is closest to the monster in the game board.
	 * @param m = monster object whose vision we are simualating
	 * @param locations = 2-D array of item locations that we are searching through (creatures)
	 * @return int that represents the direction to go to get towards the nearest item
	 */ 
	public static int monsterVision(Monster m, int[][] locations) {

		// Keep Vision inside Game board
		int minVisRow = Math.max(0, m.getRow() - sight);
		int maxVisRow = Math.min(m.getRow() + sight, ROWS - 1);
		int minVisCol = Math.max(0, m.getCol() - sight);
		int maxVisCol = Math.min(m.getCol() + sight, COLS - 1);

		boolean first = true; //ensure the first found creature is recorded
		int manhattanDistance = 0;
		int distRow = 0; // distance in row direction
		int distCol = 0; // distance in col direction

		int itemDirection = 0; // final direction to move


		//within range of creature
		for (int visionRow = minVisRow; visionRow <= maxVisRow; visionRow ++) {
			for(int visionCol = minVisCol; visionCol <= maxVisCol; visionCol ++) {

				// Don't check a square that creature is on.
				if (!(m.getRow() == visionRow && m.getCol() == visionCol)) {

					// Object has been sighted.
					if (locations[visionRow][visionCol] > 0) {

						// First sight, default values
						if(first) {
							manhattanDistance = Math.abs(visionRow - m.getRow()) + Math.abs(visionCol - m.getCol());
							distRow = visionRow - m.getRow();
							distCol = visionCol - m.getCol();
							first = false;
						}

						// Compare manhattan distances with each creature within vision range and record shortest distances
						else {
							if (manhattanDistance > Math.abs(visionRow - m.getRow()) + Math.abs(visionCol - m.getCol())) {
								manhattanDistance = Math.abs(visionRow - m.getRow()) + Math.abs(visionCol - m.getCol());
								distRow = visionRow - m.getRow();
								distCol = visionCol - m.getCol();
							}
						}
					}
				}
			}

			if (manhattanDistance != 0) {

				// Row priority
				if (Math.abs(distRow) >= Math.abs(distCol)) {
					if (distRow < 0) {
						itemDirection = 1; // Move up
					}
					else  {
						itemDirection = 2; // Move down
					}
				}

				// Column priority
				else {
					if (distCol > 0) {
						itemDirection = 3; // Move right
					}
					else {
						itemDirection = 4; // Move left
					}
				}
			}
		}
		return itemDirection;
	}

	//==========================================================================
	// Monster Getters/Setters
	//==========================================================================

	public static ArrayList<Monster> getMonsterList() {
		return monsterList;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public int getTrackingNum() {
		return trackingNumber;
	}

	public void setRow(int newRow) {
		this.row = newRow;
	}

	public void setCol( int newCol) {
		this.col = newCol;
	}

	public int getLifeForce() {
		// TODO Auto-generated method stub
		return 0;
	}
}


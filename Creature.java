//==========================================================================
// Author : Christian Yarros
// Date : 5/10/2015
// Class: Creature.java
//==========================================================================

import java.util.ArrayList;
import java.util.Random;


public class Creature {

	//==========================================================================
	// Variables
	//==========================================================================

	public static int ROWS = BoardInfo.ROWS; // Row dimensions to be used for creature positions
	public static int COLS = BoardInfo.COLS; // Column dimensions to be used for creature positions

	private static ArrayList<Integer> actionList = new ArrayList<Integer>(); // Holds action list for each creature at each time
	private static final int MOVEUP = 1;
	private static final int MOVEDOWN = 2;
	private static final int MOVERIGHT = 3;
	private static final int MOVELEFT = 4;
	private static final int EATSTRAWBERRY = 5;
	private static final int EATMUSHROOM = 6;

	//Creature Attributes
	private static int sight = 2; //vision range on board
	private int lifeForce; // number of actions creature can make before dying
	private int r; // creature row position
	private int c; // creature column position
	private double[] chromosome; // creature chromosome
	private int trackingNumber; // creature tracking numbers for testing purposes

	private static ArrayList<Creature> creatureList = new ArrayList<Creature>(); // arraylist to record live creatures
	private static ArrayList<Creature> parents = new ArrayList<Creature>(); // arraylist to record parents of future generations
	public static ArrayList<Creature> graveyard = new ArrayList<Creature>(); // arraylist to record dead creatures



	private static Random randomizer = new Random();

	//==========================================================================
	// Creature Constructor
	//==========================================================================

	/**
	 * Creature --- Creature object constructor
	 * @param r = row position 
	 * @param c = column position
	 * @param lifeForce = number of steps left that a creature can make
	 * @param chromosome = double array that influences action choices
	 * @param trackingNumber = number given at creation, for testing purposes 
	 * @return: none
	 */ 
	public Creature(int r, int c, int lifeForce, double[] chromosome, int trackingNumber) {	
		this.r = r;
		this.c = c;
		this.lifeForce = lifeForce;
		this.chromosome = chromosome;
		this.trackingNumber = trackingNumber;
	}

	//==========================================================================
	// Object interaction movement
	//==========================================================================

	/**
	 * act -- Decides what act a creature will attempt to make during one time step.
	 * @param c = the Creature that will be making the act
	 * @return : none
	 */ 
	public static void act(Creature c) {

		int row = c.getRow();
		int col = c.getCol();
		int action = (int) selectAction(c); 

		// Up direction = 1
		if (action == MOVEUP) {
			attemptMove(c, row-1, col);
		}

		// Down direction = 2
		else if (action == MOVEDOWN) {
			attemptMove(c, row+1, col);
		}

		// east direction = 3
		else if (action == MOVERIGHT) {
			attemptMove(c, row, col+1);
		}

		// west direction = 4
		else if (action == MOVELEFT) {
			attemptMove(c, row, col-1);
		}

		else if (action == EATSTRAWBERRY) {
			BoardInfo.getStrawberryLocations()[c.getRow()][c.getCol()] --; //strawberry eaten, update board info
			//c.lifeForce --;
			c.lifeForce += 5; // eating a strawberry promotes lifeForce and chances of survival
		}

		else if (action == EATMUSHROOM) {
			BoardInfo.getMushroomLocations()[c.getRow()][c.getCol()] --; //mushroom eaten, update board info
			BoardInfo.getCreatureLocations()[c.getRow()][c.getCol()] --; //creature died, update board info
			c.lifeForce = 0; // eating a mushroom kills the creature
			graveyard.add(c); // add dead creature to graveyard
		}
	}

	//==========================================================================
	// Attempt Move
	//==========================================================================

	/**
	 * attemptMove --- Creature attempts to move, based on its position. Makes sure
	 * the creature stays within the bounds of the board and also updates lifeForce
	 * @param c = Creature that is attempting the move
	 * @params targetR, targetC = the row, col that the creature is attempting to 
	 * move towards
	 * @return: none
	 */ 
	public static void attemptMove(Creature c, int targetR, int targetC) {

		BoardInfo.getCreatureLocations()[c.getRow()][c.getCol()] --; 
		c.lifeForce --;

		// Keep things in bounds
		if (targetR >= 0 && targetR < ROWS && targetC >= 0 && targetC < COLS) {
			c.setRow(targetR);
			c.setCol(targetC);
		}

		// Move has been attempted, life force has weakened.
		BoardInfo.getCreatureLocations()[c.getRow()][c.getCol()] ++; 

		// If creature has no life force, it has died.
		if (c.lifeForce <= 0) {
			BoardInfo.getCreatureLocations()[c.getRow()][c.getCol()] --;  //update board info
			graveyard.add(c); //add creature to graveyard
		}	
	}

	//==========================================================================
	// Select Action for creature
	//==========================================================================

	/**
	 * selectAction --- Decipher how a creature's chromosome influences its actions
	 * in its current environment and vision range
	 * @param c = Creature that we are selecting the action for
	 * @return = double that represents the creature's desired action
	 */ 
	public static double selectAction(Creature c) {

		int strongestWeight = -1; // in case the actual strongest weight of the list is zero, set to -1
		int idealAction = 0;
		int startWeightSection = 7; // weights of chromosome

		actionList.clear(); // Make sure the actionList is empty at start.

		//-----------------------------------------------------------------------------
		// Decide which chromosome numbers are needed in Creatures current environment.
		//-----------------------------------------------------------------------------

		if (strawberryPresent(c)) {
			if(c.getChromosome()[0] > 0) {
				actionList.add(0); //'strawberry present' action added to current possible action list
			}
		}

		if (mushroomPresent(c)) {
			if(c.getChromosome()[1] > 0) {
				actionList.add(1); //'mushroom present' action added to current possible action list
			}
		}

		if (creatureVision(c, BoardInfo.getStrawberryLocations()) > 0) {
			if (c.getChromosome()[2] > 0) {
				actionList.add(2); //'strawberry nearby' action added to current possible action list
			}
		}

		if (creatureVision(c, BoardInfo.getMushroomLocations()) > 0) {
			if (c.getChromosome()[3] > 0) {
				actionList.add(3); //'mushroom nearby' action added to current possible action list
			}
		}

		if (creatureVision(c, BoardInfo.getMonsterLocations()) > 0) {
			if (c.getChromosome()[4] > 0) {
				actionList.add(4); //'monster nearby' action added to current possible action list
			}
		}

		if (creatureVision(c, BoardInfo.getCreatureLocations()) > 0) {
			if (c.getChromosome()[5] != 0) {
				actionList.add(5); //'creature nearby' action added to current possible action list
			}
		}

		// If none of these actions are applicable given the creatures current environment, 
		// refer to the creatures default action
		if (actionList.isEmpty()) {
			if (c.getChromosome()[6] == 0) {
				return randomizer.nextInt(4) + 1; // chromosome states that default action is random
			}
			else
				return c.getChromosome()[6]; //chromosome states default action is one direction (up,down,left,right)
		}


		// Some actions are applicable, so we must refer to their priorities in the creatures chromosome.
		else  {

			for (Integer i : actionList) {
				if (strongestWeight < c.getChromosome()[startWeightSection + i]) {
					strongestWeight = (int) c.getChromosome()[startWeightSection + i];
					idealAction = i;
				}
			}

			switch (idealAction) {

			case 0:
				return EATSTRAWBERRY; // Creature wants to eat strawberry

			case 1:
				return EATMUSHROOM; // Creature wants to eat mushroom

			case 2:
				return moveDecider(c, 2, BoardInfo.getStrawberryLocations()); //Move is dependent on nearest Strawberry chromosome

			case 3:
				return moveDecider(c, 3, BoardInfo.getMushroomLocations()); //Move is dependent on nearest mushroom chromosome

			case 4:
				return moveDecider(c, 4, BoardInfo.getMonsterLocations()); // Move is dependent on nearest Monster chromosome

			case 5:
				return moveDecider(c, 5, BoardInfo.getCreatureLocations()); //Move depends on nearest creature chromosome
			}
		}
		return -1; // otherwise no action was selected
	}

	//==========================================================================
	// Move Decider
	//==========================================================================

	/**
	 * moveDecider --- after deciphering the creatures chromosome, we have discoverd
	 * that it would like to move based on a nearby item. This method uses the specific
	 * chromosome that decides if the creature would like to move towards, away, random, 
	 * or ignore based on that given item. 
	 * @param c = Creature that is deciding its move
	 * @param chromoNum = the chromosome number that reflects the action chosen
	 * @param locations = the nearby item's given board locations
	 * @return = integer that represent the movement chosen
	 */ 
	public static int moveDecider(Creature c, int chromoNum, int[][] locations) {

		// finds the direction of the nearest item in creature's vision
		int directionOfItem = Creature.creatureVision(c, locations);

		// Moving towards item
		if (c.getChromosome()[chromoNum] == 1)
			return directionOfItem;

		//Moving Away from item
		else if (c.getChromosome()[chromoNum] == 2) {
			if (directionOfItem == 1) {
				return MOVEDOWN; // item is UP; move south
			}
			else if (directionOfItem == 2) {
				return MOVEUP; // Monster is down, move north
			}
			else if (directionOfItem == 3) {
				return MOVELEFT; // Monster is right, move west
			}
			else {
				return MOVERIGHT; // Monster is left, move east
			}	
		}

		// Random Movement
		else if (c.getChromosome()[chromoNum] == 3){
			return randomizer.nextInt(4) + 1;
		}
		return -1;
	}

	//==========================================================================
	// Is a strawberry or mushroom present?
	//==========================================================================

	/**
	 * strawberryPresent --- Decides whether a creature and a strawberry share the
	 * same location on the current game board.
	 * @param c = Creature to be checked
	 * @return = boolean whether creature and strawberry share same position
	 */ 
	public static boolean strawberryPresent(Creature c) {
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				if (BoardInfo.getStrawberryLocations()[c.getRow()][c.getCol()] > 0) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * strawberryPresent --- Decides whether a creature and a mushroom share the
	 * same location on the current game board.
	 * @param c = Creature to be checked
	 * @return = boolean whether creature and mushroom share same position
	 */ 
	public static boolean mushroomPresent(Creature c) {
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col ++) {
				if (c.getRow() == row && c.getCol() == col)  {
					if (BoardInfo.getMushroomLocations()[row][col] > 0) {
						return true;
					}
				}
			}
		}
		return false;
	}

	//==========================================================================
	// Implement the creature's vision range
	//==========================================================================

	/**
	 * creatureVision --- Simulate the vision range of a creature to an 8-square space
	 * around the monster, then use manhattan distance formula to decide which 
	 * item (if any) is closest to the creature in the game board.
	 * @param c = the creature who's vision needs to be created
	 * @param locations = 2-D array of items (mushrooms, monsters, creatures, strawberries)
	 * @return int = representation for direction the monster must go to get to
	 * the nearest item in the locations 2-D array
	 */ 
	public static int creatureVision(Creature c, int[][] locations) {

		// Restrains the creatures vision to the bounds of the game board.
		int minVisRow = Math.max(0, c.getRow() - sight);
		int maxVisRow = Math.min(c.getRow() + sight, ROWS -1);
		int minVisCol = Math.max(0, c.getCol() - sight);
		int maxVisCol = Math.min(c.getCol() + sight, COLS - 1);

		boolean first = true; //Ensure that the first item found records the manhattan distance
		int manhattanDistance = 0;

		int distRow = 0; //distance in row direction
		int distCol = 0; //distance in column direction
		int itemDirection = 0; // integer representation of final direction to move

		// Start checking spaces around the creature
		for (int visionRow = minVisRow; visionRow <= maxVisRow; visionRow ++) {
			for(int visionCol = minVisCol; visionCol <= maxVisCol; visionCol ++) {

				// Don't check a square that creature is on.
				if (!(c.getRow() == visionRow && c.getCol() == visionCol)) {

					// Object has been sighted.
					if (locations[visionRow][visionCol] > 0) {

						// First sight, default values
						if(first) {
							manhattanDistance = Math.abs(visionRow - c.getRow()) + Math.abs(visionCol - c.getCol());
							distRow = visionRow - c.getRow();
							distCol = visionCol - c.getCol();
							first = false;
						}

						// Compare manhattan distances with each item within vision range and record shortest distances
						else {
							if (manhattanDistance > Math.abs(visionRow - c.getRow()) + Math.abs(visionCol - c.getCol())) {
								manhattanDistance = Math.abs(visionRow - c.getRow()) + Math.abs(visionCol - c.getCol());
								distRow = visionRow - c.getRow();
								distCol = visionCol - c.getCol();
							}
						}
					}
				}
			}

			if (manhattanDistance != 0) {
				// Row distance priority
				if (Math.abs(distRow) >= Math.abs(distCol)) {
					if (distRow < 0) {
						itemDirection = MOVEUP;
					}
					else  {
						itemDirection = MOVEDOWN;
					}
				}
				// Column distance priority
				else {
					if (distCol > 0) {
						itemDirection = MOVERIGHT;
					}
					else {
						itemDirection = MOVELEFT;
					}
				}
			}
		}
		return itemDirection;
	}

	//==========================================================================
	// Randomly select parents for the next generation
	//==========================================================================

	/**
	 * fittestParents --- Select most fit parents via the tournament genetic 
	 * algorithm (Selects a subset of n random creatures, and the fittest of that 
	 * subset becomes a parent) - repeat until numberOfParents size is satisfied.
	 * @param cList = arraylist of creatures that survived the last generation
	 * @return arraylist of selected creatures to be parents
	 */ 
	@SuppressWarnings("null")
	public static ArrayList<Creature> fittestParents(ArrayList<Creature> cList) {
		parents.clear();
		int numberOfParents = 4;
		int desiredSize = 6;
		ArrayList<Creature> subset = new ArrayList<Creature>(); 
		boolean firstSelection = true; // Ensure the first creature in a subset is recorded as best parent for comparison

		Creature bestParent = null;

		// If there are less survivors than the ideal subset, all creatures in cList are parents.
		if (desiredSize >= cList.size()) {
			for (Creature c : cList) {
				parents.add(c);
			}
		}

		//Get subsetN fittest creatures for 'mating' 
		else {
			while (parents.size() < numberOfParents) {
				while (subset.size() < desiredSize){
					Creature nextRandom = cList.get(randomizer.nextInt(cList.size())); // random creature in list

					// Ensure no duplicate creatures in subset
					if (!subset.contains(nextRandom)){
						subset.add(nextRandom); 
					}
				}

				for (Creature c : subset) {
					if (firstSelection) {
						bestParent = c; // First creature checked is best parent of subset by default
						firstSelection = false;
					}

					// Ensure unique parents
					else if(!parents.contains(c)) {
						if (bestParent.getLifeForce() < c.getLifeForce()) {
							bestParent = c; // best parent has highest lifeForce value
						}
					}
				}
				parents.add(bestParent);
				bestParent = null;
				firstSelection = true;
			}
		}
		return parents;
	}


	//==========================================================================
	// create a new chromosome from 2 random parents
	//==========================================================================

	/**
	 * nextChromo --- Takes chromosomes of 2 parents and creates a new, unique chromosome 
	 * @param parents = arraylist of creatures that survived the last generation and were selected as parents
	 * @return double array that represents the brand new chromosome
	 */ 
	public static double[] nextChromo(ArrayList<Creature> parents) {

		int randomIndex1 = randomizer.nextInt(parents.size()); 
		int randomDiff = randomIndex1;
		
		// Ensure two unique random parents
		while (randomDiff == randomIndex1) {
			randomDiff = randomizer.nextInt(parents.size());
		}
		
		int randomIndex2 = (randomIndex1 + randomDiff) % parents.size();
		Creature parent1 = parents.get(randomIndex1); // random parent from the parent list
		Creature parent2 = parents.get(randomIndex2); // second random parent from the parent list

		int length = parent1.getChromosome().length; // length of the chromosome array

		int crossover = randomizer.nextInt(length); //crossover point to split the chromosomes of parents

		double[] nextChromo = new double[length]; // new chromosome

		for (int i = 0; i < crossover; i ++) {
			nextChromo[i] = parent1.getChromosome()[i]; // add chromosome pieces from parent 1 until crossover point
		}

		for (int j = crossover; j < length; j ++) {
			nextChromo[j] = parent2.getChromosome()[j]; // Add chromsome pieces from parent 2 after crossover point
		}

		//------------------------------------------------
		// Simulate mutations for variance among children
		//------------------------------------------------

		double mutationRate = randomizer.nextDouble(); // Chance for mutation to occur = 1%

		if (mutationRate < 0.05) {
			int mutationIndex = randomizer.nextInt(length);

			if (mutationIndex == 0 || mutationIndex == 1) {			
				nextChromo[mutationIndex] = randomizer.nextInt(1); // Change chromosome for eat actions
			}

			else if (mutationIndex > 1 && mutationIndex < 7) {
				nextChromo[mutationIndex] = randomizer.nextInt(4); // Change chromosome for move actions
			}
			else
				nextChromo[mutationIndex] = randomizer.nextInt(10); // Change chromosome for weights
		}
		return nextChromo;	
	}

	//==========================================================================
	// Creature Getters/Setters
	//==========================================================================

	public static ArrayList<Creature> getCreatureList() {
		return creatureList;
	}

	public int getRow() {
		if (r < 0) {
			return 0;
		}
		if (r > ROWS) {
			return ROWS;
		}
		return r;
	}

	public int getCol() {
		if (c < 0) {
			return 0;
		}
		if (c > COLS) {
			return COLS;
		}
		return c;
	}

	public int getLifeForce() {
		return lifeForce;
	}

	public void setRow(int newX) {
		this.r = newX;
	}

	public void setCol(int newY) {
		this.c = newY;
	}

	public int getTrackingNumber() {
		return trackingNumber;
	}
	public double[] getChromosome() {
		return chromosome;
	}
	public static ArrayList<Creature> getGraveyard() {
		return graveyard;
	}
}
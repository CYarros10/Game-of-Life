//==========================================================================
// Author : Christian Yarros
// Date : 5/10/2015
// Class: BoardInfo.java
//==========================================================================

import java.util.ArrayList;
import java.util.Random;

public class BoardInfo {

	//==========================================================================
	// Variables
	//==========================================================================

	public static final int ROWS = 40; // Row dimension for game board
	public static final int COLS = 40; // Column dimension for game board

	private static int[][] strawberryLocations = new int[ROWS][COLS];
	private static int[][] mushroomLocations = new int[ROWS][COLS];
	private static int[][] creatureLocations = new int[ROWS][COLS];
	private static int[][] monsterLocations = new int[ROWS][COLS];

	private static int generations = 50; // Number of generations that will be created with new, different creatures
	private static int time = 50; // Time steps in one generation (# of actions a creature can make)
	private static int lifeForce = 40; // Total energy a creature has, 1 action = -1 lifeForce. 0 lifeForce = death
	private static int timeDelay = 300; // Time Delay for visual purposes (ms)

	//Starting populations for each item in the game
	private static int strawberryPopulation = 200; 
	private static int mushroomPopulation = 100;
	private static int creaturePopulation = 100; 
	private static int monsterPopulation = 10;

	//totalLifeForce among all items over the entire timespan
	// (# of creatures * lifeForce) +  (# of strawberries * energy per strawberry) - (time*creature)
	// (50 * 60) + (100*5) - (50*50)
	// totalLifeForce = 1000

	// Monsters move at different pace than creatures
	private static int monsterStep = 2;

	private static Random randomizer = new Random();

	//==========================================================================
	// Initial Generation Board Info
	//==========================================================================

	/**
	 * initialBoardInfo --- Create the information required to set up the game
	 * board, including first generation of creature, monster, strawberry and 
	 * mushroom locations on board.
	 * @param : none
	 * @return: none
	 */ 
	public static void initialBoardInfo() {
		int strawberries = strawberryPopulation;
		int mushrooms = mushroomPopulation;
		int creatures = creaturePopulation;
		int monsters = monsterPopulation;

		int randomRow = 0;
		int randomCol = 0;

		//---------------------------------------------------------------------
		// Randomly place items throughout the board and update their locations
		//---------------------------------------------------------------------

		while (monsters > 0) {
			randomRow = randomizer.nextInt(ROWS);
			randomCol = randomizer.nextInt(COLS);
			monsterLocations[randomRow][randomCol] ++;
			Monster.getMonsterList().add(new Monster(randomRow,randomCol, monsters));
			monsters --;
		}

		while (creatures > 0) {
			randomRow = randomizer.nextInt(ROWS);
			randomCol = randomizer.nextInt(COLS);
			creatureLocations[randomRow][randomCol] ++;
			double[] newChromosome = newChromosome();
			Creature.getCreatureList().add(new Creature(randomRow,randomCol, lifeForce, newChromosome, creatures));
			creatures --;
		}

		while(strawberries > 0) {
			randomRow = randomizer.nextInt(ROWS);
			randomCol = randomizer.nextInt(COLS);
			strawberryLocations[randomRow][randomCol] ++;
			strawberries --;
		}

		while(mushrooms > 0) {
			randomRow = randomizer.nextInt(ROWS);
			randomCol = randomizer.nextInt(COLS);
			if (strawberryLocations[randomRow][randomCol] == 0) {
				mushroomLocations[randomRow][randomCol] ++; //Ensure strawberry and mushrooms share different spaces
				mushrooms --;
			}
		}
	}


	//==========================================================================
	// New Generation Board Info
	//==========================================================================

	/**
	 * newGeneration --- Offers the same setup as initialBoardInfo but allows for
	 * new creatures to be created based on parents of the previous generation
	 * @param : none
	 * @return: none
	 */ 
	public static void newGeneration() {

		// Clear past board and reset variables
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				creatureLocations[r][c] = 0;
				monsterLocations[r][c] = 0;
				mushroomLocations[r][c] = 0;
				strawberryLocations[r][c] = 0;
			}
		}

		int strawberries = strawberryPopulation;
		int mushrooms = mushroomPopulation;
		int creatures = creaturePopulation;
		int monsters = monsterPopulation;

		// Record survivors for mating
		ArrayList<Creature> survivors = new ArrayList<Creature>();

		for (Creature c: Creature.getCreatureList()) {
			survivors.add(c);
		}

		// Clear all object lists
		Creature.getCreatureList().clear();
		Monster.getMonsterList().clear();
		Creature.graveyard.clear();

		int randomRow = 0;
		int randomCol = 0;

		//---------------------------------------------------------------------
		// Randomly place items throughout the board and update their locations
		//---------------------------------------------------------------------

		while (monsters > 0) {
			randomRow = randomizer.nextInt(ROWS);
			randomCol = randomizer.nextInt(COLS);
			monsterLocations[randomRow][randomCol] ++;
			Monster.getMonsterList().add(new Monster(randomRow,randomCol, monsters));
			monsters --;
		}

		while (creatures > 0) {
			randomRow = randomizer.nextInt(ROWS);
			randomCol = randomizer.nextInt(COLS);
			creatureLocations[randomRow][randomCol] ++;
			double[] nextGenChromo = Creature.nextChromo(Creature.fittestParents(survivors));
			Creature.getCreatureList().add(new Creature(randomRow,randomCol, lifeForce, nextGenChromo, creatures));
			creatures --;
		}

		while(strawberries > 0) {
			randomRow = randomizer.nextInt(ROWS);
			randomCol = randomizer.nextInt(COLS);
			strawberryLocations[randomRow][randomCol] ++;
			strawberries --;
		}

		while(mushrooms > 0) {
			randomRow = randomizer.nextInt(ROWS);
			randomCol = randomizer.nextInt(COLS);
			if (strawberryLocations[randomRow][randomCol] == 0) {
				mushroomLocations[randomRow][randomCol] ++; //Ensure strawberry and mushrooms share different spaces
				mushrooms --;
			}
		}
	}

	//==========================================================================
	// Build a new Chromosome
	//==========================================================================

	/**
	 * newChromosome --- Creates a 'chromosome' for each creature from first
	 * generation through complete randomness that affects the way a creature 
	 * behaves throughout a game.
	 * @param : none
	 * @return: double array that holds each chromome attribute
	 */ 
	public static double[] newChromosome() {

		double r = randomizer.nextDouble();

		double[] c = new double[13];

		//-------------------------------------
		//action to do when strawberry present 
		//-------------------------------------
		if (r < .50) {
			c[0] = 1; // Eat
		}
		else {
			c[0] = 0; // Don't Eat
		}

		//-------------------------------------
		//action to do when mushroom present 
		//-------------------------------------
		r = randomizer.nextDouble() +.01;;

		if (r < .50) {
			c[1] = 1; // Eat
		}
		else {
			c[1] = 0; // Don't Eat
		}

		//-------------------------------------
		//action to do when strawberry is near
		//-------------------------------------
		r =  randomizer.nextDouble() +.01;;

		if (r < .5 && r > .25) {
			c[2] = 1; // Move Towards
		}

		else if (r < .75 && r > .5) {
			c[2] = 2; // Move Away
		}

		else if (r < 1 && r > .75) {
			c[2] = 3; // Move Random
		}
		else {
			c[2] = 0; // Ignore
		}

		//--------------------------------
		//action on nearest mushroom
		//--------------------------------
		r =  randomizer.nextDouble() +.01;

		if (r < .5 && r > .25) {
			c[3] = 1; // Move Towards
		}

		else if (r < .75 && r > .5) {
			c[3] = 2; // Move Away
		}

		else if (r < 1 && r > .75) {
			c[3] = 3; // Move Random
		}
		else {
			c[3] = 0; // Ignore
		}

		//-------------------------------
		// action on nearest monster
		//-------------------------------
		r =  randomizer.nextDouble() +.01;

		if (r < .5 && r > .25) {
			c[4] = 1; // Move Towards
		}

		else if (r < .75 && r > .5) {
			c[4] = 2; // Move Away
		}

		else if (r < 1 && r > .75) {
			c[4] = 3; // Move Random
		}
		else {
			c[4] = 0; // Ignore
		}


		//-------------------------------
		// action on nearest creature
		//-------------------------------
		r =  randomizer.nextDouble();

		if (r < .5 && r > .25) {
			c[5] = 1; // Move Towards
		}

		else if (r < .75 && r > .5) {
			c[5] = 2; // Move Away
		}

		else if (r < 1 && r > .75) {
			c[5] = 3; // Move Random
		}
		else {
			c[5] = 0; // Ignore
		}

		//-------------------------------
		// Default Action
		//-------------------------------
		r =  randomizer.nextDouble();

		if (r <= .4  && r > .20) {
			c[6] = 1; // Move Up
		}

		else if (r <= .6 && r > .4) {
			c[6] = 2; // Move Down
		}

		else if (r <= .8 && r > .6) {
			c[6] = 3; // Move Right
		}

		else if (r <= 1 && r > .8) {
			c[6] = 4; // Move Left
		}

		// random
		else {
			c[6] = 0; // Move Random
		}

		//--------------------------------------------------
		// weight 8-13 (To offer priority level to actions)
		//--------------------------------------------------

		c[7] = randomizer.nextInt(10);

		c[8] = randomizer.nextInt(10);

		c[9] = randomizer.nextInt(10);

		c[10] = randomizer.nextInt(10);

		c[11] = randomizer.nextInt(10);

		c[12] = randomizer.nextInt(10);

		return c;
	}


	//==========================================================================
	// Getters
	//==========================================================================

	public static int[][] getStrawberryLocations() {
		return strawberryLocations;
	}

	public static int[][] getMushroomLocations() {
		return mushroomLocations;
	}

	public static int[][] getMonsterLocations() {
		return monsterLocations;
	}

	public static int[][] getCreatureLocations() {
		return creatureLocations;
	}

	//==========================================================================
	// Start the Program
	//==========================================================================

	/**
	 * main --- Initializes objects and starts the game. 
	 * @param args : A string array containing the command line arguments.
	 * @return: none
	 */ 
	public static void main(String [ ] args) throws InterruptedException {

		boolean initial = true; // Ensure the original generation is only created once
		GamePanel gp = new GamePanel(ROWS, COLS);
		BoardInfo newGame = null;
		int totalLifeForce = 0;
		for (int g = 0; g < generations; g ++) {

			totalLifeForce = 0;
			
			if (initial) {
				System.out.println("Initial Generation has begun.");
				newGame = new BoardInfo();
				initialBoardInfo();
				gp.redrawGamePanel(newGame); // Creates visuals
				initial = false; // Ensure the original generation is only created once
			}

			else {
				newGame = new BoardInfo();
				newGeneration();
				gp.redrawGamePanel(newGame);
			}


			for (int t = 0; t < time; t ++) {

				// Monsters move at different pace than creatures
				if (t % monsterStep == 0) {
					for (Monster m : Monster.getMonsterList()) {
						Monster.monsterMovement(m); // Monsters move
					}
					Creature.getCreatureList().removeAll(Creature.getGraveyard()); // Remove all creatures that were eaten.
				}

				for (Creature c : Creature.getCreatureList()) {
					Creature.act(c); // Creatures move
				}				

				Creature.getCreatureList().removeAll(Creature.getGraveyard()); // Remove all creatures where lifeForce = 0

				gp.redrawGamePanel(newGame); // Continuously visualize each step of the game

				try {
					if (g == -1 || g == generations) {
						Thread.sleep(timeDelay); // Time Delay for visual purposes
					}
				} 

				catch (InterruptedException e) { throw e;}	

			}
			for (Creature c : Creature.getCreatureList()){
				totalLifeForce += c.getLifeForce();
			}
			System.out.println(g+"\t"+ (double) totalLifeForce / creaturePopulation);
		}
		System.out.println("Average fitness per generation: " + (double) totalLifeForce/generations);
	}
}
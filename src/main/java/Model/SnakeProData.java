package Model;

import Controller.TestGame;
import Model.BoardCell;
import Model.CellType;
import Model.Preferences;
import Model.SnakeMode;

import java.awt.*;
import java.lang.Math;
import java.util.ArrayList;
import java.util.LinkedList;


public class SnakeProData {

	public int MouseX;
	public int MouseY;

	/** Collection of all BoardCells */
	private BoardCell[][] boardCells;

	/** Number of non-wall cells */
	private int freeSpots = 0;

	/** Current Direction or AI Mode */
	private SnakeMode currentMode = SnakeMode.GOING_EAST;

	/** Current Direction or AI Mode */
	private SnakeMode nextMode = SnakeMode.GOING_EAST;
	
	/** Linked List of cells that contains food */
	private LinkedList<BoardCell> foodCells = new LinkedList<BoardCell>();
	
	/** Linked List of  cells that contain the snake (head is last element of list) */
	private LinkedList<BoardCell> snakeCells = new LinkedList<BoardCell>();

	/** Array List of all the menu buttons*/
	private ArrayList<MenuButton> menuButtons = new ArrayList<MenuButton>();

	private MenuButton playAgain;

	/** Whether the game is pause */
	private boolean paused = false;

	/** Whether the game is done */
	private boolean gameOver = false;

	/** Whether the AI can reverse */
	private boolean AIReverse = false;

	/**
	 * Constructor
	 * Creates a board with walls on the boundaries and open in the interior.
	 */
	public SnakeProData() {
		int height = Preferences.NUM_CELLS_TALL;
		int width = Preferences.NUM_CELLS_WIDE;
		this.boardCells = new BoardCell[height][width];

		// Place walls around the outside
		this.addWalls();

		// Fill the remaining cells
		this.fillRemainingCells();

		// Add Button Framework
		this.addButtons();
	}

	/**
	 * Reset SnakeProData
	 */
	public void resetData() {
		int height = Preferences.NUM_CELLS_TALL;
		int width = Preferences.NUM_CELLS_WIDE;
		this.boardCells = new BoardCell[height][width];

		// Place walls around the outside
		this.addWalls();

		// Fill the remaining cells
		this.fillRemainingCells();

		this.foodCells = new LinkedList<BoardCell>();

		this.snakeCells = new LinkedList<BoardCell>();

		this.freeSpots = 0;

		this.currentMode = SnakeMode.GOING_EAST;
		this.nextMode = SnakeMode.GOING_EAST;
	}

	/* ------------------- */
	/* Option Menu Buttons */
	/* ------------------- */
	/**
	 * Adds Menu Buttons Framework
	 */
	private void addButtons() {
		menuButtons.add(new MenuButton(
				0, 0,
				Preferences.GAMEWIDTH, Preferences.MENU_HEIGHT-1,
				"Resume Game"));
		menuButtons.add(new MenuButton(
				0, Preferences.MENU_HEIGHT,
				Preferences.GAMEWIDTH, Preferences.MENU_HEIGHT-1,
				"Start New Game"));
		menuButtons.add(new MenuButton(
				0,Preferences.MENU_HEIGHT*2,
				Preferences.GAMEWIDTH, Preferences.MENU_HEIGHT-1,
				"AI - "));
		playAgain = new MenuButton(
				293,427,
				414,90,
				"Play Again?");
	}

	public ArrayList<MenuButton> getMenuButtons() {
		return this.menuButtons;
	}

	public MenuButton getPlayAgain() {
		return this.playAgain;
	}

	public boolean getAIReverse() { return this.AIReverse; }

	public void setAIReverse(boolean AIReverse) {this.AIReverse = AIReverse;}

	/**
	 * Adds walls around the outside
	 */
	private void addWalls() {
		int height = this.getNumRows();
		int width = this.getNumColumns();

		// Add Left and Right Walls
		for (int row = 0; row < height; row++) {
			this.boardCells[row][0] = new BoardCell(row, 0, CellType.WALL);
			this.boardCells[row][width - 1] = new BoardCell(row, width - 1, CellType.WALL);
		}

		// Add top and bottom walls
		for (int column = 0; column < width; column++) {
			this.boardCells[0][column] = new BoardCell(0, column, CellType.WALL);
			this.boardCells[height - 1][column] = new BoardCell(height - 1, column, CellType.WALL);
		}
	}

	/** 
	 * Add open board cells to empty board cells and set this.freeSpots to the number of OPEN cells
	 */
	private void fillRemainingCells() {
		int height = this.getNumRows();
		int width = this.getNumColumns();

		this.freeSpots = 0;
		for (int row = 0; row < height; row++) {
			for (int column = 0; column < width; column++) {
				if (this.boardCells[row][column] == null) {
					this.boardCells[row][column] = new BoardCell(row, column, CellType.OPEN);
					this.freeSpots++;
				}
			}
		}
	}

	/**
	 * Puts the snake in the upper-left corner of the walls, facing east.
	 */
	public void placeSnakeAtStartLocation() {
		BoardCell body = this.getCell(1, 1);
		BoardCell head = this.getCell(1, 2);
		this.snakeCells.addLast(body);
		this.snakeCells.addLast(head);
		head.becomeHead();
		body.becomeBody();
	}

	/* --------------------------------------------- */
	/* Methods to access information about the Board */
	/* --------------------------------------------- */
	/**
	 * @return Are we in AI mode?
	 */
	public boolean inAImode() {
		return this.nextMode == SnakeMode.AI_MODE;
	}

	/**
	 * @return currentMode
	 */
	public SnakeMode getCurrentMode() { return this.currentMode; }

	/**
	 * @return the height of the Board (including walls) in cells.
	 */
	public int getNumRows() { return this.boardCells.length; }

	/**
	 * @return The width of the Board (including walls) in cells.
	 */
	public int getNumColumns() { return this.boardCells[0].length; }

	/** Access a cell at a particular location.
	 * @param r  between 0 and this.getNumRows()-1 inclusive
	 * @param c  between 0 and this.getNumColumns()-1 inclusive
	 * @return cell c in row r.
	 */
	public BoardCell getCell(int r, int c) {
		if (r >= this.getNumRows() || c >= this.getNumColumns() || r < 0 || c < 0) {
			System.err.println("Trying to access cell outside of the Board:");
			System.err.println("row: " + r + " col: " + c);
			System.exit(0);
		}
		return this.boardCells[r][c];
	}

	public LinkedList<BoardCell> getSnakeCells() {
		return this.snakeCells;
	}

	/* -------------------- */
	/* Food-related Methods */
	/* -------------------- */
	/**
	 * @return List of food cells
	 */
	public BoardCell[] getAllFood() { return this.foodCells.toArray(new BoardCell[this.foodCells.size()]); }

	/**
	 * @return Is there zero food?
	 */
	public boolean noFood() {
		return this.foodCells.isEmpty();
	}

	/**
	 * Adds food to an open spot.
	 */
	public void addFood() {
		// Pick a random cell.
		int row    = (int) ((this.getNumRows()-1) * Math.random()) + 1;
		int column = (int) ((this.getNumColumns()-1) * Math.random()) + 1;
		BoardCell cell = this.getCell(row, column);

		double currentFreeSpots = this.freeSpots - this.snakeCells.size() - this.foodCells.size();
		double ratioFood = this.foodCells.size() / currentFreeSpots;

		if (ratioFood < 0.3) {
			if (cell.isOpen()) {
				cell.becomeFood();
				foodCells.addLast(cell);
			}
			else {
				addFood();
			}
		}
	}

	/** 
	 * Deletes the oldest piece of un-eaten food.
	 */
	public void removeFood(BoardCell foodCell) {
		this.foodCells.remove(foodCell);
	}

	/* ---------------------------------- */
	/* Snake Linked List movement methods */
	/* ---------------------------------- */

	public void addHead(BoardCell headCell) {
		snakeCells.addLast(headCell);
		headCell.becomeHead();
	}

	public void removeHead() {
		this.getSnakeHead().becomeOpen();
		snakeCells.removeLast();
	}

	public void removeTail() {
		this.getSnakeTail().becomeOpen();
		snakeCells.removeFirst();
	}

	public void reverseBody() {
		LinkedList<BoardCell> tempSnakeCells = new LinkedList<BoardCell>();
		for (BoardCell snakeCell : snakeCells) {
			tempSnakeCells.addFirst(snakeCell);
		}
		snakeCells = (LinkedList<BoardCell>) tempSnakeCells.clone();
	}

	public void addHeadBasedOnDirection() {
		if (this.snakeCells.size() != 1) {
			int headX = this.getSnakeHead().getColumn();
			int headY = this.getSnakeHead().getRow();
			int neckX = this.getSnakeNeck().getColumn();
			int neckY = this.getSnakeNeck().getRow();
			if (headX == neckX) {
				if (headY > neckY) {
					this.addHead(this.getSouthNeighbor(this.getSnakeHead()));
					this.setDirectionSouth();
					this.goingSouth();
				} else {
					this.addHead(this.getNorthNeighbor(this.getSnakeHead()));
					this.setDirectionNorth();
					this.goingNorth();
				}
			} else {
				if (headX > neckX) {
					this.addHead(this.getEastNeighbor(this.getSnakeHead()));
					this.setDirectionEast();
					this.goingEast();
				} else {
					this.addHead(this.getWestNeighbor(this.getSnakeHead()));
					this.setDirectionWest();
					this.goingWest();
				}
			}
		} else {
			switch(this.currentMode) {
				case GOING_NORTH:
					this.addHead(this.getSouthNeighbor(this.getSnakeHead()));
					this.setDirectionSouth();
					this.goingSouth();
					break;
				case GOING_SOUTH:
					this.addHead(this.getNorthNeighbor(this.getSnakeHead()));
					this.setDirectionNorth();
					this.goingNorth();
					break;
				case GOING_EAST:
					this.addHead(this.getWestNeighbor(this.getSnakeHead()));
					this.setDirectionWest();
					this.goingWest();
					break;
				case GOING_WEST:
					this.addHead(this.getEastNeighbor(this.getSnakeHead()));
					this.setDirectionEast();
					this.goingEast();
					break;
			}
		}
	}
	
	/* -------------------------------------- */
	/* Methods to support movement without AI */
	/* -------------------------------------- */
	/**
	 * @return the cell to the north of the given cell, which must not be on the boundary.
	 */
	public BoardCell getNorthNeighbor(BoardCell cell) {
		if (cell.getRow() == 0) {
			return null;
		}
		return this.getCell(cell.getRow() - 1, cell.getColumn());
	}

	/**
	 * @return the cell to the south of the given cell, which must not be on the boundary.
	 */	
	public BoardCell getSouthNeighbor(BoardCell cell) {
		if (cell.getRow() == this.getNumRows() - 1) {
			return null;
		}
		return this.getCell(cell.getRow() + 1, cell.getColumn());
	}

	/**
	 * @return the cell to the east of the given cell, which must not be on the boundary.
	 */	
	public BoardCell getEastNeighbor(BoardCell cell) {
		if (cell.getColumn() == this.getNumColumns() - 1) {
			return null;
		}
		return this.getCell(cell.getRow(), cell.getColumn() + 1);
	}

	/** 
	 * @return the cell to the west of the given cell, which must not be on the boundary.
	 */
	public BoardCell getWestNeighbor(BoardCell cell) {
		if (cell.getColumn() == 0) {
			return null;
		}
		return this.getCell(cell.getRow(), cell.getColumn() - 1);
	}
	
	/** 
	 * @return the cell to the north of the snake's head
	 */	
	public BoardCell getNorthNeighbor() {
		return this.getNorthNeighbor(this.getSnakeHead());
	}

	/** 
	 * @return the cell to the south of the snake's head
	 */	
	public BoardCell getSouthNeighbor() {
		return this.getSouthNeighbor(this.getSnakeHead());
	}

	/** 
	 * @return the cell to the east of the snake's head
	 */	
	public BoardCell getEastNeighbor() {
		return this.getEastNeighbor(this.getSnakeHead());
	}

	/** 
	 * @return the cell to the west of the snake's head
	 */	
	public BoardCell getWestNeighbor() {
		return this.getWestNeighbor(this.getSnakeHead());
	}

	/**
	 * @return a cell North, South, East or West of the snake head
	 *         based upon the current direction of travel
	 */ 
	public BoardCell getNextCellInDir() {
		switch(this.nextMode) {
			case GOING_NORTH:
				this.goingNorth();
				return getNorthNeighbor();
			case GOING_SOUTH:
				this.goingSouth();
				return getSouthNeighbor();
			case GOING_EAST:
				this.goingEast();
				return getEastNeighbor();
			case GOING_WEST:
				this.goingWest();
				return getWestNeighbor();
			default:
				return null;
		}
	}

	/* -------------------------------------------------- */
	/* Public methods to get all or one (random) neighbor */
	/* -------------------------------------------------- */
	/**
	 * @return an array of the four neighbors of the given cell
	 */
	public BoardCell[] getNeighbors(BoardCell center) {
		BoardCell[] neighborsArray = { getNorthNeighbor(center),
				                      getSouthNeighbor(center), 
				                      getEastNeighbor(center),
				                      getWestNeighbor(center) };
		return neighborsArray;
	}

	/** 
	 * @return an open neighbor of the given cell (or some other neighbor if there are no open neighbors)
	 */
	public BoardCell getRandomNeighboringCell(BoardCell start) {
		BoardCell[] neighborsArray = getNeighbors(start);
		for (BoardCell mc : neighborsArray) {
			if (mc.isOpen()) {
				return mc;
			}
		}
		// If we didn't find an open space, just return the first neighbor.
		return neighborsArray[0];
	}

	/* ------------------------------------------ */
    /* Methods to set the snake's (movement) mode */
	/* ------------------------------------------ */
	/**
	 * Makes the snake head north.
	 */
	public void setDirectionNorth() {
		this.nextMode = SnakeMode.GOING_NORTH;
	}

	/**
	 * Makes the snake head south.
	 */
	public void setDirectionSouth() {
		this.nextMode = SnakeMode.GOING_SOUTH;
	}

	/**
	 * Makes the snake head east.
	 */
	public void setDirectionEast() {
		this.nextMode = SnakeMode.GOING_EAST;
	}

	/**
	 * Makes the snake head west.
	 */
	public void setDirectionWest() {
		this.nextMode = SnakeMode.GOING_WEST;
	}

	/**
	 * Makes the snake switch to AI mode.
	 */
	public void setMode_AI() {
		this.nextMode = SnakeMode.AI_MODE;
	}

	/**
	 * Picks an initial movement mode for the snake.
	 */
	public void setStartDirection() {
		this.setDirectionEast();
	}

	/**
	 * Makes the snake head north.
	 */
	public void goingNorth() {
		this.currentMode = SnakeMode.GOING_NORTH;
	}

	/**
	 * Makes the snake head south.
	 */
	public void goingSouth() {
		this.currentMode = SnakeMode.GOING_SOUTH;
	}

	/**
	 * Makes the snake head east.
	 */
	public void goingEast() {
		this.currentMode = SnakeMode.GOING_EAST;
	}

	/**
	 * Makes the snake head west.
	 */
	public void goingWest() {
		this.currentMode = SnakeMode.GOING_WEST;
	}

	/* -------------------- */
	/* snake Access Methods */
	/* -------------------- */
	/**
	 * @return the cell containing the snake's head
	 */
	public BoardCell getSnakeHead() {
		return this.snakeCells.peekLast();
	}

	/**
	 * @return the cell containing the snake's tail
	 */
	public BoardCell getSnakeTail() {
		return this.snakeCells.peekFirst();
	}
	
	/**
	 * @return the snake body cell adjacent to the head
	 */
	public BoardCell getSnakeNeck() {
		int lastSnakeCellIndex = this.snakeCells.size() - 1;
		return this.snakeCells.get(lastSnakeCellIndex - 1);
	}

	/* ------------------------------------- */
	/* Methods to reset the model for search */
	/* ------------------------------------- */
	/**
	 * Clears the search-related fields in all the cells,
	 * in preparation for a new search.
	 */
	public void resetCellsForNextSearch() {
		for (BoardCell[] row : this.boardCells) {
			for (BoardCell cell : row) {
				cell.clear_RestartSearch();
			}
		}
	}

	/* ----------- */
	/* Game Status */
	/* ----------- */
	/** 
	 * Sets the game-over flag.
	 */
	public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }

	/** 
	 * @return game-over flag
	 */
	public boolean getGameOver() {
		return this.gameOver;
	}

	/**
	 * Sets the pause flag.
	 */
	public void setPaused(boolean paused) { this.paused = paused; }

	/**
	 * @return pause flag
	 */
	public boolean getPaused() {
		return this.paused;
	}

	/* ----------------------------------------------------- */
	/* Testing Infrastructure - You don't need these methods */
	/* ----------------------------------------------------- */
	// Constructor used exclusively for testing!
	public SnakeProData(TestGame gameNum) {
		// Want pictures of the test boards?
		// http://tinyurl.com/snakeProTestBoards
		this.boardCells = new BoardCell[6][6];
		this.addWalls();
		this.fillRemainingCells();
		if (gameNum.snakeAtStart()) {
			this.testing_snakeAtStartLocation(gameNum);
			this.setDirectionEast();
		} else {
			this.testing_snakeNotAtStartLocation(gameNum);
		}

	}

	private void testing_snakeAtStartLocation(TestGame gameNum) {
		this.placeSnakeAtStartLocation();
		if (gameNum == TestGame.G1) {
			this.getCell(1, 3).becomeFood();
		} else if (gameNum == TestGame.G2) {
			this.getCell(2, 2).becomeFood();
		} else if (gameNum == TestGame.G3) {
			this.getCell(1, 4).becomeFood();
		} else if (gameNum == TestGame.G4) {
			this.getCell(2, 1).becomeFood();
		} else if (gameNum == TestGame.G5) {
			this.getCell(4, 1).becomeFood();
		} else if (gameNum == TestGame.G6) {
			this.getCell(1, 3).becomeFood();
			this.getCell(3, 1).becomeFood();
		} else if (gameNum == TestGame.G7) {
			this.getCell(2, 2).becomeFood();
			this.getCell(1, 4).becomeFood();
		} else if (gameNum == TestGame.G8) {
			this.getCell(1, 4).becomeFood();
			this.getCell(4, 2).becomeFood();
		} else if (gameNum == TestGame.G9) {
			this.getCell(2, 1).becomeFood();
			this.getCell(2, 4).becomeFood();
		} else if (gameNum == TestGame.G10) {
			this.getCell(4, 1).becomeFood();
			this.getCell(4, 4).becomeFood();
		} else if (gameNum == TestGame.G11) {
			// No food :)
		}
		// Add all food to the food cells
		int height = this.getNumRows();
		int width = this.getNumColumns();
		for (int row = 0; row < height; row++) {
			for (int column = 0; column < width; column++) {
				BoardCell cell = this.getCell(row, column);
				if (cell.isFood()) {
					this.foodCells.add(cell);
				}
			}
		}
	}

	private void testing_snakeNotAtStartLocation(TestGame gameNum) {
		if (gameNum == TestGame.G12) {
			BoardCell body2 = this.getCell(2, 3);
			BoardCell body1 = this.getCell(2, 2);
			BoardCell head = this.getCell(2, 1);
			this.snakeCells.add(body2);
			this.snakeCells.add(body1);
			this.snakeCells.add(head);
			head.becomeHead();
			body2.becomeBody();
			body1.becomeBody();
		} else if (gameNum == TestGame.G13) {
			BoardCell body2 = this.getCell(3, 2);
			BoardCell body1 = this.getCell(2, 2);
			BoardCell head = this.getCell(2, 1);
			this.snakeCells.add(body2);
			this.snakeCells.add(body1);
			this.snakeCells.add(head);
			head.becomeHead();
			body2.becomeBody();
			body1.becomeBody();
		} else if (gameNum == TestGame.G14) {
			BoardCell body2 = this.getCell(2, 2);
			BoardCell body1 = this.getCell(3, 2);
			BoardCell head = this.getCell(3, 1);
			this.snakeCells.add(body2);
			this.snakeCells.add(body1);
			this.snakeCells.add(head);
			head.becomeHead();
			body2.becomeBody();
			body1.becomeBody();
		} else if (gameNum == TestGame.G15) {
			BoardCell body2 = this.getCell(3, 2);
			BoardCell body1 = this.getCell(3, 3);
			BoardCell head = this.getCell(3, 4);
			this.snakeCells.add(body2);
			this.snakeCells.add(body1);
			this.snakeCells.add(head);
			head.becomeHead();
			body2.becomeBody();
			body1.becomeBody();
		}
	}

	public String toString() {
		String result = "";
		for (int r = 0; r < this.getNumRows(); r++) {
			for (int c = 0; c < this.getNumColumns(); c++) {
				BoardCell cell = this.getCell(r, c);
				result += cell.toStringType();
			}
			result += "\n";
		}
		return result;
	}
	
	public String toStringParents() {
		String result = "";
		for (int r = 0; r < this.getNumRows(); r++) {
			for (int c = 0; c < this.getNumColumns(); c++) {
				BoardCell cell = this.getCell(r, c);
				result += cell.toStringParent() + "\t";
			}
			result += "\n";
		}
		return result;
	}

}

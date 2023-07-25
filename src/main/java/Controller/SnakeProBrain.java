package Controller;

import Model.*;
import View.SnakeProDisplay;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.*;
import java.lang.*;

public class SnakeProBrain extends SnakeProBrainParent
{

	/** The "View" in MVC */
	private SnakeProDisplay theDisplay;
	
	/** The "Model" in MVC */
	private SnakeProData theData;
	
	/** Number of animated frames displayed so far */
	private int cycleNum = 0;

	// Mapping between direction (names) and keys
	private static final int REVERSE = KeyEvent.VK_R;
	private static final int UP      = KeyEvent.VK_UP;
	private static final int DOWN    = KeyEvent.VK_DOWN;
	private static final int LEFT    = KeyEvent.VK_LEFT;
	private static final int RIGHT   = KeyEvent.VK_RIGHT;
	private static final int AI_MODE = KeyEvent.VK_A;
	private static final int PAUSE1  = KeyEvent.VK_P;
	private static final int PAUSE2  = KeyEvent.VK_ESCAPE;

	/**
	 * Starts a new game.
	 */
	public void startFirstGame() {
		this.theData = new SnakeProData();
		this.theData.placeSnakeAtStartLocation();
		this.theData.setStartDirection();
		
		this.theDisplay = new SnakeProDisplay(this.theData, this.screen, this.centralPanel,
				this.getSize().width, getSize().height);
		this.theDisplay.updateGraphics();

		// Wait a fraction of a second (200 ms), by which time the
		//    picture should have been fetched from disk, and redraw.
		try { Thread.sleep(200); } catch (InterruptedException e) {};
		this.theDisplay.updateGraphics();
		this.cycleNum = 0;
	}

	/**
	 * Create a new game (not First)
	 */
	public void startNewGame() {
		this.centralPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		this.theData.getPlayAgain().setActive(false);
		for (MenuButton menuButton : this.theData.getMenuButtons()) {
			menuButton.setActive(false);
		}
		theData.resetData();
		theData.setPaused(false);
		theData.setGameOver(false);

		this.theData.placeSnakeAtStartLocation();
		this.theData.setStartDirection();
		this.cycleNum = 0;
	}

	/**
	 * Declares the game is paused
	 */
	public void paused() {
		this.theData.setPaused(true);

		for (MenuButton menuButton : this.theData.getMenuButtons()) {
			menuButton.setActive(true);
		}
	}

	/**
	 * Declares the game is unpaused
	 */
	public void unpaused() {
		this.centralPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		this.theData.setPaused(false);

		for (MenuButton menuButton : this.theData.getMenuButtons()) {
			menuButton.setActive(false);
		}
	}

	/**
	 * Declares the game over.
	 */
	public void gameOver() {
		this.theData.getPlayAgain().setActive(true);
		this.theData.setGameOver(true); // tell the model that the game is over
	}
	
	/* -------- */
	/* Gameplay */
	/* -------- */
	
	/**
	 * Moves the game forward one step (i.e., one frame of animation,
	 * which occurs every Model.Preferences.SLEEP_TIME milliseconds)
	 */
	public void cycle() {
		if (!this.theData.getPaused() && !this.theData.getGameOver()) {
			// move the snake
			this.updateSnake();

			// update the list of Food
			this.updateFood();

			// update the cycle counter
			this.cycleNum++;
		}

		// draw the board
		this.theDisplay.updateGraphics();

		// make the new display visible - sends the drawing to the screen
		this.repaint();
	}

    /** 
     * Reacts to characters typed by the user.
     */
	public void keyPressed(KeyEvent evt) {
		switch (evt.getKeyCode()) {
			case UP:
				if ((this.theData.getCurrentMode() != SnakeMode.GOING_SOUTH)
						&& (!this.theData.getPaused()) && (!this.theData.getGameOver())) {
					this.theData.setDirectionNorth();
				}
				break;
			case DOWN:
				if ((this.theData.getCurrentMode() != SnakeMode.GOING_NORTH)
						&& (!this.theData.getPaused()) && (!this.theData.getGameOver())) {
					this.theData.setDirectionSouth();
				}
				break;
			case LEFT:
				if ((this.theData.getCurrentMode() != SnakeMode.GOING_EAST)
						&& (!this.theData.getPaused()) && (!this.theData.getGameOver())) {
					this.theData.setDirectionWest();
				}
				break;
			case RIGHT:
				if ((this.theData.getCurrentMode() != SnakeMode.GOING_WEST)
						&& (!this.theData.getPaused()) && (!this.theData.getGameOver())) {
					this.theData.setDirectionEast();
				}
				break;
			case REVERSE:
				if ((!this.theData.getPaused()) && (!this.theData.getGameOver())) {
					this.reverseSnake();
				}
				break;
			case AI_MODE:
				if ((!this.theData.getPaused()) && (!this.theData.getGameOver())) {
					this.theData.setMode_AI();
				}
				break;
			case PAUSE1:
			case PAUSE2:
				if (!this.theData.getGameOver()) {
					if (this.theData.getPaused()) {
						this.unpaused();
					} else {
						this.paused();
					}
				}
				break;
		}
	}

	public void mouseClicked(MouseEvent evt) {
		MenuButton menuButton = this.theData.getMenuButtons().get(0);
		if (menuButton.inBoundingBox(this.centralPanel) && menuButton.getActive()) {
			this.unpaused();
		}
		menuButton = this.theData.getMenuButtons().get(1);
		if (menuButton.inBoundingBox(this.centralPanel) && menuButton.getActive()) {
			this.startNewGame();
		}
		menuButton = this.theData.getMenuButtons().get(2);
		if (menuButton.inBoundingBox(this.centralPanel) && menuButton.getActive()) {
			this.theData.setAIReverse(!this.theData.getAIReverse());
		}
		menuButton = this.theData.getPlayAgain();
		if (menuButton.inBoundingBox(this.centralPanel) && menuButton.getActive()) {
			this.startNewGame();
		}
	}

	/**
	 *  Moves the snake forward once every REFRESH_RATE cycles,
	 *  either in the current direction, or as directed by the AI.
	 */
	public void updateSnake() {
		if (this.cycleNum % Preferences.REFRESH_RATE == 0) {
			BoardCell nextCell;
			if (this.theData.inAImode()) {
				nextCell = this.getNextCellAStar();
			} else {
				nextCell = this.theData.getNextCellInDir();
			}
			if (nextCell != null) {
				this.advanceTheSnake(nextCell);
			}
		}
	}

	/**
	 * Move the snake into the next cell (and possibly eat food)
	 * 
	 * @param nextCell  New location of the snake head (which
	 *                  must be horizontally or vertically adjacent
	 *                  to the old location of the snake head).
	 */
	public void advanceTheSnake(BoardCell nextCell) {
		if (nextCell.isWall() || nextCell.isBody()) {
			this.gameOver();
		} else if (nextCell.isFood()) {
			this.theData.addHead(nextCell);
			this.theData.getSnakeNeck().becomeBody();
			this.theData.removeFood(nextCell);
		} else {
			this.theData.addHead(nextCell);
			this.theData.removeTail();
			this.theData.getSnakeNeck().becomeBody();
		}
	}


	/** 
	 * Every FOOD_ADD_RATE cycles, tries to add one new food.
	 */
	public void updateFood() {
		if (this.theData.noFood()) {
			this.theData.addFood();
		} else if (this.cycleNum % Preferences.FOOD_ADD_RATE == 0) {
			this.theData.addFood();
		}
	}


	/**
	 * Uses AI to search for the next cell to move to
	 * @return Where to move the snake head
	 */
	public BoardCell getNextCellAStar() {
		// Initialize the search.
		theData.resetCellsForNextSearch();

		// Create search List
		ArrayList<BoardCell> cellsToSearch = new ArrayList<BoardCell>();
		BoardCell snakeHead = theData.getSnakeHead();
		snakeHead.setAddedToSearchList();
		snakeHead.setParent(null);
		snakeHead.setPathScore(0);
		snakeHead.setSearchScore(this.getHeuristicScore(snakeHead)+snakeHead.getPathScore());
		cellsToSearch.add(snakeHead);

		BoardCell reverseHead;
		if (this.theData.getAIReverse()) {
			int TailX = this.theData.getSnakeTail().getColumn();
			int TailY = this.theData.getSnakeTail().getRow();
			int TailNeckX = this.theData.getSnakeCells().get(1).getColumn();
			int TailNeckY = this.theData.getSnakeCells().get(1).getRow();
			if (TailX == TailNeckX) {
				if (TailY > TailNeckY) {
					reverseHead = this.theData.getSouthNeighbor(this.theData.getSnakeTail());
				} else {
					reverseHead = this.theData.getNorthNeighbor(this.theData.getSnakeTail());
				}
			} else {
				if (TailX > TailNeckX) {
					reverseHead = this.theData.getEastNeighbor(this.theData.getSnakeTail());
				} else {
					reverseHead = this.theData.getWestNeighbor(this.theData.getSnakeTail());
				}
			}
			if ((!reverseHead.isBody()) && (!reverseHead.isWall())) {
				this.theData.getSnakeTail().setAddedToSearchList();
				this.theData.getSnakeTail().setParent(null);
				reverseHead.setAddedToSearchList();
				reverseHead.setParent(this.theData.getSnakeTail());
				reverseHead.setPathScore(1);
				reverseHead.setSearchScore(this.getHeuristicScore(reverseHead)+reverseHead.getPathScore());
				cellsToSearch.add(reverseHead);
			}
		}

		// Search Iteration
		while (true) {
			// No possible paths
			if (cellsToSearch.size() == 0) {
				return theData.getRandomNeighboringCell(snakeHead);
			}
			// Find boardCell with least search score
			BoardCell searchCell = cellsToSearch.get(0);
			int minimumSearchScore = searchCell.getSearchScore();
			for (BoardCell cellToSearch : cellsToSearch) {
				if (cellToSearch.getSearchScore() < minimumSearchScore) {
					searchCell = cellToSearch;
					minimumSearchScore = cellToSearch.getSearchScore();
				}
				// Prioritise boardCell with a higher path score
				if (cellToSearch.getSearchScore() == minimumSearchScore) {
					if (cellToSearch.getPathScore() > searchCell.getPathScore()) {
						searchCell = cellToSearch;
					}
				}
			}

			// Remove target boardCell from cellsToSearch
			cellsToSearch.remove(searchCell);

			// Check if searchCell is a FoodCell
			if (searchCell.isFood()) {
				return this.getFirstCellInPath(searchCell);
			}

			// Expand searchCell and make sure SearchCell is valid
			for (BoardCell neighbourCell : theData.getNeighbors(searchCell)) {
				// if neighbourCell is not a wall, body or already searched
				if ((!neighbourCell.isWall()) && (!neighbourCell.isBody())) {
					if (!neighbourCell.inSearchListAlready()) {
						// Calculate Heuristic Score for neighbourCells and add to searchList
						neighbourCell.setAddedToSearchList();
						neighbourCell.setParent(searchCell);
						neighbourCell.setPathScore(searchCell.getPathScore() + 1);
						int searchScore = this.getHeuristicScore(neighbourCell) + neighbourCell.getPathScore();
						neighbourCell.setSearchScore(searchScore);
						cellsToSearch.add(neighbourCell);
					}
				}
			}
		}
	}

	/**
	 * Return Heuristic Score of a given boardCell
	 * @param target the target boardCell of the function
	 * @return Heuristic Score of the boardCell
	 */
	private int getHeuristicScore(BoardCell target) {
		// minimumScore starts off with the largest score possible,
		// which is the number of boardCells in the game
		int minimumScore = Preferences.NUM_CELLS_TALL + Preferences.NUM_CELLS_TALL;

		// Loop through all the foodCell
		for (BoardCell foodCell : theData.getAllFood()) {
			// Find manhattan distance from boardCell to foodCell
			int heightDiff = Math.abs(target.getRow()-foodCell.getRow());
			int lengthDiff = Math.abs(target.getColumn()-foodCell.getColumn());
			// See if distance calculated above is smaller than the minimum
			if ((heightDiff + lengthDiff) < minimumScore) {
				minimumScore = heightDiff + lengthDiff;
			}
		}
		return minimumScore;
	}

	/**
	 * Use Recursion to Find the starting node from the A* search
	 * @param start Starting node to search backwards
	 * @return Next cell in path for snake to move
	 */
	private BoardCell getFirstCellInPath(BoardCell start) {
		BoardCell snakeHead = theData.getSnakeHead();
		BoardCell snakeNeck = theData.getSnakeNeck();
		BoardCell snakeTail = theData.getSnakeTail();
		BoardCell previous = start.getParent();
		if ((previous.getRow()==snakeHead.getRow()) && (previous.getColumn()==snakeHead.getColumn())) {
			if (snakeHead.getColumn() == snakeNeck.getColumn()) {
				if (snakeHead.getRow() > snakeNeck.getRow()) {
					this.theData.goingSouth();
				} else {
					this.theData.goingNorth();
				}
			} else {
				if (snakeHead.getColumn() > snakeNeck.getColumn()) {
					this.theData.goingEast();
				} else {
					this.theData.goingWest();
				}
			}
			return start;
		}
		if (this.theData.getAIReverse()) {
			if ((previous.getRow()==snakeTail.getRow()) && (previous.getColumn()==snakeTail.getColumn())) {
				this.reverseSnake();
				this.theData.setMode_AI();
				return null;
			}
		}

		return this.getFirstCellInPath(previous);
	}

	/**
	 * Reverses the snake back-to-front and updates the movement 
	 * mode appropriately.
	 */
	public void reverseSnake() {
		this.theData.removeHead();
		this.theData.reverseBody();
		this.theData.addHeadBasedOnDirection();
	}

	// not used - a variable added to remove a Java warning:
	private static final long serialVersionUID = 1L;

	
	/* ---------------------- */
	/* Testing Infrastructure */
	/* ---------------------- */

	public static SnakeProBrain getTestGame(TestGame gameNum) {
		SnakeProBrain brain = new SnakeProBrain();
		brain.theData = new SnakeProData(gameNum);
		return brain;
	}
	
	public String testing_toStringParent() {
		return this.theData.toStringParents();
	}

	public BoardCell testing_getNextCellInDir() {
		return this.theData.getNextCellInDir();
	}

	public String testing_toStringSnakeProData() {
		return this.theData.toString();
	}
}

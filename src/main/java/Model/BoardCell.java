package Model;

import java.awt.Color;

public class BoardCell {

	/** the row of this cell within the Board ( >= 0 ) */
	private int row; 
	
	/** the column of this cell within the Board ( >= 0 ) */
	private int column;
	
	/** the current contents of this cell */
	private CellType myCellType;
	
	/** Has this cell been added to the search queue yet? */
	private boolean addedToSearchList = false;

	/** Length of current path */
	private int pathScore = 0;

	/** Heuristic Cost + Length of current path */
	private int searchScore = 0;
	
	/** Where did we came from, when search first reached this Model.BoardCell? */
	private BoardCell parent = null;

	/**
	 * Constructor.
	 * @param inputRow     the row of this cell
	 * @param inputColumn  the column of this cell
	 * @param type         the initial contents of this cell
	 */
	public BoardCell (int inputRow, int inputColumn, CellType type){
		this.row = inputRow; 
		this.column = inputColumn; 
		this.myCellType = type;
	}

	/* ------------------------------------- */
	/* Access basic information about a cell */
    /* ------------------------------------- */
	/** @return the row of this Model.BoardCell */
	public int getRow() {
		return this.row;
	}
	
	/** @return the column of this Model.BoardCell */
	public int getColumn() {
		return this.column;
	}

	/** @return the X position of the BoardCell */
	public int getX() {
		return this.column * Preferences.CELL_SIZE;
	}

	/** @return the Y position of the BoardCell */
	public int getY() {
		return this.row * Preferences.CELL_SIZE + Preferences.SPACE_FOR_TITLE;
	}

	/** @return Is this cell a wall? */
	public boolean isWall() {
		return this.myCellType == CellType.WALL;
	}
	
	/** @return Is this cell open (not a wall or a snake body part)? */
	public boolean isOpen() {
		return this.myCellType == CellType.OPEN || this.isFood();
	}
	
	/** @return Does this cell contain food? */
	public boolean isFood() {
		return this.myCellType == CellType.FOOD;
	}
	
	/** @return Does this cell contain part of the snake (not the head)? */
	public boolean isBody() {
		return this.myCellType == CellType.BODY;
	}

	/** @return Does this cell contain the head of the snake? */
	public boolean isHead() {
		return this.myCellType == CellType.HEAD;
	}
	
	/* ------------------------------ */
	/* Modify basic info about a cell */
	/* ------------------------------ */
	/** Marks this Model.BoardCell as food. */
	public void becomeFood() {
		this.myCellType = CellType.FOOD;
	}

	/** Marks this Model.BoardCell as open */
	public void becomeOpen() {
		this.myCellType = CellType.OPEN;
	}
	
	/** Marks this Model.BoardCell as the snake's head */
	public void becomeHead() {
		this.myCellType = CellType.HEAD;
	}

	/** Marks this Model.BoardCell as part of the snake's body */
	public void becomeBody() {
		this.myCellType = CellType.BODY;
	}

	/* ------------------------------------------ */
	/* Methods used to access and set search info */
	/* ------------------------------------------ */
	/** Marks this cell as having been added to our search queue */
	public void setAddedToSearchList() {
		this.addedToSearchList = true;
	}

	/** @return Has this cell been added to our search queue yet? */
	public boolean inSearchListAlready() {
		return this.addedToSearchList;
	}

	/** Set the parent of this cell */
	public void setParent(BoardCell p) { this.parent = p; }

	/** @return the parent of this cell */
	public BoardCell getParent() {
		return this.parent;
	}

	/** Set the path score of the cell */
	public void setPathScore(int score) { this.pathScore = score; }

	/** @return Path score of the cell */
	public int getPathScore() { return this.pathScore; }

	/** Set the search score of the cell */
	public void setSearchScore(int score) { this.searchScore = score; }

	/** @return Search score of the cell */
	public int getSearchScore() { return this.searchScore; }

	/** Clear the search-related info for this cell (to allow a new search) */
	public void clear_RestartSearch() {
		this.addedToSearchList = false;
		this.parent = null;
		this.pathScore = 0;
		this.searchScore = 0;
	}
	
	/* ---------------------------- */
	/* Helper functions for testing */
    /* ---------------------------- */
	/** @return the cell as a string. */
	public String toString() { return "[" + this.row + ", " + this.column + ", " + this.toStringType() + "]"; }
	
	/** @return the contents of the cell, as a single character. */
	public String toStringType() {
		return this.myCellType.getDisplayChar();
	}
	
	/** @return the parent of a cell, as a string */
	public String toStringParent(){
		if (this.parent == null){
			return "[null]";
		}
		else {
			return this.parent.toString();
		}
	}

	
}

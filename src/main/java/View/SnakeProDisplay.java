package View;

import Model.BoardCell;
import Model.MenuButton;
import Model.SnakeProData;
import Model.Preferences;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.io.File;
import java.net.URL;


public class SnakeProDisplay {

	/** reference to the board data */
	private SnakeProData theData;
	
	/** reference to the display screen */
	private Graphics theScreen;

	/** reference to the cenrtal JPanel */
	private JPanel thePanel;
	
	/** width of the display in pixels */
	private int width;
	
	/** height of the display in pixels */
	private int height;
	
	/** font used for most text */
	private Font font;

	/**
	 * Constructor
	 * @param theBoardInput   board data
	 * @param theScreenInput  display screen
	 * @param widthInput      width of display (in pixels)
	 * @param heightInput     height of display (in pixels)
	 */
	public SnakeProDisplay(SnakeProData theBoardInput, Graphics theScreenInput, JPanel thePanel, int widthInput, int heightInput) {
		this.theScreen = theScreenInput;
		this.theData   = theBoardInput;
		this.thePanel  = thePanel;
		this.height    = heightInput;
		this.width     = widthInput;

		ClassLoader classLoader = getClass().getClassLoader();
		File fontFile;

		// Retrieve font.ttf resource from resources folder
		URL resource = classLoader.getResource("font.ttf");
		// Error Handling for if "font.ttf" does not exist
		if (resource == null) {
			throw new IllegalArgumentException("file is not found!");
		} else {
			// Retrieve font.ttf file from the resource
			fontFile = new File(resource.getFile());
		}

		try {
			// Create a new font with font.ttf
			this.font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
			// Register the font to the graphics environment,
			// which allows us to use the font through Graphics.setFont()
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, fontFile));
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	/**
	 * Draws the board, food, and snake
	 */
	public void updateGraphics() {
		Graphics2D screen = (Graphics2D) this.theScreen;
		screen.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Draws background
		this.clear();
		
		// Draws title
		this.displayTitle();

		// Draws the board and wall
		for (int row = 0; row < Preferences.NUM_CELLS_TALL; row++) {
			for (int column = 0; column < Preferences.NUM_CELLS_WIDE; column++) {
				BoardCell cell = this.theData.getCell(row, column);
				Color cellColor;
				if (cell.isWall()) {
					cellColor = Preferences.COLOR_WALL;
				} else {
					cellColor = Preferences.COLOR_OPEN;
				}

				drawSquare(cell.getX(),cell.getY(), cellColor);
			}
		}

		// Draws the Food
		for (BoardCell foodCell : this.theData.getAllFood()) {
			this.theScreen.setColor(Preferences.COLOR_FOOD);
			screen.fill(new Ellipse2D.Double(
					foodCell.getX()+((1-Preferences.FOOD_SIZE_RATIO)/2*Preferences.CELL_SIZE),
					foodCell.getY()+((1-Preferences.FOOD_SIZE_RATIO)/2*Preferences.CELL_SIZE),
					Preferences.CELL_SIZE*Preferences.FOOD_SIZE_RATIO,
					Preferences.CELL_SIZE*Preferences.FOOD_SIZE_RATIO));
		}

		// Draws the snake
		this.drawSnake();

		// Draws Game Border
		this.theScreen.setColor(Preferences.COLOR_WALL);
		this.theScreen.drawRect(0, Preferences.SPACE_FOR_TITLE,
				Preferences.GAMEWIDTH-1, Preferences.GAMEHEIGHT-1);
		this.theScreen.drawRect(1, Preferences.SPACE_FOR_TITLE+1,
				Preferences.GAMEWIDTH-3, Preferences.GAMEHEIGHT-3);
		
		// Draw the game-over message, if appropriate.
 		if (this.theData.getGameOver()) {
			this.displayGameOver();
		}

 		// Draw pause screen
 		if (this.theData.getPaused()) {
			this.displayPaused();
		}
	}

	/**
	 * Draws a cell-sized square with its upper-left corner
	 * at the given pixel coordinates (i.e., x pixels to the right and 
	 * y pixels below the upper-left corner) on the display.
	 * 
	 * @param x  x-coordinate of the square, between 0 and this.width-1 inclusive
	 * @param y  y-coordinate of the square, between 0 and this.width-1 inclusive
	 * @param cellColor  color of the square being drawn
	 */
	private void drawSquare(int x, int y, Color cellColor) {
		this.theScreen.setColor(cellColor);
		this.theScreen.fillRect(x, y, Preferences.CELL_SIZE,
				Preferences.CELL_SIZE);
	}

	/**
	 * Draws the snake
	 */
	private void drawSnake() {
		int cellSize = Preferences.CELL_SIZE;
		Graphics2D screen = (Graphics2D) this.theScreen;
		screen.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		LinkedList<BoardCell> SnakeCells = this.theData.getSnakeCells();
		BasicStroke s = new BasicStroke((float)(cellSize*Preferences.SNAKE_SIZE_RATIO),
				BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

		// Draw Snake Shadow (Using Path2D and stroked line)
		Path2D ShadowPath = new Path2D.Double();
		ShadowPath.moveTo(
				SnakeCells.get(0).getX()+cellSize/2+Preferences.SHADOW_OFFSET,
				SnakeCells.get(0).getY()+cellSize/2+Preferences.SHADOW_OFFSET);

		for (int i = 1; i < SnakeCells.size(); i++) {
			BoardCell currentCell = SnakeCells.get(i);
			int currentRow = currentCell.getRow();
			int currentCol = currentCell.getColumn();
			ShadowPath.lineTo(
					currentCell.getX()+cellSize/2+Preferences.SHADOW_OFFSET,
					currentCell.getY()+cellSize/2+Preferences.SHADOW_OFFSET);
		}
		screen.setPaint(Preferences.COLOR_SHADOW);
		screen.fill(s.createStrokedShape(ShadowPath));

		// Draw Snake Body (Using Path2D and stroked line)
		Path2D SnakePath = new Path2D.Double();
		SnakePath.moveTo(SnakeCells.get(0).getX()+cellSize/2, SnakeCells.get(0).getY()+cellSize/2);
		for (int i = 1; i < SnakeCells.size(); i++) {
			BoardCell currentCell = SnakeCells.get(i);
			int currentRow = currentCell.getRow();
			int currentCol = currentCell.getColumn();
			SnakePath.lineTo(currentCell.getX()+cellSize/2, currentCell.getY()+cellSize/2);
		}

		int startRed = Preferences.COLOR_SNAKE.getRed();
		int startGreen = Preferences.COLOR_SNAKE.getGreen();
		int startBlue = Preferences.COLOR_SNAKE.getBlue();
		int endRed = Preferences.COLOR_SNAKE_END.getRed();
		int endGreen = Preferences.COLOR_SNAKE_END.getGreen();
		int endBlue = Preferences.COLOR_SNAKE_END.getBlue();
		int tailRed = (int) (((startRed-endRed)*(1.0/SnakeCells.size())*1.5)+endRed);
		int tailGreen = (int) (((startGreen-endGreen)*(1.0/SnakeCells.size())*1.5)+endGreen);
		int tailBlue = (int) (((startBlue-endBlue)*(1.0/SnakeCells.size())*1.5)+endBlue);
		GradientPaint snakePaint = new GradientPaint(
				SnakeCells.getLast().getX()+Preferences.CELL_SIZE/2,
				SnakeCells.getLast().getY()+Preferences.CELL_SIZE/2,
				Preferences.COLOR_SNAKE,
				SnakeCells.getFirst().getX()+Preferences.CELL_SIZE/2,
				SnakeCells.getFirst().getY()+Preferences.CELL_SIZE/2,
				new Color(tailRed, tailGreen, tailBlue));
		screen.setPaint(snakePaint);
		screen.fill(s.createStrokedShape(SnakePath));

		// Draw Eyes
		Ellipse2D.Double leftEye = null;
		Ellipse2D.Double rightEye = null;
		int headX = this.theData.getSnakeHead().getX();
		int headY = this.theData.getSnakeHead().getY();
		int neckX = this.theData.getSnakeNeck().getX();
		int neckY = this.theData.getSnakeNeck().getY();
		if (headX == neckX) {
			if (headY > neckY) {
				// South
				leftEye = new Ellipse2D.Double(
						headX + Preferences.CELL_SIZE * ((1-Preferences.SNAKE_EYES_DIFF_RATIO)/2 - Preferences.SNAKE_EYES_SIZE_RATIO/2),
						headY + Preferences.CELL_SIZE * (Preferences.SNAKE_EYES_RATIO - Preferences.SNAKE_EYES_SIZE_RATIO/2),
						Preferences.CELL_SIZE * Preferences.SNAKE_EYES_SIZE_RATIO,
						Preferences.CELL_SIZE * Preferences.SNAKE_EYES_SIZE_RATIO);
				rightEye = new Ellipse2D.Double(
						headX + Preferences.CELL_SIZE * ((1-(1-Preferences.SNAKE_EYES_DIFF_RATIO)/2) - Preferences.SNAKE_EYES_SIZE_RATIO/2),
						headY + Preferences.CELL_SIZE * (Preferences.SNAKE_EYES_RATIO - Preferences.SNAKE_EYES_SIZE_RATIO/2),
						Preferences.CELL_SIZE * Preferences.SNAKE_EYES_SIZE_RATIO,
						Preferences.CELL_SIZE * Preferences.SNAKE_EYES_SIZE_RATIO);
			} else {
				// North
				leftEye = new Ellipse2D.Double(
						headX + Preferences.CELL_SIZE * ((1-Preferences.SNAKE_EYES_DIFF_RATIO)/2 - Preferences.SNAKE_EYES_SIZE_RATIO/2),
						headY + Preferences.CELL_SIZE * ((1-Preferences.SNAKE_EYES_RATIO) - Preferences.SNAKE_EYES_SIZE_RATIO/2),
						Preferences.CELL_SIZE * Preferences.SNAKE_EYES_SIZE_RATIO,
						Preferences.CELL_SIZE * Preferences.SNAKE_EYES_SIZE_RATIO);
				rightEye = new Ellipse2D.Double(
						headX + Preferences.CELL_SIZE * ((1-(1-Preferences.SNAKE_EYES_DIFF_RATIO)/2) - Preferences.SNAKE_EYES_SIZE_RATIO/2),
						headY + Preferences.CELL_SIZE * ((1-Preferences.SNAKE_EYES_RATIO) - Preferences.SNAKE_EYES_SIZE_RATIO/2),
						Preferences.CELL_SIZE * Preferences.SNAKE_EYES_SIZE_RATIO,
						Preferences.CELL_SIZE * Preferences.SNAKE_EYES_SIZE_RATIO);
			}
		} else {
			if (headX > neckX) {
				// East
				leftEye = new Ellipse2D.Double(
						headX + Preferences.CELL_SIZE * (Preferences.SNAKE_EYES_RATIO - Preferences.SNAKE_EYES_SIZE_RATIO/2),
						headY + Preferences.CELL_SIZE * ((1-Preferences.SNAKE_EYES_DIFF_RATIO)/2 - Preferences.SNAKE_EYES_SIZE_RATIO/2),
						Preferences.CELL_SIZE*Preferences.SNAKE_EYES_SIZE_RATIO,
						Preferences.CELL_SIZE*Preferences.SNAKE_EYES_SIZE_RATIO);
				rightEye = new Ellipse2D.Double(
						headX + Preferences.CELL_SIZE * (Preferences.SNAKE_EYES_RATIO - Preferences.SNAKE_EYES_SIZE_RATIO/2),
						headY + Preferences.CELL_SIZE * ((1-(1-Preferences.SNAKE_EYES_DIFF_RATIO)/2) - Preferences.SNAKE_EYES_SIZE_RATIO/2),
						Preferences.CELL_SIZE * Preferences.SNAKE_EYES_SIZE_RATIO,
						Preferences.CELL_SIZE * Preferences.SNAKE_EYES_SIZE_RATIO);
			} else {
				// West
				leftEye = new Ellipse2D.Double(
						headX + Preferences.CELL_SIZE * ((1-Preferences.SNAKE_EYES_RATIO) - Preferences.SNAKE_EYES_SIZE_RATIO/2),
						headY + Preferences.CELL_SIZE * ((1-Preferences.SNAKE_EYES_DIFF_RATIO)/2 - Preferences.SNAKE_EYES_SIZE_RATIO/2),
						Preferences.CELL_SIZE*Preferences.SNAKE_EYES_SIZE_RATIO,
						Preferences.CELL_SIZE*Preferences.SNAKE_EYES_SIZE_RATIO);
				rightEye = new Ellipse2D.Double(
						headX + Preferences.CELL_SIZE * ((1-Preferences.SNAKE_EYES_RATIO) - Preferences.SNAKE_EYES_SIZE_RATIO/2),
						headY + Preferences.CELL_SIZE * ((1-(1-Preferences.SNAKE_EYES_DIFF_RATIO)/2) - Preferences.SNAKE_EYES_SIZE_RATIO/2),
						Preferences.CELL_SIZE * Preferences.SNAKE_EYES_SIZE_RATIO,
						Preferences.CELL_SIZE * Preferences.SNAKE_EYES_SIZE_RATIO);
			}
		}
		screen.setPaint(Preferences.COLOR_SNAKE_EYES);
		screen.fill(leftEye);
		screen.fill(rightEye);
	}

	/**
	 * Draws the background
	 */
	private void clear() {
		this.theScreen.setColor(Preferences.COLOR_BACKGROUND);
		this.theScreen.fillRect(0, 0, Preferences.GAMEWIDTH, Preferences.GAMEBOARDHEIGHT);
	}
	
	/**
	 * Displays the title of the game.
	 */
	private void displayTitle() {
		Graphics2D screen = (Graphics2D) this.theScreen;

		Font titleFont = this.font.deriveFont(60f);
		this.theScreen.setFont(titleFont);
		FontMetrics fm = this.theScreen.getFontMetrics();
		this.theScreen.setColor(Preferences.COLOR_TEXT);
		this.theScreen.drawString(Preferences.TITLE,
				(Preferences.GAMEWIDTH - fm.stringWidth(Preferences.TITLE))/2,
				fm.getAscent()+(Preferences.SPACE_FOR_TITLE - (fm.getAscent() + fm.getDescent()))/2);

		// Draw Score
		screen.setPaint(Preferences.COLOR_FOOD);
		screen.fill(new Ellipse2D.Double(
				Preferences.SPACE_FOR_TITLE*0.2,Preferences.SPACE_FOR_TITLE*0.25,
				Preferences.SPACE_FOR_TITLE*0.6,Preferences.SPACE_FOR_TITLE*0.6));

		Font scoreFont = this.font.deriveFont(50f);
		this.theScreen.setFont(scoreFont);
		fm = this.theScreen.getFontMetrics();
		this.theScreen.setColor(Preferences.COLOR_TEXT);
		this.theScreen.drawString(""+this.theData.getSnakeCells().size(),
				Preferences.SPACE_FOR_TITLE,
				fm.getAscent()+(Preferences.SPACE_FOR_TITLE - (fm.getAscent() + fm.getDescent()))/2);
	}

	/**
	 * Displays the menu buttons.
	 */
	private void displayPaused() {
		Graphics2D screen = (Graphics2D) this.theScreen;

		Font titleFont = this.font.deriveFont(60f);
		this.theScreen.setFont(titleFont);

		screen.setPaint(Preferences.COLOR_PAUSE);
		screen.fill(new Rectangle2D.Double(0, 0,
				Preferences.GAMEWIDTH,
				Preferences.GAMEBOARDHEIGHT));

		FontMetrics fm = this.theScreen.getFontMetrics();
		boolean hovering = false;
		for (MenuButton menuButton : this.theData.getMenuButtons()) {
			String ButtonText = menuButton.getText();
			if (menuButton.inBoundingBox(thePanel)) {
				hovering = true;
				screen.setPaint(Preferences.COLOR_MENU_SELECT);
				screen.fill(new Rectangle2D.Double(menuButton.getX(), menuButton.getY(),
						menuButton.getW(), menuButton.getH()));
				this.theScreen.setColor(Preferences.COLOR_TEXT_MOUSE);
			} else {
				this.theScreen.setColor(Preferences.COLOR_TEXT);
			}
			if (ButtonText.equals("AI - ")) {
				if (this.theData.getAIReverse()) {
					ButtonText += "With Reverse";
				} else {
					ButtonText += "No Reverse";
				}
			}
			this.theScreen.drawString(ButtonText,
					60,
					menuButton.getY()+fm.getAscent()+(menuButton.getH() - (fm.getAscent() + fm.getDescent()))/2);
		}
		if (hovering) {
			this.thePanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		} else {
			this.thePanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}

		this.theScreen.setColor(Preferences.COLOR_TEXT);
		Font controlFont = this.font.deriveFont(40f);
		this.theScreen.setFont(controlFont);
		fm = this.theScreen.getFontMetrics();
		String controlText =
				"Movement     Arrow Keys\n" +
				"Reverse        R\n" +
				"AI Mode         A\n" +
				"Pause           P or esc";
		MenuButton lastButton = this.theData.getMenuButtons().get(this.theData.getMenuButtons().size()-1);
		int startingY = 60+lastButton.getY()+lastButton.getH();
		int lineHeight = fm.getHeight();
		for (String line : controlText.split("\n")) {
			this.theScreen.drawString(line, 60, startingY);
			startingY += lineHeight;
		}
	}

	/**
	 * Displays the game-over message.
	 */
	private void displayGameOver() {
		Graphics2D screen = (Graphics2D) this.theScreen;

		screen.setPaint(Preferences.COLOR_PAUSE);
		screen.fill(new Rectangle2D.Double(0, 0,
				Preferences.GAMEWIDTH,
				Preferences.GAMEBOARDHEIGHT));

		Font titleFont = this.font.deriveFont(130f);
		this.theScreen.setFont(titleFont);
		FontMetrics fm = this.theScreen.getFontMetrics();
		this.theScreen.setColor(Preferences.COLOR_TEXT);
		this.theScreen.drawString(Preferences.GAME_OVER_TEXT,
				(Preferences.GAMEWIDTH - fm.stringWidth(Preferences.GAME_OVER_TEXT))/2,
				(int) (Preferences.GAMEBOARDHEIGHT*0.3));

		String scoreText = "Score: " + this.theData.getSnakeCells().size();
		Font scoreFont = this.font.deriveFont(50f);
		this.theScreen.setFont(scoreFont);
		fm = this.theScreen.getFontMetrics();
		this.theScreen.setColor(Preferences.COLOR_TEXT);
		this.theScreen.drawString(scoreText,
				(Preferences.GAMEWIDTH - fm.stringWidth(scoreText))/2,
				(int) (Preferences.GAMEBOARDHEIGHT*0.5));

		Font playAgainFont = this.font.deriveFont(80f);
		this.theScreen.setFont(playAgainFont);
		fm = this.theScreen.getFontMetrics();
		MenuButton playAgain = this.theData.getPlayAgain();
		if (playAgain.inBoundingBox(thePanel)) {
			this.thePanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
			screen.setPaint(Preferences.COLOR_MENU_SELECT);
			screen.fill(new Rectangle2D.Double(playAgain.getX(), playAgain.getY(),
					playAgain.getW(), playAgain.getH()));
			this.theScreen.setColor(Preferences.COLOR_TEXT_MOUSE);
		} else {
			this.thePanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			this.theScreen.setColor(Preferences.COLOR_TEXT);
		}
		this.theScreen.drawString(playAgain.getText(),
				(Preferences.GAMEWIDTH - fm.stringWidth(playAgain.getText()))/2,
				(int) (Preferences.GAMEBOARDHEIGHT*0.75));
	}
}

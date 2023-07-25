package Model;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.FontRenderContext;


public final class Preferences {
	// Timing Constants:
	public static final int REFRESH_RATE = 2;
	public static final int FOOD_ADD_RATE = 20;
	public static final int SLEEP_TIME = 30; // milliseconds between updates
	// Sizing Constants:
	public static final int MENU_HEIGHT = 100;
	public static final int NUM_CELLS_WIDE = 50;
	public static final int NUM_CELLS_TALL = 30;
	public static final int CELL_SIZE = 20;
	public static final int SPACE_FOR_TITLE = 60;
	public static final int GAMEBOARDHEIGHT = NUM_CELLS_TALL * CELL_SIZE + SPACE_FOR_TITLE;
	public static final int GAMEHEIGHT = NUM_CELLS_TALL * CELL_SIZE;
	public static final int GAMEWIDTH = NUM_CELLS_WIDE * CELL_SIZE;
	public static final int SHADOW_OFFSET = 2;
	public static final double FOOD_SIZE_RATIO = 0.7;
	public static final double SNAKE_SIZE_RATIO = 0.9;
	public static final double SNAKE_EYES_RATIO = 0.6;
	public static final double SNAKE_EYES_DIFF_RATIO = 0.3;
	public static final double SNAKE_EYES_SIZE_RATIO = 0.15;
	// Colors Constants:
	public static final Color COLOR_BACKGROUND = new Color(110, 214, 242);
	public static final Color COLOR_WALL = new Color(148, 244, 161);
	public static final Color COLOR_FOOD = new Color(251, 177, 116);
	public static final Color COLOR_OPEN = new Color(201, 254, 209);
	public static final Color COLOR_SNAKE = new Color(246, 161, 161);
	public static final Color COLOR_SNAKE_END = new Color(246, 120, 120);
	public static final Color COLOR_SNAKE_EYES = new Color(54, 54, 54);
	public static final Color COLOR_SHADOW = new Color(61, 61, 61, 128);
	public static final Color COLOR_PAUSE = new Color(0, 0, 0, 204);
	public static final Color COLOR_TEXT = new Color(221, 231, 231);
	public static final Color COLOR_TEXT_MOUSE = new Color(73, 73, 73);
	public static final Color COLOR_MENU_SELECT = new Color(189, 189, 189, 204);
	// Text display
	public static final String TITLE = "S N A K E";
	public static final String GAME_OVER_TEXT = "Game Over";
}

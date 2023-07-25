package Model;

import javax.swing.*;
import java.awt.geom.Point2D;
import java.lang.*;
import java.awt.*;

public class MenuButton {

    /** Bounding Box for detection for mouse activity */
    private int x;
    private int y;
    private int h;
    private int w;

    /** Button Text */
    private String text;

    /** Whether or not the button is active */
    private boolean active = false;

    /**
     * Constructor for Menu Button
     */
    public MenuButton(int x, int y, int w, int h, String text) {
        this.x = x;
        this.y = y;
        this.h = h;
        this.w = w;
        this.text = text;
    }

    /* ---------------------------- */
    /* Getters and Setter functions */
    /* ---------------------------- */

    public int getX() { return this.x; };

    public void setX(int x) { this.x = x; }

    public int getY() { return this.y; };

    public void setY(int y) { this.y = y; }

    public int getH() { return this.h; };

    public void setH(int h) { this.h = h; }

    public int getW() { return this.w; };

    public void setW(int w) { this.w = w; }

    public String getText() { return this.text; };

    public void setText(String text) { this.text = text; }

    public boolean getActive() { return this.active; }

    public void setActive(boolean active) { this.active = active; }

    /* ----------------------- */
    /* Mouse-Related Functions */
    /* ----------------------- */

    public boolean inBoundingBox(JPanel centralPanel) {
        PointerInfo a = MouseInfo.getPointerInfo();
        Point b = a.getLocation();
        // Get Mouse position
        double mouseX = b.getX();
        double mouseY = b.getY();
        // Get Applet position
        double panelX = centralPanel.getLocationOnScreen().getX();
        double panelY = centralPanel.getLocationOnScreen().getY();
        // Check is mouse in over the button
        if (this.x+panelX < mouseX && mouseX < this.x+this.w+panelX &&
                this.y+panelY < mouseY && mouseY < this.y+this.h+panelY) {
            return true;
        }
        return false;
    }
}

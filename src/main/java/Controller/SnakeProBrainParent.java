package Controller;

import Model.Preferences;
import View.SnakeProDisplay;
import View.SnakeProImagePanel;

import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.IOException;
import java.net.URL;

//DO NOT MODIFY
/**
 * Controller.SnakeProBrainParent - Provides back-end for Controller.SnakeProBrain and View.SnakeProDisplay
 * 
 * @author CS60 instructors
 */
public abstract class SnakeProBrainParent extends JApplet implements
		ActionListener, KeyListener, MouseListener, Runnable {

	// off screen buffer of image:
	public Image image;

	// the buffer's graphical tools:
	public Graphics screen; 

	// Central Panel
	public SnakeProImagePanel centralPanel;

	// Buttons
	private JButton newGameButton;

	// Here are other data members you might like to use (optional)...
	public AudioClip audioFood; // Food sound
	public AudioClip audioCrunch; // Crunch sound
	public AudioClip audioMeow; // Meow sound

	// Method called when SnakePro is started! (not per game)
	public void init() {

		// set positions
		this.setLayout(new BorderLayout());

		// Sets up the back (off-screen) buffer for drawing, named image
		this.image = this.createImage(Preferences.GAMEWIDTH, Preferences.GAMEBOARDHEIGHT);
		this.screen = this.image.getGraphics(); // screen holds the drawing routines

		// Add a central panel which holds the buffer (the game board)
		centralPanel = new SnakeProImagePanel(image);
		this.add(centralPanel, BorderLayout.CENTER);

		// register key and mouse listeners with the JPanel
		centralPanel.addKeyListener(this);
		centralPanel.addMouseListener(this);

		// Resize Window to fit game
		resize(Preferences.GAMEWIDTH, Preferences.GAMEBOARDHEIGHT);

		try {
			this.startFirstGame(); // Set up the game internals!
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.repaint(); // re-render the environment to the screen
		this.centralPanel.requestFocus();
		this.go();
	}

	// Here is how buttons and menu items work...
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();
		this.requestFocus(); // makes sure the Applet keeps keyboard focus
	}

	public void keyReleased(KeyEvent evt) {
		// Not used
	}

	public void keyTyped(KeyEvent evt) {
		// Not used
	}

	public void mouseEntered(MouseEvent evt) {
		// Not used
	}

	public void mouseExited(MouseEvent evt) {
		// Not used
	}

	public void mousePressed(MouseEvent evt) {
		// Not used
	}

	public void mouseReleased(MouseEvent evt) {
		// Not used
	}

	/*
	 * The following methods and data members are used to implement the Runnable
	 * interface and to support pausing and resuming the applet.
	 */
	Thread thread; // the thread controlling the updates
	boolean running; // whether or not the thread is stopped

	/*
	 * This is the method that calls the "cycle()" method every so often (every
	 * sleepTime milliseconds).
	 */
	public void run() {
		while (this.running) {
			try {
				if (this.thread != null) {
					Thread.sleep(Preferences.SLEEP_TIME);
				}
			} catch (InterruptedException e) {}

			try {
				this.cycle(); // this represents 1 update cycle for the environment
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.thread = null;
	}

	/*
	 * This is the method attached to the "Start" button
	 */
	public synchronized void go() {
		if (this.thread == null) {
			this.thread = new Thread(this);
			this.running = true;
			this.thread.start();
		}
	}

	/*
	 * This is a method called when you leave the page that contains the applet.
	 * It stops the thread altogether.
	 */
	public synchronized void stop() {
		this.running = false;
	}

	/*
	 * Methods that will be overridden to provide SnakePro functionality
	 */
	abstract void cycle();

	abstract void startFirstGame();

	public abstract void keyPressed(KeyEvent evt);

	public abstract void mouseClicked(MouseEvent evt);

	private static final long serialVersionUID = 1L;

	/* This is the end of the SnakeProBase class */
}

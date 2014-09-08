package roguelike.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import roguelike.MainScreen;
import roguelike.Screen;
import roguelike.TitleScreen;
import squidpony.squidcolor.SColor;
import squidpony.squidgrid.gui.TextCellFactory;

public class MainWindow {
	// private static final String CHARS_USED = "☃☺.,Xy@";
	private static final String CHARS_USED = "#@.~M";

	public static final int screenWidth = 1000;
	public static final int screenHeight = 700;
	public static final int cellWidth = 16;
	public static final int cellHeight = 20;

	public static final int width = screenWidth / cellWidth;
	public static final int height = screenHeight / cellHeight;
	public static final int statWidth = 20, fontSize = 22, outputLines = 5;

	private JFrame frame;

	final int FRAMES_PER_SECOND = 50;
	final int SKIP_TICKS = 1000 / FRAMES_PER_SECOND;

	private long nextTick;
	private long time;
	private double lastFrame;

	private Screen currentScreen;
	private DisplayManager displayManager;

	private JLayeredPane layeredPane;

	public MainWindow() {

		System.out.println("SKIP_TICKS: " + SKIP_TICKS);
		displayManager = new DisplayManager(fontSize, cellWidth, cellHeight);

		initFrame();

		currentScreen = new TitleScreen(displayManager.getTerminal());
		// currentScreen = new MainScreen(displayManager.getTerminal());

		while (true) {
			currentScreen.draw();
			currentScreen.process();
			currentScreen = currentScreen.getScreen();

			displayManager.refresh();

			nextTick += SKIP_TICKS;
			long sleepTime = nextTick - System.currentTimeMillis();
			if (sleepTime >= 0) {
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * startGame();
	 * 
	 * time = 0; lastFrame = time; nextTick = System.currentTimeMillis();
	 * TurnResult run = game.processTurn();
	 * 
	 * // runGame(); while (run.isRunning() || !started) { started = true; if
	 * (game.isPlayerDead() || !game.getIsRunning()) {
	 * 
	 * if (drawTitle) { // TODO: refactor into Screen object
	 * InputManager.setInputEnabled(true); animationManager.clear();
	 * 
	 * drawTitleScreen(screenFont);
	 * 
	 * game = gameLoader.newGame(); statsDisplay.setPlayer(game.getPlayer());
	 * 
	 * drawTitle = false; displayManager.setDirty(); stopGame(); } KeyEvent
	 * nextKey = InputManager.nextKey(); if (nextKey == null) continue;
	 * 
	 * if (nextKey.getKeyCode() == KeyEvent.VK_ENTER) { game.initialize();
	 * 
	 * initGamePanels(); messageDisplay.clear(); run = new TurnResult(true);
	 * 
	 * drawTitle = true;
	 * 
	 * startGame();
	 * 
	 * } else if (nextKey.getKeyCode() == KeyEvent.VK_ESCAPE) { run = new
	 * TurnResult(false); stopGame(); }
	 * 
	 * } else { run = processGameLoop(run);
	 * 
	 * } }
	 * 
	 * // quit System.exit(0);
	 */

	private void initFrame() {
		frame = new JFrame("Untitled Roguelike");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {
			frame.setIconImage(ImageIO.read(new File("./icon.png")));
		} catch (IOException ex) {
			// don't do anything if it failed, the default Java icon will be
			// used
		}

		InputManager.registerWithFrame(frame);

		// initGamePanels();
		displayManager.init(width, height);

		JPanel mainWinPanel = new JPanel();

		mainWinPanel.setBackground(SColor.BLACK);
		mainWinPanel.setLayout(new BorderLayout());

		layeredPane = displayManager.displayPane();

		mainWinPanel.add(layeredPane, BorderLayout.WEST);

		frame.add(mainWinPanel);
		frame.pack();

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	/*
	 * boolean drawing = true;
	 * 
	 * private void startGame() { // drawing = true; // Thread thread = new
	 * Thread(new Runnable() { // @Override // public void run() { // runGame();
	 * // } // }); // thread.start(); }
	 * 
	 * private void stopGame() { drawing = false; }
	 * 
	 * private void runGame() { final Timer t = new Timer(SKIP_TICKS, new
	 * ActionListener() {
	 * 
	 * @Override public void actionPerformed(ActionEvent e) { long durationMs =
	 * drawFrame();
	 * 
	 * currentTurn = game.processTurn();
	 * 
	 * nextTick += SKIP_TICKS; // long sleepTime = nextTick -
	 * System.currentTimeMillis(); // try { // // Thread.sleep(Math.max(0,
	 * SKIP_TICKS - durationMs)); // Thread.sleep(Math.max(0, sleepTime)); //
	 * System.out.println("drawing " + durationMs); // } catch
	 * (InterruptedException ex) { // }
	 * 
	 * if (!game.getIsRunning()) { System.exit(0); } } }); t.start();
	 * 
	 * // while (drawing) { // } }
	 */
}

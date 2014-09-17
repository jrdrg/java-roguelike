package roguelike.ui;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import roguelike.Screen;
import roguelike.TitleScreen;
import squidpony.squidcolor.SColor;

public class MainWindow {
	public static final int screenWidth = 1200;
	public static final int screenHeight = 700;
	public static final int cellWidth = 14;
	public static final int cellHeight = 20;

	public static final int width = screenWidth / cellWidth;
	public static final int height = screenHeight / cellHeight;
	public static final int statWidth = 20, fontSize = 22, outputLines = 5;

	private JFrame frame;

	final int FRAMES_PER_SECOND = 40;
	final int SKIP_TICKS = 1000 / FRAMES_PER_SECOND;

	private Screen currentScreen;
	private DisplayManager displayManager;

	private JLayeredPane layeredPane;

	public MainWindow() {

		System.out.println("SKIP_TICKS: " + SKIP_TICKS);
		displayManager = new DisplayManager(fontSize, cellWidth, cellHeight);

		initFrame();
		setKeyBindings();

		currentScreen = new TitleScreen(displayManager.getTerminal());

		long nextTick = System.currentTimeMillis();
		while (true) {
			currentScreen.process();
			currentScreen = currentScreen.getScreen();
			currentScreen.draw();

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

	public void setKeyBindings() {
		KeyMap defaultKeys = new KeyMap("Default");

		defaultKeys
				.bindKey(KeyEvent.VK_UP, InputCommand.UP)
				.bindKey(KeyEvent.VK_DOWN, InputCommand.DOWN)
				.bindKey(KeyEvent.VK_LEFT, InputCommand.LEFT)
				.bindKey(KeyEvent.VK_RIGHT, InputCommand.RIGHT)
				.bindKey(KeyEvent.VK_UP, true, InputCommand.UP_LEFT)
				.bindKey(KeyEvent.VK_RIGHT, true, InputCommand.UP_RIGHT)
				.bindKey(KeyEvent.VK_DOWN, true, InputCommand.DOWN_RIGHT)
				.bindKey(KeyEvent.VK_LEFT, true, InputCommand.DOWN_LEFT)

				.bindKey(KeyEvent.VK_ENTER, InputCommand.CONFIRM)
				.bindKey(KeyEvent.VK_ESCAPE, InputCommand.CANCEL)

				.bindKey(KeyEvent.VK_PERIOD, InputCommand.REST)
				.bindKey(KeyEvent.VK_C, InputCommand.CLOSE_DOOR)
				.bindKey(KeyEvent.VK_I, InputCommand.INVENTORY)
				.bindKey(KeyEvent.VK_G, InputCommand.PICK_UP)
				.bindKey(KeyEvent.VK_L, InputCommand.LOOK);

		InputManager.setActiveKeybindings(defaultKeys);
	}

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

		displayManager.init(width, height);

		layeredPane = displayManager.displayPane();

		JPanel mainWinPanel = new JPanel();
		mainWinPanel.setBackground(SColor.BLACK);
		mainWinPanel.setLayout(new BorderLayout());
		mainWinPanel.add(layeredPane, BorderLayout.WEST);

		frame.add(mainWinPanel);
		frame.pack();

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		hideMouseCursor();
	}

	private void hideMouseCursor() {
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

		// Create a new blank cursor.
		java.awt.Cursor blankCursor = Toolkit
				.getDefaultToolkit()
				.createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");

		// Set the blank cursor to the JFrame.
		frame.getContentPane().setCursor(blankCursor);
	}

}

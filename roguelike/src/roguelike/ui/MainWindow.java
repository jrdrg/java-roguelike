package roguelike.ui;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;

import roguelike.Game;
import roguelike.Screen;
import roguelike.TitleScreen;
import roguelike.data.serialization.PlayerSerializer;
import roguelike.util.Log;

public class MainWindow {
	public static final int screenWidth = 1200;
	public static final int screenHeight = 690;
	public static final int cellWidth = 9; // in AsciiPanel, this is constant
	public static final int cellHeight = 16;

	public static final int width = screenWidth / cellWidth;
	public static final int height = screenHeight / cellHeight;
	public static final int statWidth = 50, fontSize = 14, outputLines = 5;

	private JFrame frame;

	final int FRAMES_PER_SECOND = 50;
	final int SKIP_TICKS = 1000 / FRAMES_PER_SECOND;

	private JComponent displayPane;
	private Screen currentScreen;
	private DisplayManager displayManager;

	public MainWindow() {

		System.out.println("SKIP_TICKS: " + SKIP_TICKS);
		displayManager = new DisplayManager(fontSize);

		initFrame();
		setKeyBindings();

		/* draw the title screen once before loop, since it won't process until the player presses a key */
		currentScreen = new TitleScreen(displayManager.getTerminal());
		currentScreen.draw();
		displayManager.setDirty();
		displayManager.refresh();

		long nextTick = System.currentTimeMillis();

		while (true) {

			currentScreen.process();
			currentScreen = currentScreen.getScreen();
			long drawTicks = currentScreen.draw();

			displayManager.setDirty();
			displayManager.refresh();

			nextTick += SKIP_TICKS;
			// long sleepTime = SKIP_TICKS - drawTicks;
			long sleepTime = nextTick - System.currentTimeMillis();
			if (sleepTime >= 0) {
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				// Log.warning("d SLEEPTIME < 0, skipping next keypress: " + sleepTime);
				// InputManager.nextCommand();
				Log.warning("draw SLEEPTIME < 0: " + sleepTime + " drawTicks=" + drawTicks);
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

				.bindKey(KeyEvent.VK_GREATER, InputCommand.USE_STAIRS)

				.bindKey(KeyEvent.VK_ENTER, InputCommand.CONFIRM)
				.bindKey(KeyEvent.VK_ESCAPE, InputCommand.CANCEL)
				.bindKey(KeyEvent.VK_SLASH, true, InputCommand.PREVIOUS_TARGET)
				.bindKey(KeyEvent.VK_SLASH, InputCommand.NEXT_TARGET)
				.bindKey(KeyEvent.VK_M, InputCommand.SHOW_MESSAGES)

				.bindKey(KeyEvent.VK_R, InputCommand.RANGED_ATTACK)
				.bindKey(KeyEvent.VK_A, InputCommand.ATTACK)

				.bindKey(KeyEvent.VK_PERIOD, InputCommand.REST)
				.bindKey(KeyEvent.VK_C, InputCommand.CLOSE_DOOR)
				.bindKey(KeyEvent.VK_I, InputCommand.INVENTORY)
				.bindKey(KeyEvent.VK_G, InputCommand.PICK_UP)
				.bindKey(KeyEvent.VK_L, InputCommand.LOOK);

		InputManager.setActiveKeybindings(defaultKeys);
		InputManager.DefaultKeyBindings = defaultKeys;
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
		displayPane = displayManager.displayPane();

		frame.add(displayPane);
		frame.pack();

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setResizable(false);

		Log.info("Window size: " + frame.getSize().width + "x" + frame.getSize().height);

		hideMouseCursor();

		frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentHidden(ComponentEvent e) {

				PlayerSerializer.serialize(Game.current().getPlayer());

				((JFrame) (e.getComponent())).dispose();
			}
		});

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {

				if (Game.current() != null && Game.current().getPlayer() != null)
					PlayerSerializer.serialize(Game.current().getPlayer());

			}
		}, "Shutdown-thread"));

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

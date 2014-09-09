package roguelike.ui;

import java.awt.BorderLayout;
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
	public static final int cellWidth = 16;
	public static final int cellHeight = 20;

	public static final int width = screenWidth / cellWidth;
	public static final int height = screenHeight / cellHeight;
	public static final int statWidth = 20, fontSize = 22, outputLines = 5;

	private JFrame frame;

	final int FRAMES_PER_SECOND = 50;
	final int SKIP_TICKS = 1000 / FRAMES_PER_SECOND;

	private Screen currentScreen;
	private DisplayManager displayManager;

	private JLayeredPane layeredPane;

	public MainWindow() {

		System.out.println("SKIP_TICKS: " + SKIP_TICKS);
		displayManager = new DisplayManager(fontSize, cellWidth, cellHeight);

		initFrame();

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
	}

}

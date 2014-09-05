package roguelike.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import roguelike.Game;
import roguelike.GameLoader;
import roguelike.MessageDisplayProperties;
import roguelike.TurnEvent;
import roguelike.TurnResult;
import roguelike.actors.Actor;
import roguelike.maps.MapArea;
import roguelike.maps.Tile;
import roguelike.ui.windows.Dialog;
import roguelike.ui.windows.InputManager;
import roguelike.util.Coordinate;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;
import squidpony.squidgrid.fov.FOVTranslator;
import squidpony.squidgrid.fov.TranslucenceWrapperFOV;
import squidpony.squidgrid.gui.SwingPane;
import squidpony.squidgrid.gui.TextCellFactory;
import squidpony.squidgrid.util.BasicRadiusStrategy;
import squidpony.squidgrid.util.DirectionIntercardinal;
import squidpony.squidgrid.util.RadiusStrategy;

public class MainWindow {
	private static final String CHARS_USED = "☃☺.,Xy@";

	private static final int screenWidth = 1000;
	private static final int screenHeight = 700;
	private static final int cellWidth = 16;
	private static final int cellHeight = 20;

	private static final int width = screenWidth / cellWidth;
	private static final int height = screenHeight / cellHeight;
	private static final int statWidth = 20, fontSize = 22, outputLines = 5;

	private JFrame frame;
	private JLayeredPane layeredPane;
	private JPanel mainWinPanel;
	private JPanel titlePanel;
	private SwingPane statsPanel;
	private SwingPane outputPanel;
	private final FOVTranslator fov = new FOVTranslator(new TranslucenceWrapperFOV());
	private final RadiusStrategy radiusStrategy = BasicRadiusStrategy.CIRCLE;
	private Font screenFont;

	Game game;
	GameLoader gameLoader;
	MessageDisplay messageDisplay;
	StatsDisplay statsDisplay;
	DisplayManager displayManager;

	final int FRAMES_PER_SECOND = 25;
	final int SKIP_TICKS = 1000 / FRAMES_PER_SECOND;

	private long nextTick;
	private long time;
	private double lastFrame;

	public MainWindow() {

		System.out.println("SKIP_TICKS: " + SKIP_TICKS);

		displayManager = new DisplayManager(fontSize, cellWidth, cellHeight);
		displayManager.init(width, height);
		screenFont = displayManager.screenFont();

		initFrame();

		messageDisplay = new MessageDisplay(outputPanel, outputLines);
		statsDisplay = new StatsDisplay(statsPanel);

		gameLoader = new GameLoader();

		game = gameLoader.newGame();
		statsDisplay.setPlayer(game.getPlayer());

		game.initialize();
		boolean drawTitle = true;
		boolean started = false;

		/* used for FOV lighting */
		SColorFactory.addPallet("light", SColorFactory.asGradient(SColor.WHITE, SColor.DARK_SLATE_GRAY));

		time = 0;
		lastFrame = time;
		nextTick = System.currentTimeMillis();
		TurnResult run = game.processTurn();
		while (run.isRunning() || !started) {
			started = true;
			if (game.isPlayerDead() || !game.getIsRunning()) {

				if (drawTitle) {
					// TODO: refactor into Screen object

					drawTitleScreen(screenFont);

					game = gameLoader.newGame();
					statsDisplay.setPlayer(game.getPlayer());

					drawTitle = false;
				}
				KeyEvent nextKey = InputManager.nextKey();
				if (nextKey == null)
					continue;

				if (nextKey.getKeyCode() == KeyEvent.VK_ENTER) {
					game.initialize();

					initGamePanels();
					messageDisplay.clear();
					run = new TurnResult(true);

					drawTitle = true;

				} else if (nextKey.getKeyCode() == KeyEvent.VK_ESCAPE) {
					run = new TurnResult(false);
				}

			} else {
				run = processGameLoop(run);

			}
		}

		// quit
		System.exit(0);
	}

	private TurnResult processGameLoop(TurnResult run) {
		long start = System.currentTimeMillis();

		if (!drawActiveWindow(run)) {
			drawMap();
			drawEvents(run);
		}
		drawMessages(run);
		drawStats();

		/*
		 * this will only refresh if player input has occurred or something has
		 * reset the dirty flag
		 */
		displayManager.refresh();

		run = game.processTurn();

		nextTick += SKIP_TICKS;
		long sleepTime = nextTick - System.currentTimeMillis();
		if (sleepTime >= 0) {
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		long end = System.currentTimeMillis();
		time = end - start;
		time = (long) (time * 0.9 + lastFrame * 0.1);
		lastFrame = time;

		statsPanel.put(0, 10, "FPS: " + time);
		return run;
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

		initGamePanels();

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private void initGamePanels() {
		if (titlePanel != null)
			frame.remove(titlePanel);

		if (mainWinPanel == null) {
			mainWinPanel = new JPanel();
			mainWinPanel.setBackground(SColor.BLACK);
			mainWinPanel.setLayout(new BorderLayout());

			Font font = displayManager.screenFont();
			TextCellFactory textFactory2 = new TextCellFactory(font, cellWidth / 1, cellHeight, true, 0, CHARS_USED);

			layeredPane = displayManager.displayPane();

			mainWinPanel.add(layeredPane, BorderLayout.WEST);

			statsPanel = new SwingPane(statWidth, height, textFactory2, null);
			statsPanel.setDefaultForeground(SColor.WHITE);
			statsPanel.put(0, 0, "Stats");
			statsPanel.refresh();
			mainWinPanel.add(statsPanel, BorderLayout.EAST);

			outputPanel = new SwingPane(width + statsPanel.gridWidth(), outputLines, textFactory2, null);
			outputPanel.setDefaultForeground(SColor.AMARANTH);
			outputPanel.put(0, 0, "Output");

			outputPanel.refresh();
			mainWinPanel.add(outputPanel, BorderLayout.SOUTH);
		}
		frame.add(mainWinPanel);
		frame.pack();
	}

	// TODO: create separate screens
	private void drawTitleScreen(Font font) {
		if (mainWinPanel != null)
			frame.remove(mainWinPanel);

		if (titlePanel == null) {
			titlePanel = new JPanel();
			titlePanel.setBackground(SColor.BLACK);
			titlePanel.setLayout(new BorderLayout());

			TextCellFactory textFactory = new TextCellFactory(font, cellWidth, cellHeight, true, 0, CHARS_USED);
			// SwingPane pane = new SwingPane(width + statWidth / 2, height +
			// outputLines, textFactory, null);
			SwingPane pane = new SwingPane(width + statWidth, height + outputLines, textFactory, null);
			pane.setDefaultForeground(SColor.WHITE);
			String title = "Title Screen";
			int x = (int) ((pane.gridWidth() / 2f) - (title.length() / 2f));

			pane.put(x, 10, "Title Screen");
			pane.refresh();
			titlePanel.add(pane, BorderLayout.WEST);
		}
		System.out.println("Drawing title screen");

		frame.add(titlePanel);
		frame.pack();
	}

	private void drawMap() {
		MapArea currentMap = game.getCurrentMapArea();
		Rectangle screenArea = currentMap.getAreaInTiles(width, height, game.getPlayer().getPosition());

		doFOV(currentMap, screenArea, game.getPlayer().getPosition());

		for (int x = screenArea.x; x < screenArea.getMaxX(); x++) {
			for (int y = screenArea.y; y < screenArea.getMaxY(); y++) {
				Tile tile = currentMap.getTileAt(x, y);
				int screenX = x - screenArea.x;
				int screenY = y - screenArea.y;
				if (tile.isVisible()) {
					SColor color, bgColor;
					color = SColorFactory.lightWith(tile.getColor(), tile.getLightedColorValue());
					bgColor = SColorFactory.lightWith(tile.getBackground(), tile.getLightedColorValue());

					displayManager.getTerminal().withColor(color, bgColor).put(screenX, screenY, tile.getSymbol());
				} else {
					displayManager.getTerminal().withColor(tile.getColor(), tile.getBackground()).put(screenX, screenY, tile.getSymbol());

				}
			}
		}
	}

	/**
	 * Calculates the Field of View and marks the maps spots seen appropriately.
	 */
	private void doFOV(MapArea currentMap, Rectangle screenArea, Coordinate player) {
		boolean[][] walls = new boolean[width][height];
		float[][] lighting = new float[width][height];

		for (int x = screenArea.x; x < screenArea.getMaxX(); x++) {
			for (int y = screenArea.y; y < screenArea.getMaxY(); y++) {
				Tile tile = currentMap.getTileAt(x, y);
				if (tile != null) {
					walls[x - screenArea.x][y - screenArea.y] = tile.isWall();
					lighting[x - screenArea.x][y - screenArea.y] = tile.getLighting();
				}
			}
		}

		float lightForce = game.getPlayer().getVisionRadius();
		float[][] incomingLight = fov.calculateFOV(lighting, player.x - screenArea.x, player.y - screenArea.y, 1f, 1 / lightForce, radiusStrategy);

		for (int x = screenArea.x; x < screenArea.getMaxX(); x++) {
			for (int y = screenArea.y; y < screenArea.getMaxY(); y++) {
				int cX = x - screenArea.x;
				int cY = y - screenArea.y;

				Tile tile = currentMap.getTileAt(x, y);
				tile.setVisible(fov.isLit(cX, cY));

				if (incomingLight[cX][cY] > 0) {
					float bright = 1 - incomingLight[cX][cY];
					tile.setLightedColorValue(SColorFactory.fromPallet("light", bright));
					// clean[x][y] = false;
				} else if (!tile.getLightedColorValue().equals(SColor.BLACK)) {
					tile.setLightedColorValue(SColor.BLACK);
					// clean[x][y] = false;
				}

			}
		}
	}

	private void drawEvents(TurnResult run) {
		Rectangle screenArea = game.getCurrentMapArea().getAreaInTiles(width, height, game.getPlayer().getPosition());
		for (TurnEvent event : run.getEvents()) {
			if (event.getType() == TurnEvent.ATTACKED) {
				Actor attacker = event.getInitiator();
				Actor target = event.getTarget();

				Coordinate attackerPos = attacker.getPosition();
				Coordinate targetPos = target.getPosition();
				Coordinate diff = attacker.getPosition().createOffsetPosition(-targetPos.x, -targetPos.y);
				DirectionIntercardinal direction = DirectionIntercardinal.getDirection(-diff.x, -diff.y);

				System.out.println(attacker.getName() + " attacks " + target.getName() + " in direction " + direction.symbol);

				// TODO: add animations here
				if (screenArea.contains(attackerPos) && screenArea.contains(targetPos)) {

					// try {
					// bgPanel.put(target.getPosition().x - screenArea.x,
					// target.getPosition().y - screenArea.y,
					// SColorFactory.asSColor(100, 0, 0));
					// bgPanel.bump(attacker.getPosition().createOffsetPosition(-screenArea.x,
					// -screenArea.y), direction);
					// } catch (Exception e) {
					// System.out.println("unable to display " + e);
					// throw e;
					// }

					// mapPanel.bump(target.getPosition(), direction);
					// Point end = new Point(direction.deltaX + targetPos.x,
					// direction.deltaY + targetPos.y);
					// mapPanel.slide(targetPos, end, 100);
					// mapPanel.slide(end, targetPos, 100);
					// mapPanel.waitForAnimations();
				}
			}
		}
		// bgPanel.waitForAnimations();
	}

	private boolean drawActiveWindow(TurnResult run) {
		Dialog window = run.getActiveWindow();
		if (window != null) {
			window.showInPane(displayManager.getTerminal());
			window.draw();

			return true;
		}

		return false;
	}

	private void drawMessages(TurnResult run) {
		for (MessageDisplayProperties msg : run.getMessages()) {
			messageDisplay.display(msg);
		}
		messageDisplay.draw();
	}

	private void drawStats() {
		statsDisplay.draw();
	}
}

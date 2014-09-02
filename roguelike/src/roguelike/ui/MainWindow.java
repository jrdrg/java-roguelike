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
import roguelike.actors.Player;
import roguelike.maps.MapArea;
import roguelike.maps.Tile;
import roguelike.util.Coordinate;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;
import squidpony.squidgrid.fov.FOVTranslator;
import squidpony.squidgrid.fov.TranslucenceWrapperFOV;
import squidpony.squidgrid.gui.SGKeyListener;
import squidpony.squidgrid.gui.SwingPane;
import squidpony.squidgrid.gui.TextCellFactory;
import squidpony.squidgrid.util.BasicRadiusStrategy;
import squidpony.squidgrid.util.DirectionIntercardinal;
import squidpony.squidgrid.util.RadiusStrategy;

public class MainWindow {

	private static final int width = 50, height = 30, statWidth = 20, fontSize = 22, outputLines = 5;
	private JFrame frame;
	private SGKeyListener keyListener;
	private JLayeredPane layeredPane;
	private JPanel mainWinPanel;
	private JPanel titlePanel;
	private int cellWidth = 26;
	private int cellHeight = 26;
	private SwingPane fgPanel;
	private SwingPane bgPanel;
	private SwingPane windowPanel;
	private SwingPane statsPanel;
	private SwingPane outputPanel;
	private static final String CHARS_USED = "☃☺.,Xy@";
	private final FOVTranslator fov = new FOVTranslator(new TranslucenceWrapperFOV());
	private final RadiusStrategy radiusStrategy = BasicRadiusStrategy.CIRCLE;
	private Font screenFont;

	Game game;
	GameLoader gameLoader;
	MessageDisplay messageDisplay;

	public MainWindow() {
		// screenFont = new Font("Lucidia", Font.PLAIN, fontSize);
		screenFont = new Font("Lucida Sans Typewriter", Font.PLAIN, fontSize);

		initFrame();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		messageDisplay = new MessageDisplay(outputPanel, outputLines);
		gameLoader = new GameLoader(keyListener);
		game = gameLoader.newGame();
		game.initialize();

		TurnResult run = game.processTurn();
		while (run.isRunning()) {
			if (game.isPlayerDead() || !game.getIsRunning()) {

				boolean drawTitle = true;
				if (drawTitle) {
					// TODO: refactor into Screen object

					drawTitleScreen(screenFont);
					game = gameLoader.newGame();

					drawTitle = false;
				}
				KeyEvent nextKey = keyListener.next();
				if (nextKey.getKeyCode() == KeyEvent.VK_ENTER) {
					game.initialize();

					initGamePanels(screenFont);
					messageDisplay.clear();
					run = new TurnResult(true);

				} else if (nextKey.getKeyCode() == KeyEvent.VK_ESCAPE) {
					run = new TurnResult(false);
				}

			} else {
				drawMap();
				drawEvents(run);
				drawMessages(run);
				drawStats();
				run = game.processTurn();
			}
		}

		// quit
		System.exit(0);
	}

	private void initFrame() {
		frame = new JFrame("Roguelike");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {
			frame.setIconImage(ImageIO.read(new File("./icon.png")));
		} catch (IOException ex) {
			// don't do anything if it failed, the default Java icon will be
			// used
		}

		keyListener = new SGKeyListener(true, SGKeyListener.CaptureType.DOWN);
		frame.addKeyListener(keyListener);

		initGamePanels(screenFont);
	}

	private void initGamePanels(Font font) {
		if (titlePanel != null)
			frame.remove(titlePanel);

		if (mainWinPanel == null) {
			mainWinPanel = new JPanel();
			mainWinPanel.setBackground(SColor.BLACK);
			mainWinPanel.setLayout(new BorderLayout());

			TextCellFactory textFactory = new TextCellFactory(font, cellWidth, cellHeight, true, 0, CHARS_USED);
			fgPanel = new SwingPane(width, height, textFactory, null);
			fgPanel.put(width / 2 - 4, height / 2, "Loading");
			fgPanel.refresh();

			bgPanel = new SwingPane(width, height, textFactory, null);
			windowPanel = new SwingPane(width, height, textFactory, null);

			layeredPane = new JLayeredPane();
			layeredPane.setLayer(fgPanel, JLayeredPane.PALETTE_LAYER);
			layeredPane.setLayer(bgPanel, JLayeredPane.DEFAULT_LAYER);
			layeredPane.setLayer(windowPanel, JLayeredPane.POPUP_LAYER);

			layeredPane.add(windowPanel);
			layeredPane.add(fgPanel);
			layeredPane.add(bgPanel);

			layeredPane.setSize(fgPanel.getPreferredSize());
			layeredPane.setPreferredSize(fgPanel.getPreferredSize());
			layeredPane.setMinimumSize(fgPanel.getPreferredSize());

			mainWinPanel.add(layeredPane, BorderLayout.WEST);

			TextCellFactory textFactory2 = new TextCellFactory(font, cellWidth / 2, cellHeight, true, 0, CHARS_USED);
			statsPanel = new SwingPane(statWidth, fgPanel.gridHeight(), textFactory2, null);
			statsPanel.setDefaultForeground(SColor.WHITE);
			statsPanel.put(0, 0, "Stats");
			statsPanel.refresh();
			mainWinPanel.add(statsPanel, BorderLayout.EAST);

			outputPanel = new SwingPane(fgPanel.gridWidth() + statsPanel.gridWidth(), outputLines, textFactory2, null);
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
		frame.remove(mainWinPanel);

		if (titlePanel == null) {
			titlePanel = new JPanel();
			titlePanel.setBackground(SColor.BLACK);
			titlePanel.setLayout(new BorderLayout());

			TextCellFactory textFactory = new TextCellFactory(font, cellWidth, cellHeight, true, 0, CHARS_USED);
			SwingPane pane = new SwingPane(width + statWidth / 2, height + outputLines, textFactory, null);

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
		doFOV();

		MapArea currentMap = game.getCurrentMapArea();
		Rectangle screenArea = currentMap.getAreaInTiles(width, height, game.getPlayer().getPosition());

		for (int x = screenArea.x; x < screenArea.getMaxX(); x++) {
			for (int y = screenArea.y; y < screenArea.getMaxY(); y++) {
				Tile tile = currentMap.getTileAt(x, y);
				int screenX = x - screenArea.x;
				int screenY = y - screenArea.y;
				if (tile.isVisible()) {
					SColor color, bgColor;
					color = SColorFactory.lightWith(tile.getColor(), tile.getLightedColorValue());
					bgColor = SColorFactory.lightWith(tile.getBackground(), tile.getLightedColorValue());

					bgPanel.put(screenX, screenY, bgColor);
					fgPanel.put(screenX, screenY, tile.getSymbol(), color);
				} else {
					bgPanel.put(screenX, screenY, tile.getBackground());
					fgPanel.put(screenX, screenY, tile.getSymbol(), tile.getColor());
				}
			}
		}
		bgPanel.refresh();
		fgPanel.refresh();
	}

	/**
	 * Calculates the Field of View and marks the maps spots seen appropriately.
	 */
	private void doFOV() {
		Coordinate player = game.getPlayer().getPosition();
		MapArea currentMap = game.getCurrentMapArea();
		Rectangle screenArea = currentMap.getAreaInTiles(width, height, player);
	
		SColorFactory.addPallet("light", SColorFactory.asGradient(SColor.WHITE, SColor.DARK_BROWN));
	
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
	
		// fov.calculateFOV(walls, player.x - screenArea.x, player.y -
		// screenArea.y, width + height);
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
		for (TurnEvent event : run.getEvents()) {
			if (event.getType() == TurnEvent.ATTACKED) {
				Actor attacker = event.getInitiator();
				Actor target = event.getTarget();

				Coordinate targetPos = target.getPosition();

				// Coordinate diff =
				// attacker.getPosition().createOffsetPosition(-targetPos.x,
				// -targetPos.y);
				Coordinate diff = attacker.getPosition().createOffsetPosition(-targetPos.x, -targetPos.y);
				DirectionIntercardinal direction = DirectionIntercardinal.getDirection(diff.x, diff.y);

				System.out.println(attacker.getName() + " attacks " + target.getName() + " in direction " + direction.symbol);
				// TODO: add animations here

				// mapPanel.bump(target.getPosition(), direction);
				// Point end = new Point(direction.deltaX + targetPos.x,
				// direction.deltaY + targetPos.y);
				// mapPanel.slide(targetPos, end, 100);
				// mapPanel.slide(end, targetPos, 100);
				// mapPanel.waitForAnimations();
			}
		}
	}

	private void drawMessages(TurnResult run) {
		for (MessageDisplayProperties msg : run.getMessages()) {
			messageDisplay.display(msg);
		}
		messageDisplay.draw();
	}

	private void drawStats() {
		Player player = game.getPlayer();

		statsPanel.put(0, 4, String.format("Health:%3d", player.getHealth().getCurrent()));
		statsPanel.put(0, 5, String.format("Energy:%3d", player.getEnergy().getCurrent()));

		statsPanel.refresh();
	}
}

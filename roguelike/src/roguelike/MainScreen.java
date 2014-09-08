package roguelike;

import java.awt.Font;
import java.awt.Rectangle;

import roguelike.actors.Actor;
import roguelike.maps.MapArea;
import roguelike.maps.Tile;
import roguelike.ui.DisplayManager;
import roguelike.ui.MainWindow;
import roguelike.ui.MessageDisplay;
import roguelike.ui.StatsDisplay;
import roguelike.ui.animations.AnimationManager;
import roguelike.ui.animations.AttackAnimation;
import roguelike.ui.windows.Dialog;
import roguelike.ui.windows.Terminal;
import roguelike.util.ArrayUtils;
import roguelike.util.Coordinate;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;
import squidpony.squidgrid.fov.FOVTranslator;
import squidpony.squidgrid.fov.TranslucenceWrapperFOV;
import squidpony.squidgrid.util.BasicRadiusStrategy;
import squidpony.squidgrid.util.DirectionIntercardinal;
import squidpony.squidgrid.util.RadiusStrategy;

public class MainScreen extends Screen {
	private static final String CHARS_USED = "#@.~M";

	private final FOVTranslator fov = new FOVTranslator(new TranslucenceWrapperFOV());
	private final RadiusStrategy radiusStrategy = BasicRadiusStrategy.CIRCLE;
	private Font screenFont;

	Terminal terminal;

	Game game;
	GameLoader gameLoader;
	MessageDisplay messageDisplay;
	StatsDisplay statsDisplay;
	DisplayManager displayManager;
	AnimationManager animationManager;

	public MainScreen(Terminal terminal) {
		this.terminal = terminal;

		/* used for FOV lighting */
		SColorFactory.addPallet("light",
				SColorFactory.asGradient(SColor.WHITE, SColor.DARK_SLATE_GRAY));

		animationManager = new AnimationManager();
		displayManager = DisplayManager.instance();
		screenFont = displayManager.screenFont();

		Terminal messageTerminal =
				terminal.getWindow(0, height - outputLines, width, outputLines);
		Terminal statsTerminal =
				terminal.getWindow(width - MainWindow.statWidth, 0, MainWindow.statWidth, height);

		messageDisplay = new MessageDisplay(messageTerminal, outputLines);
		statsDisplay = new StatsDisplay(statsTerminal);

		gameLoader = new GameLoader();

		game = gameLoader.newGame();
		statsDisplay.setPlayer(game.getPlayer());

		game.initialize();
	}

	@Override
	public long draw() {
		return drawFrame();
	}

	@Override
	public void process() {
		TurnResult run;
		run = game.processTurn();
		currentTurn = run;
	}

	@Override
	public Screen getScreen() {
		return this;
	}

	// TODO: get key update every iteration, but process it on a fixed rate,
	// i.e. processTurn(keyInput)
	/*
	 * private void initGamePanels() { if (titlePanel != null)
	 * frame.remove(titlePanel);
	 * 
	 * if (mainWinPanel == null) { mainWinPanel = new JPanel();
	 * mainWinPanel.setBackground(SColor.BLACK); mainWinPanel.setLayout(new
	 * BorderLayout());
	 * 
	 * Font font = displayManager.screenFont(); TextCellFactory textFactory2 =
	 * new TextCellFactory(font, cellWidth / 1, cellHeight, true, 0,
	 * CHARS_USED);
	 * 
	 * layeredPane = displayManager.displayPane();
	 * 
	 * mainWinPanel.add(layeredPane, BorderLayout.WEST);
	 * 
	 * statsPanel = new SwingPane(statWidth, height, textFactory2, null);
	 * statsPanel.setDefaultForeground(SColor.WHITE); statsPanel.refresh();
	 * mainWinPanel.add(statsPanel, BorderLayout.EAST);
	 * 
	 * outputPanel = new SwingPane(width + statsPanel.gridWidth(), outputLines,
	 * textFactory2, null); outputPanel.setDefaultForeground(SColor.AMARANTH);
	 * outputPanel.put(0, 0, "Output");
	 * 
	 * outputPanel.refresh(); mainWinPanel.add(outputPanel, BorderLayout.SOUTH);
	 * } frame.add(mainWinPanel); frame.pack(); }
	 */

	TurnResult currentTurn;

	private long drawFrame() {
		long start = System.currentTimeMillis();

		if (currentTurn == null)
			return 0;

		/*
		 * this will only refresh if player input has occurred or something has
		 * reset the dirty flag
		 */
		boolean animationProcessed = animationManager
				.nextFrame(displayManager.getTerminal());

		if (animationProcessed || animationManager.shouldRefresh()) {
			displayManager.setDirty();
			System.out.println("Set dirty flag");
		}

		if (currentTurn.playerActedThisTurn() || animationManager.shouldRefresh()) {
			if (!drawActiveWindow(currentTurn)) {
				drawMap();
			}
			drawStats();
		}
		drawMessages(currentTurn);
		drawEvents(currentTurn);

		displayManager.refresh();

		long end = System.currentTimeMillis();
		return end - start;
	}

	private void drawMap() {
		// if ((currentTurn != null && !currentTurn.playerActedThisTurn()) ||
		// animationManager.shouldRefresh())
		// return;

		MapArea currentMap = game.getCurrentMapArea();
		Rectangle screenArea = currentMap.getAreaInTiles(width, height, game
				.getPlayer().getPosition());

		doFOV(currentMap, screenArea, game.getPlayer().getPosition());

		for (int x = screenArea.x; x < screenArea.getMaxX(); x++) {
			for (int y = screenArea.y; y < screenArea.getMaxY(); y++) {
				Tile tile = currentMap.getTileAt(x, y);
				int screenX = x - screenArea.x;
				int screenY = y - screenArea.y;
				if (tile.isVisible()) {
					SColor color, bgColor;
					color = SColorFactory.lightWith(tile.getColor(),
							tile.getLightedColorValue());
					bgColor = SColorFactory.lightWith(tile.getBackground(),
							tile.getLightedColorValue());

					displayManager.getTerminal().withColor(color, bgColor)
							.put(screenX, screenY, tile.getSymbol());
				} else {
					displayManager.getTerminal()
							.withColor(tile.getColor(), tile.getBackground())
							.put(screenX, screenY, tile.getSymbol());

				}
			}
		}
	}

	/**
	 * Calculates the Field of View and marks the maps spots seen appropriately.
	 */
	private void doFOV(MapArea currentMap, Rectangle screenArea,
			Coordinate player) {
		boolean[][] walls = new boolean[width][height];
		float[][] lighting = new float[width][height];

		walls = ArrayUtils.getSubArray(currentMap.getWalls(), screenArea);
		lighting = ArrayUtils.getSubArray(currentMap.getLightValues(),
				screenArea);

		// for (int x = screenArea.x; x < screenArea.getMaxX(); x++) {
		// for (int y = screenArea.y; y < screenArea.getMaxY(); y++) {
		// Tile tile = currentMap.getTileAt(x, y);
		// if (tile != null) {
		// walls[x - screenArea.x][y - screenArea.y] = tile.isWall();
		// lighting[x - screenArea.x][y - screenArea.y] = tile.getLighting();
		// }
		// }
		// }

		float lightForce = game.getPlayer().getVisionRadius();
		float[][] incomingLight = fov.calculateFOV(lighting, player.x
				- screenArea.x, player.y - screenArea.y, 1f, 1 / lightForce,
				radiusStrategy);

		for (int x = screenArea.x; x < screenArea.getMaxX(); x++) {
			for (int y = screenArea.y; y < screenArea.getMaxY(); y++) {
				int cX = x - screenArea.x;
				int cY = y - screenArea.y;

				Tile tile = currentMap.getTileAt(x, y);
				tile.setVisible(fov.isLit(cX, cY));

				if (incomingLight[cX][cY] > 0) {
					float bright = 1 - incomingLight[cX][cY];
					tile.setLightedColorValue(SColorFactory.fromPallet("light",
							bright));
					// clean[x][y] = false;
				} else if (!tile.getLightedColorValue().equals(SColor.BLACK)) {
					tile.setLightedColorValue(SColor.BLACK);
					// clean[x][y] = false;
				}

			}
		}
	}

	private void drawEvents(TurnResult run) {
		Rectangle screenArea = game.getCurrentMapArea().getAreaInTiles(width,
				height, game.getPlayer().getPosition());
		for (TurnEvent event : run.getEvents()) {
			if (event.getType() == TurnEvent.ATTACKED) {
				Actor attacker = event.getInitiator();
				Actor target = event.getTarget();

				Coordinate attackerPos = attacker.getPosition();
				Coordinate targetPos = target.getPosition();
				Coordinate diff = attacker.getPosition().createOffsetPosition(
						-targetPos.x, -targetPos.y);
				DirectionIntercardinal direction = DirectionIntercardinal
						.getDirection(-diff.x, -diff.y);

				System.out.println(attacker.getName() + " attacks "
						+ target.getName() + " in direction "
						+ direction.symbol);

				if (screenArea.contains(attackerPos)
						&& screenArea.contains(targetPos)) {

					animationManager.addAnimation(new AttackAnimation(target,
							event.getMessage()));
					System.out.println("Added attack animation");

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

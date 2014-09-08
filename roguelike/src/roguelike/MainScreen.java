package roguelike;

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
	private final FOVTranslator fov = new FOVTranslator(new TranslucenceWrapperFOV());
	private final RadiusStrategy radiusStrategy = BasicRadiusStrategy.CIRCLE;

	Terminal terminal;

	private MainScreen nextScreen;
	Game game;
	GameLoader gameLoader;
	MessageDisplay messageDisplay;
	StatsDisplay statsDisplay;
	DisplayManager displayManager;
	AnimationManager animationManager;

	TurnResult currentTurn;

	public MainScreen(Terminal terminal) {
		this.terminal = terminal;
		this.nextScreen = this;

		/* used for FOV lighting */
		SColorFactory.addPallet("light",
				SColorFactory.asGradient(SColor.WHITE, SColor.DARK_SLATE_GRAY));

		animationManager = new AnimationManager();
		displayManager = DisplayManager.instance();

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

		drawMap();
		drawStats();
		displayManager.setDirty();
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
		return nextScreen;
	}

	private long drawFrame() {
		long start = System.currentTimeMillis();

		if (currentTurn == null) {
			System.out.println("No current turn yet");
			return 0;
		}

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
		Coordinate playerPosition = game.getPlayer().getPosition();

		Rectangle screenArea = currentMap
				.getAreaInTiles(width, height, playerPosition);

		doFOV(currentMap, screenArea, playerPosition);

		for (int x = screenArea.x; x < screenArea.getMaxX(); x++) {
			for (int y = screenArea.y; y < screenArea.getMaxY(); y++) {
				Tile tile = currentMap.getTileAt(x, y);
				int screenX = x - screenArea.x;
				int screenY = y - screenArea.y;
				if (tile.isVisible()) {
					SColor color, bgColor;
					SColor litColor = tile.getLightedColorValue();
					color = SColorFactory.lightWith(tile.getColor(), litColor);
					bgColor = SColorFactory.lightWith(tile.getBackground(), litColor);

					displayManager.getTerminal()
							.withColor(color, bgColor)
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
	private void doFOV(MapArea currentMap, Rectangle screenArea, Coordinate player) {
		// boolean[][] walls = new boolean[width][height];
		float[][] lighting = new float[width][height];

		// walls = ArrayUtils.getSubArray(currentMap.getWalls(), screenArea);
		lighting = ArrayUtils.getSubArray(currentMap.getLightValues(), screenArea);

		float lightForce = game.getPlayer().getVisionRadius();
		float[][] incomingLight = fov.calculateFOV(
				lighting,
				player.x - screenArea.x,
				player.y - screenArea.y,
				1f,
				1 / lightForce,
				radiusStrategy);

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
		Rectangle screenArea = game
				.getCurrentMapArea()
				.getAreaInTiles(width, height, game.getPlayer().getPosition());

		for (TurnEvent event : run.getEvents()) {
			if (event.getType() == TurnEvent.ATTACKED) {
				Actor attacker = event.getInitiator();
				Actor target = event.getTarget();

				Coordinate attackerPos = attacker.getPosition();
				Coordinate targetPos = target.getPosition();
				Coordinate diff = attacker
						.getPosition()
						.createOffsetPosition(-targetPos.x, -targetPos.y);

				DirectionIntercardinal direction = DirectionIntercardinal
						.getDirection(-diff.x, -diff.y);

				System.out.println(attacker.getName() + " attacks "
						+ target.getName() + " in direction "
						+ direction.symbol);

				if (screenArea.contains(attackerPos) && screenArea.contains(targetPos)) {

					animationManager.addAnimation(new AttackAnimation(target, event.getMessage()));
					System.out.println("Added attack animation");

				}
			}
		}
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

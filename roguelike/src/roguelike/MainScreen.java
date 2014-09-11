package roguelike;

import java.awt.Rectangle;

import roguelike.actors.Actor;
import roguelike.actors.AttackAttempt;
import roguelike.actors.Player;
import roguelike.maps.MapArea;
import roguelike.maps.Tile;
import roguelike.ui.DisplayManager;
import roguelike.ui.InputManager;
import roguelike.ui.MainWindow;
import roguelike.ui.MessageDisplay;
import roguelike.ui.StatsDisplay;
import roguelike.ui.animations.AnimationManager;
import roguelike.ui.animations.AttackAnimation;
import roguelike.ui.animations.AttackMissedAnimation;
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

	private final int windowWidth = width - MainWindow.statWidth;
	private final int windowHeight = height - outputLines;

	Terminal fullTerminal;
	Terminal terminal;

	Game game;
	GameLoader gameLoader;
	MessageDisplay messageDisplay;
	StatsDisplay statsDisplay;
	DisplayManager displayManager;
	AnimationManager animationManager;

	TurnResult currentTurn;

	public MainScreen(Terminal fullTerminal) {
		this.fullTerminal = fullTerminal;
		this.terminal = fullTerminal.getWindow(0, 0, windowWidth, windowHeight);
		this.nextScreen = this;

		/* used for FOV lighting */
		SColorFactory.addPallet("light",
				SColorFactory.asGradient(SColor.WHITE, SColor.DARK_SLATE_GRAY));

		animationManager = new AnimationManager();
		displayManager = DisplayManager.instance();

		Terminal messageTerminal =
				fullTerminal.getWindow(0, height - outputLines, width, outputLines);

		Terminal statsTerminal =
				fullTerminal.getWindow(width - MainWindow.statWidth, 0, MainWindow.statWidth, height);

		messageDisplay = new MessageDisplay(messageTerminal, outputLines);
		statsDisplay = new StatsDisplay(statsTerminal);

		gameLoader = GameLoader.instance();

		game = gameLoader.newGame();
		statsDisplay.setPlayer(game.getPlayer());

		game.initialize();

		drawMap();
		drawStats();

		InputManager.setInputEnabled(true);
		displayManager.setDirty();
	}

	@Override
	public long draw() {
		return drawFrame();
	}

	@Override
	public void process() {

		if (game.isPlayerDead()) {
			System.out.println("You died");

			Player player = game.getPlayer();
			AttackAttempt killedBy = player.getLastAttackedBy();

			// KeyEvent nextKey = InputManager.nextKey();
			// if (nextKey != null) {
			System.out.println("Switching to game over screen");
			nextScreen = new PlayerDiedScreen(killedBy.getActor(), this.fullTerminal);
			// }
			// else {
			// System.out.println("waiting for input");
			// }

		} else {

			TurnResult run;
			run = game.processTurn();
			currentTurn = run;

			if (!run.isRunning()) {
				nextScreen = new TitleScreen(fullTerminal);
			}
		}
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
				.nextFrame(terminal);

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
				.getAreaInTiles(windowWidth, windowHeight, playerPosition);

		doFOV(currentMap, screenArea, playerPosition);

		for (int x = screenArea.x; x < screenArea.getMaxX(); x++) {
			for (int y = screenArea.y; y < screenArea.getMaxY(); y++) {
				Tile tile = currentMap.getTileAt(x, y);
				int screenX = x - screenArea.x;
				int screenY = y - screenArea.y;
				if (tile.isVisible()) {
					SColor color, bgColor;
					SColor litColor = tile.getLightedColorValue();
					if (tile.getColor() == null)
						throw new IllegalArgumentException("null tile color");
					if (litColor == null)
						throw new IllegalArgumentException("null lit color");
					color = SColorFactory.lightWith(tile.getColor(), litColor);
					bgColor = SColorFactory.lightWith(tile.getBackground(), litColor);

					terminal.withColor(color, bgColor)
							.put(screenX, screenY, tile.getSymbol());

				} else {
					terminal.withColor(tile.getColor(), tile.getBackground())
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
			Actor initiator = event.getInitiator();
			Actor target = event.getTarget();

			Coordinate initiatorPos = initiator.getPosition();
			Coordinate targetPos, diff;
			DirectionIntercardinal direction;

			switch (event.getType()) {

			case TurnEvent.ATTACKED:
				targetPos = target.getPosition();
				diff = initiator
						.getPosition()
						.createOffsetPosition(-targetPos.x, -targetPos.y);

				direction = DirectionIntercardinal
						.getDirection(-diff.x, -diff.y);

				System.out.println(initiator.getName() + " attacks "
						+ target.getName() + " in direction "
						+ direction.symbol);

				if (screenArea.contains(initiatorPos) && screenArea.contains(targetPos)) {

					animationManager.addAnimation(new AttackAnimation(target, event.getMessage()));
					System.out.println("Added attack animation");

				}
				break;

			case TurnEvent.ATTACK_MISSED:
				targetPos = target.getPosition();
				diff = initiator
						.getPosition()
						.createOffsetPosition(-targetPos.x, -targetPos.y);

				direction = DirectionIntercardinal
						.getDirection(-diff.x, -diff.y);

				if (screenArea.contains(initiatorPos) && screenArea.contains(targetPos)) {

					animationManager.addAnimation(new AttackMissedAnimation(target));
					System.out.println("Added attack missed animation");

				}
				break;
			}

		}
	}

	private boolean drawActiveWindow(TurnResult run) {
		Dialog window = run.getActiveWindow();
		if (window != null) {
			window.showInPane(terminal);
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

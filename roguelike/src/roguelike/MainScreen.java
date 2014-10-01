package roguelike;

import java.awt.Point;
import java.awt.Rectangle;

import roguelike.actors.Actor;
import roguelike.actors.AttackAttempt;
import roguelike.actors.Player;
import roguelike.maps.MapArea;
import roguelike.maps.Tile;
import roguelike.ui.DisplayManager;
import roguelike.ui.InputManager;
import roguelike.ui.LookDisplay;
import roguelike.ui.MainWindow;
import roguelike.ui.MessageDisplay;
import roguelike.ui.StatsDisplay;
import roguelike.ui.animations.AnimationManager;
import roguelike.ui.animations.AttackAnimation;
import roguelike.ui.animations.AttackMissedAnimation;
import roguelike.ui.animations.RangedAttackAnimation;
import roguelike.ui.windows.TerminalBase;
import roguelike.util.ArrayUtils;
import roguelike.util.Coordinate;
import roguelike.util.Log;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;
import squidpony.squidgrid.fov.FOVTranslator;
import squidpony.squidgrid.fov.TranslucenceWrapperFOV;
import squidpony.squidgrid.util.BasicRadiusStrategy;
import squidpony.squidgrid.util.DirectionIntercardinal;
import squidpony.squidgrid.util.RadiusStrategy;
import squidpony.squidutility.Pair;

public class MainScreen extends Screen {
	private final FOVTranslator fov = new FOVTranslator(new TranslucenceWrapperFOV());
	private final RadiusStrategy radiusStrategy = BasicRadiusStrategy.CIRCLE;

	private final int windowWidth = width - MainWindow.statWidth;
	private final int windowHeight = height - outputLines;

	TerminalBase fullTerminal;

	Game game;
	GameLoader gameLoader;
	MessageDisplay messageDisplay;
	StatsDisplay statsDisplay;
	LookDisplay lookDisplay;

	DisplayManager displayManager;
	AnimationManager animationManager;

	TurnResult currentTurn;

	public MainScreen(TerminalBase fullTerminal) {
		this(fullTerminal, null);
	}

	public MainScreen(TerminalBase fullTerminal, Game initialGame) {
		if (game == null) {
			this.game = GameLoader.instance().newGame();
		} else {
			this.game = initialGame;
		}

		game.initialize();

		this.fullTerminal = fullTerminal;
		this.terminal = fullTerminal.getWindow(0, 0, windowWidth, windowHeight);
		this.setNextScreen(this);

		Log.debug("Window tile size: " + windowWidth + "x" + windowHeight);

		/* used for FOV lighting */
		SColorFactory.addPallet("light",
				SColorFactory.asGradient(SColor.WHITE, SColor.DARK_SLATE_GRAY));

		animationManager = new AnimationManager();
		displayManager = DisplayManager.instance();

		TerminalBase messageTerminal =
				fullTerminal.getWindow(0, height - outputLines, width - MainWindow.statWidth, outputLines);

		TerminalBase statsTerminal =
				fullTerminal.getWindow(width - MainWindow.statWidth, 0, MainWindow.statWidth, height);

		messageDisplay = new MessageDisplay(Game.current().messages, messageTerminal, outputLines);
		statsDisplay = new StatsDisplay(statsTerminal);
		statsDisplay.setPlayer(game.getPlayer());

		Rectangle statsSize = statsTerminal.size();
		lookDisplay = new LookDisplay(statsTerminal.getWindow(statsSize.x + 2, statsSize.height - 23, statsSize.width - 4, 22), statsSize.width - 4, 22);

		doFOV();
		drawMap();
		drawStats();

		InputManager.setInputEnabled(true);
		// InputManager.previousKeyMap();
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

			System.out.println("Switching to game over screen");
			Actor killedByActor = null;
			if (killedBy != null)
				killedByActor = killedBy.getActor();

			setNextScreen(new PlayerDiedScreen(killedByActor, this.fullTerminal));

		} else {

			TurnResult run;
			run = game.processTurn();
			currentTurn = run;

			if (!run.isRunning()) {
				setNextScreen(new TitleScreen(fullTerminal));
			}

			/* recalculate FOV if player moved/acted */
			if (run.playerActedThisTurn()) {
				doFOV();
			}
		}
	}

	@Override
	public Screen getScreen() {
		return nextScreen();
	}

	private long drawFrame() {
		long start = System.currentTimeMillis();

		if (currentTurn == null) {
			return 0;
		}

		if (!drawActiveWindow(currentTurn)) {
			drawMap();
		}
		drawStats();
		drawMessages(currentTurn);
		drawEvents(currentTurn);

		drawCursor(currentTurn);

		drawLookDisplay(currentTurn);

		/*
		 * this will only refresh if player input has occurred or something has reset the dirty flag
		 */
		boolean animationProcessed = animationManager
				.nextFrame(terminal);

		if (animationProcessed || animationManager.shouldRefresh()) {
			displayManager.setDirty();
		}

		long end = System.currentTimeMillis();
		return end - start;
	}

	private void drawMap() {
		MapArea currentMap = game.getCurrentMapArea();
		Coordinate centerPosition = game.getCenterScreenPosition();

		Rectangle screenArea = currentMap
				.getVisibleAreaInTiles(windowWidth, windowHeight, centerPosition);

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
	private void doFOV() {
		MapArea currentMap = game.getCurrentMapArea();
		Coordinate centerPosition = game.getCenterScreenPosition();

		Rectangle screenArea = currentMap
				.getVisibleAreaInTiles(windowWidth, windowHeight, centerPosition);

		doFOV(currentMap, screenArea, centerPosition);
	}

	private void doFOV(MapArea currentMap, Rectangle screenArea, Coordinate player) {
		float[][] lighting = new float[width][height];

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

				} else if (!tile.getLightedColorValue().equals(SColor.BLACK)) {

					tile.setLightedColorValue(SColor.BLACK);
				}
			}
		}
	}

	private void drawEvents(TurnResult run) {
		if (run == null)
			return;

		Rectangle screenArea = game
				.getCurrentMapArea()
				.getVisibleAreaInTiles(windowWidth, windowHeight, game.getCenterScreenPosition());

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

				Log.debug(initiator.getName() + " attacks "
						+ target.getName() + " in direction "
						+ direction.symbol);

				if (screenArea.contains(initiatorPos) && screenArea.contains(targetPos)) {

					animationManager.addAnimation(new AttackAnimation(target, event.getMessage()));
					Log.debug("Added attack animation");

				}
				break;

			case TurnEvent.ATTACK_MISSED:
				targetPos = target.getPosition();
				if (screenArea.contains(initiatorPos) && screenArea.contains(targetPos)) {

					animationManager.addAnimation(new AttackMissedAnimation(target));
					Log.debug("Added attack missed animation");

				}
				break;

			case TurnEvent.RANGED_ATTACKED:
				targetPos = target.getPosition();
				if (screenArea.contains(initiatorPos) && screenArea.contains(targetPos)) {

					animationManager.addAnimation(new RangedAttackAnimation(initiator, target, event.getMessage()));
					Log.debug("Added attack animation");

				}
				break;
			}

		}
		// prevent processing multiple times
		run.getEvents().clear();
	}

	private void drawCursor(TurnResult run) {
		Cursor activeCursor = run.getCursor();
		if (activeCursor == null)
			return;

		MapArea currentMap = game.getCurrentMapArea();
		Coordinate centerPosition = game.getCenterScreenPosition();
		Rectangle screenArea = currentMap
				.getVisibleAreaInTiles(windowWidth, windowHeight, centerPosition);

		activeCursor.draw(terminal, screenArea);
		displayManager.setDirty();
	}

	private boolean drawActiveWindow(TurnResult run) {
		Dialog<?> window = run.getActiveWindow();
		if (window != null) {
			window.showInPane(window.showFullscreen() ? fullTerminal : terminal);
			window.draw();

			return true;
		}
		return false;
	}

	private void drawMessages(TurnResult run) {
		messageDisplay.draw();
	}

	private void drawStats() {
		statsDisplay.draw();
	}

	private void drawLookDisplay(TurnResult run) {
		Pair<Point, Boolean> p = run.getCurrentLook();
		if (p == null || p.getFirst() == null) {
			lookDisplay.erase();
			return;
		}
		lookDisplay.draw(game.getCurrentMapArea(), p.getFirst().x, p.getFirst().y, p.getSecond(), p.getSecond() ? "Looking at" : "On ground");
	}
}

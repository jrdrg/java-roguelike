package roguelike.screens;

import java.awt.Point;
import java.awt.Rectangle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import roguelike.Game;
import roguelike.GameLoader;
import roguelike.TurnEvent;
import roguelike.TurnResult;
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
import roguelike.ui.windows.TerminalBase;
import roguelike.util.ArrayUtils;
import roguelike.util.Coordinate;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;
import squidpony.squidgrid.fov.FOVTranslator;
import squidpony.squidgrid.fov.TranslucenceWrapperFOV;
import squidpony.squidgrid.util.BasicRadiusStrategy;
import squidpony.squidgrid.util.DirectionIntercardinal;
import squidpony.squidgrid.util.RadiusStrategy;
import squidpony.squidutility.Pair;

public class MainScreen extends Screen {
    private static final Logger LOG = LogManager.getLogger(MainScreen.class);

    private final static int windowWidth = WIDTH - MainWindow.STAT_WIDTH;
    private final static int windowHeight = HEIGHT;

    private final FOVTranslator fov = new FOVTranslator(new TranslucenceWrapperFOV());
    private final RadiusStrategy radiusStrategy = BasicRadiusStrategy.CIRCLE;

    TerminalBase windowTerminal;

    Game game;
    MessageDisplay messageDisplay;
    StatsDisplay statsDisplay;
    LookDisplay lookDisplay;

    DisplayManager displayManager;
    AnimationManager animationManager;

    TurnResult currentTurn;

    private Rectangle[] screenQuadrants = new Rectangle[4];

    public MainScreen(TerminalBase terminal, Game initialGame) {
        super(terminal);
        if (initialGame == null)
            throw new IllegalArgumentException("initialGame cannot be null");

        this.game = initialGame;

        int midX = windowWidth / 2;
        int midY = windowHeight / 2;

        screenQuadrants[0] = new Rectangle(0, 0, midX, midY);
        screenQuadrants[1] = new Rectangle(0, midY, midX, midY);
        screenQuadrants[2] = new Rectangle(midX, 0, midX, midY);
        screenQuadrants[3] = new Rectangle(midX, midY, midX, midY);

        game.initialize();

        this.windowTerminal = terminal.getWindow(0, 0, windowWidth, windowHeight);

        LOG.debug("Window tile size: {} x {}", windowWidth, windowHeight);

        /* used for FOV lighting */
        SColorFactory.addPallet("light", SColorFactory.asGradient(SColor.WHITE, SColor.DARK_SLATE_GRAY));

        animationManager = new AnimationManager();
        displayManager = DisplayManager.instance();

        int messageLines = 21;
        TerminalBase messageTerminal =
                terminal.getWindow(WIDTH - MainWindow.STAT_WIDTH + 1, messageLines - 1, MainWindow.STAT_WIDTH - 2, HEIGHT - messageLines);

        TerminalBase statsTerminal = terminal.getWindow(WIDTH - MainWindow.STAT_WIDTH, 0, MainWindow.STAT_WIDTH, HEIGHT);

        messageDisplay = new MessageDisplay(Game.current().messages(), messageTerminal, messageLines);
        statsDisplay = new StatsDisplay(statsTerminal);
        statsDisplay.setPlayer(game.getPlayer());

        Rectangle statsSize = statsTerminal.size();
        int lookWidth = Math.min(20, windowWidth - 4);
        int lookHeight = Math.min(20, windowHeight - 4);
        lookDisplay =
                new LookDisplay(statsTerminal.getWindow(statsSize.x + 2, statsSize.height - 23, statsSize.width - 4, 22), lookWidth, lookHeight);

        doFOV();
        drawMap();
        drawStats();

        InputManager.setInputEnabled(true);
        InputManager.previousKeyMap();
        displayManager.setDirty();
    }

    @Override
    public Rectangle getDrawableArea() {
        return new Rectangle(0, 0, windowWidth, windowHeight);
    }

    @Override
    public void onDraw() {
        drawFrame();
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

            setNextScreen(new PlayerDiedScreen(killedByActor, terminal), false);

        }
        else {

            TurnResult run;
            run = game.processTurn();
            currentTurn = run;

            if (!run.isRunning()) {
                GameLoader.save(this.game);
                LOG.debug("Saving game...");
                setNextScreen(new TitleScreen(terminal), false);
            }

            /* recalculate FOV if player moved/acted */
            if (run.playerActedThisTurn()) {
                doFOV();
            }
        }
    }

    private void drawFrame() {
        if (currentTurn == null) {
            return;
        }

        drawMap();

        drawStats();
        drawLookDisplay(currentTurn);
        drawMessages(currentTurn);
        drawEvents(currentTurn);

        /*
         * this will only refresh if player input has occurred or something has reset
         * the dirty flag
         */
        boolean animationProcessed = animationManager.nextFrame(terminal);

        if (animationProcessed || animationManager.shouldRefresh()) {
            displayManager.setDirty();
        }
    }

    private void drawMap() {
        MapArea currentMap = game.getCurrentMapArea();
        Coordinate centerPosition = game.getCenterScreenPosition();

        Rectangle screenArea = currentMap.getVisibleAreaInTiles(windowWidth, windowHeight, centerPosition);

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

                    terminal.withColor(color, bgColor).put(screenX, screenY, tile.getSymbol());

                }
                else {
                    terminal.withColor(tile.getColor(), tile.getBackground()).put(screenX, screenY, tile.getSymbol());

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

        Rectangle screenArea = currentMap.getVisibleAreaInTiles(windowWidth, windowHeight, centerPosition);

        doFOV(currentMap, screenArea, centerPosition);
    }

    private void doFOV(MapArea currentMap, Rectangle screenArea, Coordinate player) {
        float[][] lighting = new float[WIDTH][HEIGHT];

        lighting = ArrayUtils.getSubArray(currentMap.getLightValues(), screenArea);

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

                }
                else if (!tile.getLightedColorValue().equals(SColor.BLACK)) {

                    tile.setLightedColorValue(SColor.BLACK);
                }
            }
        }
    }

    private void drawEvents(TurnResult run) {
        if (run == null)
            return;

        Rectangle screenArea = game.getCurrentMapArea().getVisibleAreaInTiles(windowWidth, windowHeight, game.getCenterScreenPosition());

        for (TurnEvent event : run.getEvents()) {

            Actor initiator = event.getInitiator();
            Actor target = event.getTarget();

            Coordinate initiatorPos = initiator.getPosition();
            Coordinate targetPos, diff;
            DirectionIntercardinal direction;

            switch (event.getType()) {

            case TurnEvent.ATTACKED:
            case TurnEvent.ATTACK_MISSED:
                targetPos = target.getPosition();
                diff = initiator.getPosition().createOffsetPosition(-targetPos.x, -targetPos.y);

                direction = DirectionIntercardinal.getDirection(-diff.x, -diff.y);

                LOG.debug("{} attacks {} in direction {}", initiator.getName(), target.getName(), direction.symbol);

                if (shouldDisplayAnimation(initiatorPos, targetPos, screenArea, true)) {

                    animationManager.addAnimation(event.getAnimation());
                }
                break;

            case TurnEvent.RANGED_ATTACKED:
                targetPos = target.getPosition();
                if (shouldDisplayAnimation(initiatorPos, targetPos, screenArea, false)) { // only target needs to be
                                                                                          // visible here

                    animationManager.addAnimation(event.getAnimation());
                    LOG.debug("Added attack animation");

                }
                break;
            }

        }
        // prevent processing multiple times
        run.getEvents().clear();
    }

    private void drawMessages(TurnResult run) {
        messageDisplay.draw();
    }

    private void drawStats() {
        statsDisplay.draw();
    }

    private void drawLookDisplay(TurnResult run) {

        // TODO: make this based on the player's position instead of using a global
        // property on the Game object

        Pair<Point, Boolean> p = run.getCurrentLook();
        if (p == null || p.getFirst() == null) {
            // lookDisplay.erase();
            return;
        }

        int quadrantIdx = 0;
        Rectangle quadrant = screenQuadrants[0];
        Point player = game.getPlayer().getPosition();
        Point lookingAt = p.getFirst();
        while (quadrant.contains(player) || quadrant.contains(lookingAt)) {
            quadrantIdx++;
            quadrant = screenQuadrants[quadrantIdx];
        }
        TerminalBase term = this.terminal.getWindow(quadrant.x + 1, quadrant.y + 0, quadrant.width - 2, quadrant.height - 0);

        int height = lookDisplay.setTerminal(term).getHeight(game.getCurrentMapArea(), p.getFirst().x, p.getFirst().y, p.getSecond(),
                p.getSecond() ? "Looking at" : "On ground");
        lookDisplay.draw(height);
    }

    private boolean shouldDisplayAnimation(Point initiatorPos, Point targetPos, Rectangle screenArea, boolean initiatorMustBeVisible) {
        MapArea map = game.getCurrentMapArea();
        if (screenArea.contains(initiatorPos) && screenArea.contains(targetPos)) {
            return (map.isVisible(initiatorPos) || !initiatorMustBeVisible) && map.isVisible(targetPos);
        }
        return false;
    }
}

package roguelike;

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import roguelike.actions.Action;
import roguelike.actions.ActionResult;
import roguelike.actors.Actor;
import roguelike.actors.Energy;
import roguelike.actors.Player;
import roguelike.items.Inventory;
import roguelike.maps.MapArea;
import roguelike.ui.DisplayManager;
import roguelike.util.Coordinate;
import squidpony.squidcolor.SColor;
import squidpony.squidmath.RNG;

/**
 * Setting - sword and sorcery version of 17th century caribbean/pirate setting.
 * port towns, swashbucklers, black powder weapons, jungle temples, fanatical
 * cultists, lost treasures, etc
 * 
 * win condition - leaving the island alive with as much wealth as you can carry
 * 
 * @author john
 * 
 */
public class Game implements Serializable {
    private static final Logger LOG = LogManager.getLogger(Game.class);

    private static final long serialVersionUID = 1L;

    public final static int MAP_WIDTH = 83;
    public final static int MAP_HEIGHT = 43;

    private static Game currentGame;

    private RNG rng;
    private boolean running;
    private boolean playerDead;
    private Player player;
    private MapArea currentMapArea;
    private Queue<Action> queuedActions;
    private TurnResult currentTurnResult;

    Cursor activeCursor;
    MessageLog messages;

    /**
     * This should only be called by GameLoader
     * 
     * @param gameLoader
     */
    Game() {
        this.queuedActions = new LinkedList<Action>();
        this.rng = GameLoader.getRandom();

        currentGame = this;
        LOG.debug("Created Game");

        this.messages = new MessageLog();

        this.player = GameLoader.createPlayer();
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        currentGame = this;
    }

    /**
     * Always returns the current game
     * 
     * @return
     */
    public static Game current() {
        return currentGame;
    }

    public MessageLog messages() {
        return messages;
    }

    public RNG random() {
        return rng;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isPlayerDead() {
        if (playerDead) {
            playerDead = false;
            return true;
        }
        return false;
    }

    public Player getPlayer() {
        return player;
    }

    public Coordinate getCenterScreenPosition() {
        return player.position;
    }

    public MapArea getCurrentMapArea() {
        return currentMapArea;
    }

    public void setCurrentMapArea(MapArea mapArea) {
        if (mapArea == null)
            return;

        currentMapArea = mapArea;
    }

    public void initialize() {
        LOG.debug("Initializing Game");

        running = true;
    }

    public TurnResult processTurn() {
        if (running) {
            currentTurnResult = onProcessing();
            return currentTurnResult;
        }
        return TurnResult.reset(currentTurnResult, false);
    }

    public void stopGame() {
        running = false;
    }

    public void reset() {
        playerDead = true;
    }

    /**
     * Displays a message in the bottom pane of the UI
     * 
     * @param message
     */
    public void displayMessage(String message) {
        messages.add(message);
    }

    public void displayMessage(String message, SColor color) {
        messages.add(new MessageDisplayProperties(message, color));
    }

    public void addEvent(TurnEvent event) {
        currentTurnResult.addEvent(event);
    }

    public void setCurrentlyLookingAt(Point point) {
        setCurrentlyLookingAt(point, true);
    }

    public void setCurrentlyLookingAt(Point point, boolean drawActor) {
        currentTurnResult.setCurrentLook(point, drawActor);
    }

    /**
     * Processes one turn
     * 
     * @return
     */
    private TurnResult onProcessing() {
        TurnResult turnResult = null;

        turnResult = TurnResult.reset(currentTurnResult, running);
        currentTurnResult = turnResult;

        showItemsOnPlayerSquare();

        while (true) {

            while (!queuedActions.isEmpty()) {
                if (executeQueuedActions(turnResult) != null) {
                    return turnResult;
                }
            }

            while (queuedActions.isEmpty()) {
                if (getCurrentActions(turnResult) != null) {
                    return turnResult;
                }
            }

            if (playerDead)
                return turnResult;

        }
    }

    /**
     * Queues an action for the current actor
     * 
     * @param turnResult
     * @return
     */
    private TurnResult getCurrentActions(TurnResult turnResult) {
        Actor actor = currentMapArea.getCurrentActor();

        while (!actor.isAlive()) {
            currentMapArea.nextActor("Attempting to act on dead actor: " + actor.getName());
            actor = currentMapArea.getCurrentActor();
        }

        int speed = actor.effectiveSpeed(currentMapArea);
        Energy energy = actor.energy();

        if (energy.canAct() || energy.increase(speed)) {
            Action action = actor.getNextAction();
            if (action != null) {
                queuedActions.add(action);
            }
            else {
                return turnResult;
            }
        }
        else { // advance to next actor
            currentMapArea.nextActor("getCurrentActions, !canAct, queueSize=" + queuedActions.size());

            if (Player.isPlayer(actor)) {
                // TODO: process things that happen every turn after player queues actions
                Game.currentGame.currentMapArea.spawnMonsters();
                LOG.debug("Game: Queue length: {}", queuedActions.size());
            }
        }

        return null;
    }

    /**
     * Executes the current action in the queue
     * 
     * @param turnResult
     * @return
     */
    private TurnResult executeQueuedActions(TurnResult turnResult) {
        Action currentAction = queuedActions.remove();

        // don't perform the action if the actor is dead
        if (!currentAction.getActor().isAlive()) {
            currentMapArea.nextActor("executeQueuedActions, currentAction actor !isAlive: " + currentAction.getActor().getName());
            return turnResult;
        }

        ActionResult result = currentAction.perform();
        messages.add(result.getMessage());

        /*
         * if the result is completed we can proceed, else put it back on the queue
         */
        if (result.isCompleted()) {

            while (result.getAlternateAction() != null) {
                Action alternate = result.getAlternateAction();
                result = alternate.perform();
                messages.add(result.getMessage());

                if (!result.isCompleted())
                    queuedActions.add(alternate);
            }

            Actor currentActor = currentAction.getActor();
            if (currentActor != null && !currentActor.energy().canAct()) {

                if (result.isSuccess()) {
                    currentActor.finishTurn();
                    DisplayManager.instance().setDirty(); // make sure we show the result of the action
                }
                else {
                    currentMapArea.nextActor("executeQueuedActions, !currentActor.canAct && !success");
                    return turnResult;
                }

            }
            else {
                LOG.warn("Game: Actor = {} Alive = {} Action = {}", currentActor.getName(), currentActor.isAlive(), currentAction);
                LOG.warn("Game: Remaining energy: {} Result = {}", currentActor.energy().getCurrent(), result);
                LOG.warn("Game: M = {} S = {} C = {}", result.getMessage(), result.isSuccess(), result.isCompleted());

                /*
                 * bug fix for infinite loop with enemy pathfinding where they can't move to a
                 * square they want to and fail the walk action
                 */
                if (!result.isSuccess())
                    currentMapArea.nextActor("executeQueueActions, can act but not success");

                return turnResult;
            }

        }
        else { // incomplete action

            queuedActions.add(currentAction);
        }

        /* return when player's actions are performed so we can redraw */
        if (Player.isPlayer(currentAction.getActor())) {
            turnResult.playerActed();
            return turnResult;
        }

        return null;
    }

    private void showItemsOnPlayerSquare() {
        if (currentTurnResult.getCurrentLook().getFirst() != null)
            return;

        Coordinate playerPos = player.getPosition();
        Inventory inventory = currentMapArea.getItemsAt(playerPos.x, playerPos.y);
        if (inventory != null && inventory.any()) {
            setCurrentlyLookingAt(playerPos, false);
        }
    }
}

package roguelike;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import roguelike.actions.Action;
import roguelike.actions.ActionResult;
import roguelike.actors.Actor;
import roguelike.actors.Energy;
import roguelike.actors.Player;
import roguelike.data.DataFactory;
import roguelike.maps.MapArea;
import roguelike.ui.DisplayManager;
import roguelike.util.Coordinate;
import roguelike.util.Log;
import squidpony.squidcolor.SColor;
import squidpony.squidmath.RNG;

/**
 * Setting - sword and sorcery version of 17th century caribbean/pirate setting. port towns, swashbucklers, black powder
 * weapons, jungle temples, fanatical cultists, lost treasures, etc
 * 
 * win condition - leaving the island alive with as much wealth as you can carry
 * 
 * @author john
 * 
 */
public class Game {
	private static Game currentGame;

	private RNG rng;
	private boolean running;
	private boolean playerDead;
	private MapArea currentMapArea;
	private Player player;
	private Queue<Action> queuedActions;
	private TurnResult currentTurnResult;

	Stack<Dialog<?>> windows;
	Dialog<?> activeWindow;
	Cursor activeCursor;

	final DataFactory dataFactory;

	/**
	 * This should only be called by GameLoader
	 * 
	 * @param gameLoader
	 */
	Game() {
		GameLoader gameLoader = GameLoader.instance();
		this.player = gameLoader.createPlayer();

		this.queuedActions = new LinkedList<Action>();
		this.rng = gameLoader.getRandom();
		this.windows = new Stack<Dialog<?>>();

		currentGame = this;
		Log.debug("Created Game");

		this.dataFactory = gameLoader.dataFactory;
	}

	/**
	 * Always returns the current game
	 * 
	 * @return
	 */
	public static Game current() {
		return currentGame;
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
		return player.getPosition();
	}

	public MapArea getCurrentMapArea() {
		return currentMapArea;
	}

	public void setCurrentMapArea(MapArea mapArea) {
		if (mapArea == null)
			return;

		currentMapArea = mapArea;

		// TODO: set player position somewhere besides 1,1
		// this.player.setPosition(1, 1);
	}

	public void initialize() {
		Log.debug("Initializing Game");

		running = true;
	}

	public TurnResult processTurn() {
		if (running) {
			currentTurnResult = onProcessing();
			return currentTurnResult;
		}
		return new TurnResult(false);
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
		currentTurnResult.addMessage(message);
		Log.debug("> " + message);
	}

	public void displayMessage(String message, SColor color) {
		currentTurnResult.addMessage(message, color);
	}

	public void addEvent(TurnEvent event) {
		currentTurnResult.addEvent(event);
	}

	@Deprecated
	public void waitingForAction(boolean waiting) {
		currentTurnResult.setNeedsInput(waiting);
	}

	/**
	 * Sets the currently active dialog window. If there was an active window prior to this call, it gets pushed on the
	 * stack. Passing null to this method will cause the active dialog to be set to the next value in the stack, or null
	 * if it's empty.
	 * 
	 * @param dialog
	 *            A window to make active, or null to close the current one.
	 * @return
	 */
	boolean setActiveDialog(Dialog<?> dialog) {
		if (activeWindow != null && activeWindow.equals(dialog))
			return false;

		if (dialog != null) {
			if (activeWindow != null)
				windows.push(activeWindow);

			activeWindow = dialog;
		}
		else {
			activeWindow = windows.size() > 0 ? windows.pop() : null;
		}
		return true;
	}

	/**
	 * Sets the current cursor, which will be drawn until it's set to null.
	 * 
	 * @param cursor
	 */
	void setCursor(Cursor cursor) {
		activeCursor = cursor;
	}

	/**
	 * Processes one turn
	 * 
	 * @return
	 */
	private TurnResult onProcessing() {
		TurnResult turnResult = null;

		if (activeWindow != null) {
			turnResult = processActiveWindow();

		} else if (activeCursor != null) {
			turnResult = processCursor();

		}
		if (turnResult != null)
			return turnResult;

		turnResult = new TurnResult(running);
		currentTurnResult = turnResult;

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
	 * Processes the active window, if there is one. If this method returns null, it means that the active window has
	 * received some input and has returned a result.
	 * 
	 * @return The current turn result if there is an active window waiting for input, null otherwise.
	 */
	private TurnResult processActiveWindow() {
		if (activeWindow.waitingForResult()) {
			if (!activeWindow.process())
				return currentTurnResult;
		}
		return null;
	}

	/**
	 * Processes the active cursor, if there is one. If this method returns null, it means that the player has either
	 * selected a tile or has canceled the cursor.
	 * 
	 * @return The current turn result if there is an active cursor, null otherwise.
	 */
	private TurnResult processCursor() {
		if (activeCursor.waitingForResult()) {
			if (!activeCursor.process())
				return currentTurnResult;
		}
		return null;
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
		Energy energy = actor.getEnergy();

		if (energy.canAct() || energy.increase(speed)) {
			Action action = actor.getNextAction();
			if (action != null) {
				queuedActions.add(action);
			} else {
				return turnResult;
			}
		} else { // advance to next actor
			currentMapArea.nextActor("getCurrentActions, !canAct, queueSize=" + queuedActions.size());

			if (Player.isPlayer(actor)) {
				// TODO: process things that happen every turn after player queues actions
				Game.currentGame.currentMapArea.spawnMonsters();
				Log.debug("Game: Queue length: " + queuedActions.size());
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
		turnResult.addMessage(result.getMessage());

		/*
		 * if the result is completed we can proceed, else put it back on the queue
		 */
		if (result.isCompleted()) {

			while (result.getAlternateAction() != null) {
				result = result.getAlternateAction().perform();
				turnResult.addMessage(result.getMessage());
			}

			Actor currentActor = currentAction.getActor();
			if (currentActor != null && !currentActor.getEnergy().canAct()) {

				if (result.isSuccess()) {
					currentActor.finishTurn();
					DisplayManager.instance().setDirty(); // make sure we show the result of the action
				} else {
					currentMapArea.nextActor("executeQueuedActions, !currentActor.canAct && !success");
					return turnResult;
				}

			} else {

				Log.warning("Game: Actor=" + currentActor.getName());
				Log.warning("Game: Energy=" + currentActor.isAlive());
				Log.warning("Game: Remaining energy: " + currentActor.getEnergy().getCurrent() + " Result=" + result);
				Log.warning("Game: " + result.getMessage() + ", " + result.isSuccess() + ", " + result.isCompleted());
			}

		} else { // incomplete action

			queuedActions.add(currentAction);
		}

		/* return when player's actions are performed so we can redraw */
		if (Player.isPlayer(currentAction.getActor())) {
			turnResult.playerActed();
			return turnResult;
		}

		return null;
	}
}

package roguelike;

import java.util.LinkedList;
import java.util.Queue;

import roguelike.actions.Action;
import roguelike.actions.ActionResult;
import roguelike.actors.Actor;
import roguelike.actors.Energy;
import roguelike.actors.Player;
import roguelike.data.DataFactory;
import roguelike.maps.MapArea;
import roguelike.ui.Cursor;
import roguelike.ui.DisplayManager;
import roguelike.ui.windows.Dialog;
import roguelike.util.Coordinate;
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

	final DataFactory dataFactory;

	/**
	 * This should only be called by GameLoader
	 * 
	 * @param gameLoader
	 */
	Game() {
		GameLoader gameLoader = GameLoader.instance();
		this.player = gameLoader.createPlayer();

		queuedActions = new LinkedList<Action>();
		rng = gameLoader.getRandom();
		currentGame = this;
		System.out.println("Created Game");

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

	public void setCursor(Cursor cursor) {
		currentTurnResult.setCursor(cursor);
	}

	public void initialize() {
		System.out.println("Initializing Game");

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
		System.out.println("> " + message);
	}

	public void displayMessage(String message, SColor color) {
		currentTurnResult.addMessage(message, color);
	}

	public void addEvent(TurnEvent event) {
		currentTurnResult.addEvent(event);
	}

	public void setActiveDialog(Dialog dialog) {
		currentTurnResult.setWindow(dialog);
	}

	public void waitingForAction(boolean waiting) {
		currentTurnResult.setNeedsInput(waiting);
	}

	/**
	 * Processes one turn
	 * 
	 * @return
	 */
	private TurnResult onProcessing() {
		TurnResult turnResult;
		// if (currentTurnResult != null && currentTurnResult.isInputRequired())
		// {
		// turnResult = currentTurnResult;
		// } else
		{
			turnResult = new TurnResult(running);
			if (currentTurnResult != null && currentTurnResult.activeWindow != null) {

				turnResult.setWindow(currentTurnResult.getActiveWindow());
			}
		}
		currentTurnResult = turnResult;

		while (true) {

			Actor nextActor = currentMapArea.peekNextActor();

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

			if (nextActor != null && Player.isPlayer(nextActor)) {
				System.out.println("nextactor=player");
				return turnResult;
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

		int speed = actor.getStatistics().speed.getTotalValue();
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
				System.out.println("Queue length: " + queuedActions.size());
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
			// clear any dialogs
			turnResult.setWindow(null);

			while (result.getAlternateAction() != null) {
				result = result.getAlternateAction().perform();
				turnResult.addMessage(result.getMessage());
			}

			Actor currentActor = currentAction.getActor();
			if (currentActor != null && !currentActor.getEnergy().canAct()) {

				if (result.isSuccess()) {
					currentActor.finishTurn();
				} else {
					currentMapArea.nextActor("executeQueuedActions, !currentActor.canAct");
					return turnResult;
				}

			} else {

				System.out.println("Actor=" + currentActor.getName());
				System.out.println("Energy=" + currentActor.isAlive());
				System.out.println("Remaining energy: " + currentActor.getEnergy().getCurrent() + " Result=" + result);
				System.out.println(result.getMessage() + ", " + result.isSuccess() + ", " + result.isCompleted());
			}

		} else { // incomplete action

			queuedActions.add(currentAction);
		}

		/* return when player's actions are performed so we can redraw */
		if (Player.isPlayer(currentAction.getActor())) {
			turnResult.playerActed();
			return turnResult;
		}
		Actor nextActor = currentMapArea.peekNextActor();
		if (nextActor != null) {
			System.out.println("nextActor=" + nextActor.getName());
			if (Player.isPlayer(nextActor)) {
				System.out.println("################ next actor is player, returning");
				DisplayManager.instance().setDirty();
				return turnResult;
			}
		}

		return null;
	}
}

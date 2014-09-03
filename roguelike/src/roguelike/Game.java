package roguelike;

import java.util.LinkedList;
import java.util.Queue;

import roguelike.actions.Action;
import roguelike.actions.ActionResult;
import roguelike.actors.Actor;
import roguelike.actors.Energy;
import roguelike.actors.Player;
import roguelike.maps.MapArea;
import squidpony.squidcolor.SColor;

public class Game {

	private boolean isRunning;
	private boolean playerDead;
	private MapArea currentMapArea;
	private Player player;
	private Queue<Action> queuedActions;
	private TurnResult currentTurnResult;

	public Game(GameLoader gameLoader) {
		this.player = gameLoader.createPlayer(this);

		queuedActions = new LinkedList<Action>();
		System.out.println("Created Game");
	}

	public Player getPlayer() {
		return player;
	}

	public boolean getIsRunning() {
		return isRunning;
	}

	public boolean isPlayerDead() {
		if (playerDead) {
			playerDead = false;
			return true;
		}
		return false;
	}

	public MapArea getCurrentMapArea() {
		return currentMapArea;
	}

	public void setCurrentMapArea(MapArea mapArea) {
		if (mapArea == null)
			return;

		currentMapArea = mapArea;
		this.player.setPosition(1, 1);
	}

	public void initialize() {
		System.out.println("Initializing Game");

		isRunning = true;
	}

	public TurnResult processTurn() {
		if (isRunning) {
			currentTurnResult = onProcessing();
			return currentTurnResult;
		}
		return new TurnResult(false);
	}

	public void stopGame() {
		isRunning = false;
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

	/**
	 * Processes one turn
	 * 
	 * @return
	 */
	private TurnResult onProcessing() {
		TurnResult turnResult = new TurnResult(isRunning);
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
	 * Queues an action for the current actor
	 * 
	 * @param turnResult
	 * @return
	 */
	private TurnResult getCurrentActions(TurnResult turnResult) {
		Actor actor = currentMapArea.getCurrentActor();

		while (!actor.isAlive()) {
			System.out.println("Attempting to act on dead actor: " + actor.getName());
			currentMapArea.nextActor();
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
		} else {
			// advance to next actor
			currentMapArea.nextActor();
		}

		if (Player.isPlayer(actor)) {
			// TODO: process things that happen every turn after player
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
			currentMapArea.nextActor();
			System.out.println(" -> actor " + currentAction.getActor().getName() + " is dead, skipping");
			return turnResult;
		}

		ActionResult result = currentAction.perform();
		turnResult.addMessage(result.getMessage());

		/*
		 * if the result is completed we can proceed, else put it back on the
		 * queue
		 */
		if (result.isCompleted()) {
			while (result.getAlternateAction() != null) {
				result = result.getAlternateAction().perform();
				turnResult.addMessage(result.getMessage());
			}

			Actor currentActor = currentAction.getActor();
			if (currentActor != null) {// && result.isSuccess()) {
				currentActor.finishTurn();
				currentMapArea.nextActor();
			}

		} else {
			System.out.println("Incomplete action, re-adding on queue...");
			queuedActions.add(currentAction);
		}

		/* return when player's actions are performed so we can redraw */
		if (Player.isPlayer(currentAction.getActor())) {
			return turnResult;
		}

		return null;
	}
}

package roguelike.actors;

import roguelike.Game;
import roguelike.actions.Action;
import roguelike.actors.behaviors.Behavior;
import roguelike.actors.behaviors.PlayerInputBehavior;
import squidpony.squidcolor.SColor;

public class Player extends Actor {

	private Behavior behavior;

	public Player(Game game) {
		super('@', SColor.WHITE, game);
		// TODO: load these during character creation somewhere
		this.getStatistics().speed.setBase(20);

		this.behavior = new PlayerInputBehavior(this);
	}

	public static boolean isPlayer(Actor actor) {
		return actor instanceof Player;
	}

	@Override
	public Action getNextAction() {
		Action action = behavior.getAction();
		if (action == null) {
			game.waitingForAction(true);
		} else {
			game.waitingForAction(false);
		}
		return action;
	}

	@Override
	public String getName() {
		return "Player";
	}

	@Override
	public String getDescription() {
		return "The player";
	}

	@Override
	public int getVisionRadius() {
		return 35;
	}

	@Override
	public void onAttacked(Actor attacker) {
	}

	@Override
	public void onKilled() {
		// back to title screen
	}
}

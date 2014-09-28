package roguelike.actors;

import roguelike.actions.Action;
import roguelike.actors.behaviors.Behavior;
import roguelike.actors.behaviors.PlayerInputBehavior;
import roguelike.util.Log;
import squidpony.squidcolor.SColor;

public class Player extends Actor {

	private Behavior behavior;

	public Player() {
		super('@', SColor.WHITE);
		// TODO: load these during character creation somewhere
		this.statistics().speed.setBase(20);

		this.behavior = new PlayerInputBehavior(this);
	}

	public static boolean isPlayer(Actor actor) {
		return actor instanceof Player;
	}

	@Override
	public Action getNextAction() {
		Action action = behavior.getAction();
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
	public void onAttackedInternal(Actor attacker) {
		Log.verboseDebug("Player attacked by " + attacker.getName());
		Log.verboseDebug("AttackedBy count=" + attackedBy.size());
	}

	@Override
	public void onKilled() {
		// back to title screen
	}
}

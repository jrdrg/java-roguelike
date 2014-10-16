package roguelike.actors.behaviors;

import roguelike.actions.Action;
import roguelike.actors.Actor;

public abstract class Behavior {

	protected Actor actor;

	protected Behavior(Actor actor) {
		this.actor = actor;
	}

	public abstract boolean isHostile();

	public void onNoAmmunition() {
	}

	public void onAttacked(Actor attacker) {
	}

	public abstract Action getAction();

	/**
	 * Allows the actor to change behaviors based on some criteria
	 * 
	 * @return
	 */
	public abstract Behavior getNextBehavior();

	public abstract String getDescription();
}

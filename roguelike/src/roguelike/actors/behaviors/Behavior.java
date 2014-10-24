package roguelike.actors.behaviors;

import java.io.Serializable;

import roguelike.actions.Action;
import roguelike.actors.Actor;

public abstract class Behavior implements Serializable {
	private static final long serialVersionUID = 1L;

	protected Actor actor;

	protected Behavior(Actor actor) {
		if (actor == null)
			throw new IllegalArgumentException("actor cannot be null");

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

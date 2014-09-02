package roguelike.actions;

import roguelike.actors.Actor;

public abstract class Action {

	protected Actor actor;
	protected boolean usesEnergy;

	protected Action(Actor actor) {
		this.actor = actor;
		this.usesEnergy = true;
	}

	public Actor getActor() {
		return actor;
	}

	public ActionResult perform() {
		ActionResult result = onPerform();

		if (result.success && this.usesEnergy)
			actor.getEnergy().act();

		return result;
	}

	protected abstract ActionResult onPerform();

	protected int getEnergyCost() {
		return 0;
	}
}

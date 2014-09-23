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

	public final ActionResult perform() {
		if (checkForIncomplete())
			return ActionResult.incomplete();

		ActionResult result = onPerform();

		if (result.success && this.usesEnergy)
			actor.getEnergy().act();

		return result;
	}

	protected boolean checkForIncomplete() {
		return false;
	}

	protected abstract ActionResult onPerform();
}

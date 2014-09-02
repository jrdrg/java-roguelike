package roguelike.actions;

import roguelike.actors.Actor;

public class CloseDoorAction extends Action {

	public CloseDoorAction(Actor actor) {
		super(actor);
	}

	@Override
	protected ActionResult onPerform() {
		// TODO: implement close door
		return ActionResult.failure();
	}

}

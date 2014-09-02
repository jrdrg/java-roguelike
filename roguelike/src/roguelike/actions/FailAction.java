package roguelike.actions;

import roguelike.actors.Actor;

public class FailAction extends Action {

	public FailAction(Actor actor) {
		super(actor);
	}

	@Override
	public ActionResult onPerform() {
		return ActionResult.failure();
	}
}

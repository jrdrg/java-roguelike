package roguelike.actions;

import roguelike.actors.Actor;

public class WaitAction extends Action {

	public WaitAction(Actor actor) {
		super(actor);
	}

	@Override
	protected ActionResult onPerform() {
		return ActionResult.success();
	}

}

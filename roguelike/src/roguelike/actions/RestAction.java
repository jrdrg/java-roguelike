package roguelike.actions;

import roguelike.actors.Actor;

public class RestAction extends Action {

	public RestAction(Actor actor) {
		super(actor);
	}

	@Override
	protected ActionResult onPerform() {
		actor.getHealth().heal(10);
		return ActionResult.success();
	}

}

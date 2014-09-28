package roguelike.actions;

import roguelike.actors.Actor;

public class RestAction extends Action {

	public RestAction(Actor actor) {
		super(actor);
	}

	@Override
	protected ActionResult onPerform() {
		// actor.health().heal(1);

		if (actor.health().damage(1))
			actor.dead();

		return ActionResult.success();
	}

}

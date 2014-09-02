package roguelike.actions;

import roguelike.actors.Actor;

public class QuitAction extends Action {

	public QuitAction(Actor actor) {
		super(actor);
		this.usesEnergy = false;
	}

	@Override
	public ActionResult onPerform() {
		actor.getGame().stopGame();
		return ActionResult.success();
	}

}

package roguelike.actions;

import roguelike.Game;
import roguelike.actors.Actor;

public class QuitAction extends Action {

	public QuitAction(Actor actor) {
		super(actor);
		this.usesEnergy = false;
	}

	@Override
	public ActionResult onPerform() {
		Game.current().stopGame();
		return ActionResult.success();
	}

}

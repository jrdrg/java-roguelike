package roguelike.actions;

import roguelike.Cursor;
import roguelike.CursorResult;
import roguelike.Game;
import roguelike.actors.Actor;
import roguelike.maps.MapArea;
import roguelike.ui.InputCommand;
import roguelike.ui.windows.LookDialog;
import roguelike.util.Coordinate;

public class LookAction extends InputRequiredAction<InputCommand> {

	public LookAction(Actor actor, MapArea mapArea) {
		super(actor);
		this.usesEnergy = false;
		this.cursor = new Cursor(actor.getPosition(), mapArea);

		cursor.show();
	}

	@Override
	protected ActionResult onPerform() {

		if (dialog != null) {

			if (dialog.result() != null)
				return ActionResult.success();

		} else {

			CursorResult result = cursor.result();
			if (result.isCanceled())
				return ActionResult.failure().setMessage("Canceled look");

			if (!lookAt(result.position())) // nothing to look at, return
				return ActionResult.success();

		}
		return ActionResult.incomplete();
	}

	private boolean lookAt(Coordinate position) {
		int x = position.x;
		int y = position.y;
		Game game = Game.current();

		MapArea map = game.getCurrentMapArea();
		if (map.getActorAt(x, y) == null && !map.getItemsAt(x, y).any())
			return false;

		dialog = new LookDialog(game.getCurrentMapArea(), x, y);
		dialog.show();

		return true;
	}
}

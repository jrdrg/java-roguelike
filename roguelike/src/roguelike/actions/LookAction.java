package roguelike.actions;

import roguelike.Game;
import roguelike.actors.Actor;
import roguelike.maps.MapArea;
import roguelike.ui.Cursor;
import roguelike.ui.InputCommand;
import roguelike.ui.InputManager;
import roguelike.ui.windows.LookDialog;
import roguelike.util.Coordinate;
import squidpony.squidgrid.util.DirectionIntercardinal;

public class LookAction extends Action {

	private MapArea mapArea;
	private Cursor lookCursor;
	private LookDialog lookDialog;

	public LookAction(Actor actor, MapArea mapArea) {
		super(actor);
		this.mapArea = mapArea;
		this.usesEnergy = false;
		this.lookCursor = new Cursor(actor.getPosition());
	}

	@Override
	protected ActionResult onPerform() {

		if (lookDialog != null) {
			Game.current().setActiveDialog(lookDialog);

			if (lookDialog.shouldClose())
				return ActionResult.success();

		} else {
			Game.current().setCursor(lookCursor);

			InputCommand cmd = InputManager.nextCommand();
			if (cmd != null) {
				DirectionIntercardinal direction = cmd.toDirection();
				if (direction != DirectionIntercardinal.NONE) {

					Coordinate newPosition = lookCursor.getPosition().createOffsetPosition(direction);

					if (mapArea.isWithinBounds(newPosition.x, newPosition.y)) {
						lookCursor.setPosition(newPosition);
					}

				} else {
					switch (cmd) {
					case CONFIRM:
						// look at whatever's under the cursor
						Game.current().setCursor(null);
						if (!lookAt())
							return ActionResult.success();

						break;

					case CANCEL:
						Game.current().setCursor(null);
						return ActionResult.failure().setMessage("Canceled look");

					default:
					}
				}
			}
		}
		return ActionResult.incomplete();
	}

	private boolean lookAt() {
		int x = lookCursor.getPosition().x;
		int y = lookCursor.getPosition().y;
		Game game = Game.current();

		MapArea map = game.getCurrentMapArea();
		if (map.getActorAt(x, y) == null && !map.getItemsAt(x, y).any())
			return false;

		lookDialog = new LookDialog(game.getCurrentMapArea(), x, y);
		game.setActiveDialog(lookDialog);
		return true;
	}
}

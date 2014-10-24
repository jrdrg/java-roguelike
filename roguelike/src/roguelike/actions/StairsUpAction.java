package roguelike.actions;

import java.awt.Point;

import roguelike.actors.Actor;
import roguelike.maps.MapArea;
import roguelike.maps.Stairs;
import roguelike.maps.Tile;

public class StairsUpAction extends Action {

	private MapArea map;

	public StairsUpAction(Actor actor, MapArea map) {
		super(actor);
		this.map = map;
	}

	@Override
	protected ActionResult onPerform() {
		Point actorPosition = actor.getPosition();
		Tile t = map.getTileAt(actorPosition);

		if (!(t instanceof Stairs))
			return ActionResult.failure().setMessage("No stairs");

		Stairs stairs = (Stairs) t;
		if (!stairs.isDown()) {

			stairs.use();
			return ActionResult.success().setMessage(actor.doAction("walks up the stairs."));
		} else {

			return ActionResult.failure().setMessage("No stairs up.");
		}
	}

}

package roguelike.actions;

import java.awt.Point;

import roguelike.actors.Actor;
import roguelike.maps.MapArea;
import roguelike.maps.Stairs;
import roguelike.maps.Tile;

public class StairsDownAction extends Action {

	private MapArea map;

	public StairsDownAction(Actor actor, MapArea map) {
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
		stairs.use();

		return ActionResult.success().setMessage(actor.doAction("walks down the stairs."));
	}

}

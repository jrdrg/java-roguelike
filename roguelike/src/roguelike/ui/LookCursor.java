package roguelike.ui;

import roguelike.Cursor;
import roguelike.Game;
import roguelike.maps.MapArea;
import roguelike.util.Coordinate;

public class LookCursor extends Cursor {

	public LookCursor(Coordinate initialPosition, MapArea mapArea) {
		super(initialPosition, mapArea);
		setCurrentLookPoint(initialPosition);
	}

	@Override
	protected boolean onUpdatePosition(Coordinate position) {
		setCurrentLookPoint(position);

		return true;
	}

	private void setCurrentLookPoint(Coordinate position) {
		// TODO: make sure that only visible tiles can be looked at
		if (mapArea.getActorAt(position.x, position.y) != null)
			Game.current().setCurrentlyLookingAt(position);
		else if (mapArea.getItemsAt(position.x, position.y).any())
			Game.current().setCurrentlyLookingAt(position);
		else
			Game.current().setCurrentlyLookingAt(null);
	}
}

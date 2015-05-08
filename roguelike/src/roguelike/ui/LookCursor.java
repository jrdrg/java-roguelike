package roguelike.ui;

import roguelike.Cursor;
import roguelike.maps.MapArea;
import roguelike.screens.LookScreen;
import roguelike.util.Coordinate;

public class LookCursor extends Cursor {

	private LookScreen lookScreen;

	public LookCursor(Coordinate initialPosition, MapArea mapArea) {
		super(initialPosition, mapArea);
	}

	public void setLookScreen(LookScreen lookScreen) {
		this.lookScreen = lookScreen;
		setCurrentLookPoint(position);
	}

	@Override
	protected boolean onUpdatePosition(Coordinate position) {
		setCurrentLookPoint(position);

		return true;
	}

	private void setCurrentLookPoint(Coordinate position) {
		// Make sure that only visible tiles can be looked at
		if (!mapArea.getTileAt(position).isVisible())
		{
			lookScreen.lookAt(mapArea, null);
			return;
		}

		if (mapArea.getActorAt(position.x, position.y) != null) {
			lookScreen.lookAt(mapArea, position);
			return;
		}
		else if (mapArea.getItemsAt(position.x, position.y).any()) {
			lookScreen.lookAt(mapArea, position);
			return;
		}
		lookScreen.lookAt(mapArea, null);
	}
}

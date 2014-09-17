package roguelike.maps;

public class Door extends Tile {

	private static final long serialVersionUID = -5705303883628916040L;

	private boolean isOpen;

	public void open(MapArea map) {
		if (!isOpen) {
			isOpen = true;
			isPassable = true;
			wall = false;
			lighting = 0f;

			map.updateValues(); // visibility changed, update
		}
	}

	public void close(MapArea map) {
		if (isOpen) {
			isOpen = false;
			isPassable = false;
			wall = true;
			lighting = 1f;

			map.updateValues(); // visibility changed, update
		}
	}

	@Override
	public char getSymbol() {
		if (getActor() != null && visible) {
			return getActor().getSymbol();
		}

		return isOpen ? '/' : symbol;
	}
}

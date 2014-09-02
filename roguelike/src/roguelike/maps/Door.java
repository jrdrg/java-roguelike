package roguelike.maps;

public class Door extends Tile {

	private boolean isOpen;

	public void open() {
		if (!isOpen) {
			isOpen = true;
			isPassable = true;
			wall = false;
			lighting = 0f;
		}
	}

	public void close() {
		if (isOpen) {
			isOpen = false;
			isPassable = false;
			wall = true;
			lighting = 1f;
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

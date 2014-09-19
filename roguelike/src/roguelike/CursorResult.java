package roguelike;

import roguelike.util.Coordinate;

public class CursorResult {
	private Coordinate position;
	private boolean canceled;

	public CursorResult(Coordinate position, boolean canceled) {
		this.position = position;
		this.canceled = canceled;
	}

	public Coordinate position() {
		return position;
	}

	public boolean isCanceled() {
		return canceled;
	}
}

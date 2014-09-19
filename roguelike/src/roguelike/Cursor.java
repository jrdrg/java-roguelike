package roguelike;

import java.awt.Rectangle;

import roguelike.maps.MapArea;
import roguelike.ui.InputCommand;
import roguelike.ui.InputManager;
import roguelike.ui.windows.TerminalBase;
import roguelike.util.Coordinate;
import squidpony.squidcolor.SColor;
import squidpony.squidgrid.util.DirectionIntercardinal;

public class Cursor {

	private boolean isActive;

	protected char symbol;
	protected SColor color;
	protected Coordinate position;
	protected MapArea mapArea;

	protected CursorResult result;

	public Cursor(Coordinate initialPosition, MapArea mapArea) {
		this.mapArea = mapArea;
		this.symbol = '_';
		this.color = SColor.INDIGO_DYE;
		this.position = new Coordinate(initialPosition.x, initialPosition.y);
	}

	public final void draw(TerminalBase terminal, Rectangle screenArea) {
		if (terminal == null)
			throw new IllegalArgumentException("terminal cannot be null");

		if (!screenArea.contains(position)) {
			int px = (int) Math.max(screenArea.getMinX(), Math.min(position.x, screenArea.getMaxX() - 1));
			int py = (int) Math.max(screenArea.getMinY(), Math.min(position.y, screenArea.getMaxY() - 1));

			setPosition(px, py);
		}

		int sx = position.x - screenArea.x;
		int sy = position.y - screenArea.y;

		onDraw(terminal, sx, sy);
	}

	public final boolean waitingForResult() {
		return isActive;
	}

	public final void show() {
		Game.current().setCursor(this);
		isActive = true;
		onShow();
	}

	public final boolean process() {
		CursorResult processed = onProcess();

		if (processed != null) {
			isActive = false;
			this.result = processed;
			Game.current().setCursor(null);
		}
		return !isActive;
	}

	public final CursorResult result() {
		return result;
	}

	protected void onDraw(TerminalBase terminal, int sx, int sy) {
		terminal.withColor(SColor.TRANSPARENT, color).fill(sx, sy, 1, 1);
		MapArea mapArea = Game.current().getCurrentMapArea();
		if (!mapArea.getTileAt(position).isExplored())
			terminal.withColor(SColor.TRANSPARENT, color).put(sx, sy, ' ');
	}

	protected CursorResult onProcess() {
		InputCommand cmd = InputManager.nextCommand();
		if (cmd != null) {

			DirectionIntercardinal direction = cmd.toDirection();
			if (direction != DirectionIntercardinal.NONE) {

				Coordinate newPosition = position.createOffsetPosition(direction);
				if (mapArea.isWithinBounds(newPosition.x, newPosition.y)) {
					setPosition(newPosition);
				}

			} else {
				switch (cmd) {
				case CONFIRM:
					result = new CursorResult(position, false);
					break;

				case CANCEL:
					result = new CursorResult(position, true);
					break;

				default:
				}
			}
		}
		return result;
	}

	protected void onShow() {
	}

	private void setPosition(int x, int y) {
		position.x = x;
		position.y = y;
	}

	private void setPosition(Coordinate position) {
		this.position.x = position.x;
		this.position.y = position.y;
	}

}

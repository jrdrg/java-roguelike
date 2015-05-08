package roguelike;

import java.awt.Rectangle;

import roguelike.maps.MapArea;
import roguelike.ui.InputCommand;
import roguelike.ui.InputManager;
import roguelike.ui.windows.TerminalBase;
import roguelike.util.Coordinate;
import squidpony.squidcolor.SColor;
import squidpony.squidgrid.util.BasicRadiusStrategy;
import squidpony.squidgrid.util.DirectionIntercardinal;
import squidpony.squidgrid.util.RadiusStrategy;

public class Cursor {

	private boolean isActive;

	protected RadiusStrategy radiusStrategy = BasicRadiusStrategy.SQUARE;
	protected char symbol;
	protected SColor color;
	protected Coordinate position;
	protected MapArea mapArea;
	protected CursorResult result;

	protected int maxRadius = 0;

	public Cursor(Coordinate initialPosition, MapArea mapArea) {
		this.mapArea = mapArea;
		this.symbol = '_';
		this.color = SColor.INDIGO_DYE;
		this.position = new Coordinate(initialPosition.x, initialPosition.y);
	}

	public Cursor(Coordinate initialPosition, MapArea mapArea, int maxRadius) {
		this(initialPosition, mapArea);
		this.maxRadius = maxRadius;
	}

	public final boolean waitingForResult() {
		return isActive;
	}

	public final void show() {
		isActive = true;
		onShow();
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

	public final boolean process() {
		CursorResult processed = onProcess();

		if (processed != null) {
			isActive = false;
			this.result = processed;
		}
		return !isActive;
	}

	public final CursorResult result() {
		return result;
	}

	protected void onShow() {
	}

	protected void onDraw(TerminalBase terminal, int sx, int sy) {
		terminal.withColor(SColor.TRANSPARENT, color).fill(sx, sy, 1, 1);
		MapArea mapArea = Game.current().getCurrentMapArea();
		if (!mapArea.getTileAt(position).isExplored())
			terminal.withColor(SColor.TRANSPARENT, color).put(sx, sy, ' ');
	}

	protected final CursorResult onProcess() {
		InputCommand cmd = InputManager.nextCommand();
		if (cmd != null) {

			DirectionIntercardinal direction = cmd.toDirection();
			if (direction != DirectionIntercardinal.NONE) {

				Coordinate newPosition = position.createOffsetPosition(direction);
				if (isWithinBounds(newPosition)) {
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
					result = onProcessCommand(cmd);
				}
			}
		}
		return result;
	}

	private void setPosition(int x, int y) {
		position.x = x;
		position.y = y;
	}

	private void setPosition(Coordinate position) {
		this.position.x = position.x;
		this.position.y = position.y;
	}

	private boolean isWithinBounds(Coordinate position) {
		if (mapArea.isWithinBounds(position.x, position.y) && onUpdatePosition(position)) {
			if (maxRadius > 0) {
				Coordinate playerLocation = Game.current().getPlayer().getPosition();
				float distance = playerLocation.distance(position, radiusStrategy);
				return distance <= maxRadius;
			}
			return true;
		}
		return false;
	}

	/**
	 * Return false if the cursor can't be moved to the desired position, i.e. outside field of vision
	 * 
	 * @return
	 */
	protected boolean onUpdatePosition(Coordinate position) {
		return true;
	}

	/**
	 * Allow derived cursors to implement additional commands (for instance, to select a target)
	 * 
	 * @param command
	 * @return
	 */
	protected CursorResult onProcessCommand(InputCommand command) {
		return null;
	}
}

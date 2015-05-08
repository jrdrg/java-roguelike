package roguelike.screens;

import java.awt.Rectangle;

import roguelike.Cursor;
import roguelike.Game;
import roguelike.functionalInterfaces.CursorCallback;
import roguelike.maps.MapArea;
import roguelike.ui.windows.TerminalBase;
import roguelike.util.Coordinate;

public class CursorScreen extends Screen {

	protected Cursor cursor;
	protected CursorCallback resultCallback;

	protected TerminalBase cloneTerminal;

	public CursorScreen(TerminalBase terminal, Cursor cursor, CursorCallback resultCallback) {
		super(terminal);
		if (cursor == null)
			throw new IllegalArgumentException("cursor cannot be null");
		if (resultCallback == null)
			throw new IllegalArgumentException("resultCallback cannot be null");

		this.cursor = cursor;
		this.resultCallback = resultCallback;

		this.cloneTerminal = terminal.cloneTerminal();

		this.cursor.show();
	}

	@Override
	public void process() {
		if (cursor.process()) {
			resultCallback.setResult(cursor.result());
			restorePreviousScreen();
		}
	}

	@Override
	protected final void onDraw() {
		Rectangle drawableArea = getDrawableArea();
		Game game = Game.current();

		MapArea currentMap = game.getCurrentMapArea();
		Coordinate centerPosition = game.getCenterScreenPosition();
		Rectangle screenArea = currentMap
				.getVisibleAreaInTiles(drawableArea.width, drawableArea.height, centerPosition);

		/* redraw the previous terminal data */
		cloneTerminal.refresh(screenArea.x, screenArea.y, screenArea.width, screenArea.height);

		onDrawAdditional(currentMap, centerPosition, screenArea);

		cursor.draw(terminal, screenArea);
	}

	protected void onDrawAdditional(MapArea currentMap, Coordinate centerPosition, Rectangle screenArea) {
	}
}

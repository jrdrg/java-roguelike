package roguelike.ui;

import java.awt.Rectangle;

import roguelike.Game;
import roguelike.maps.MapArea;
import roguelike.ui.windows.TerminalBase;
import roguelike.util.Coordinate;
import squidpony.squidcolor.SColor;

public class Cursor {
	char symbol;
	SColor color;
	Coordinate position;

	public Cursor(Coordinate initialPosition) {
		symbol = '_';
		color = SColor.INDIGO_DYE;
		position = new Coordinate(initialPosition.x, initialPosition.y);
	}

	public Coordinate getPosition() {
		return position;
	}

	public void setPosition(int x, int y) {
		position.x = x;
		position.y = y;
	}

	public void setPosition(Coordinate position) {
		this.position.x = position.x;
		this.position.y = position.y;
	}

	public void draw(TerminalBase terminal, Rectangle screenArea) {
		if (terminal == null)
			throw new IllegalArgumentException("terminal cannot be null");

		if (!screenArea.contains(position)) {
			int px = (int) Math.max(screenArea.getMinX(), Math.min(position.x, screenArea.getMaxX() - 1));
			int py = (int) Math.max(screenArea.getMinY(), Math.min(position.y, screenArea.getMaxY() - 1));

			setPosition(px, py);
		}

		int sx = position.x - screenArea.x;
		int sy = position.y - screenArea.y;

		terminal.withColor(SColor.TRANSPARENT, color).fill(sx, sy, 1, 1);
		MapArea mapArea = Game.current().getCurrentMapArea();
		if (!mapArea.getTileAt(position).isExplored())
			terminal.withColor(SColor.TRANSPARENT, color).put(sx, sy, ' ');
	}
}

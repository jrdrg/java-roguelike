package roguelike.maps;

import java.awt.Point;
import java.awt.Rectangle;

import roguelike.Game;
import roguelike.util.Symbol;
import squidpony.squidgrid.util.DirectionCardinal;
import squidpony.squidmath.RNG;

public abstract class MapBuilderBase {
	protected RNG random = Game.current().random();
	protected TileBuilder tb = new TileBuilder();
	protected Tile[][] map;

	protected int width;
	protected int height;
	protected Rectangle mapRect;

	public final void buildMap(Tile[][] map) {
		this.map = map;
		this.width = map.length;
		this.height = map[0].length;
		this.mapRect = new Rectangle(0, 0, width, height);

		onBuildMap(map);
	}

	protected DirectionCardinal getRandomDirection() {
		int rnd = random.between(0, 4);
		return DirectionCardinal.CARDINALS[rnd];
	}

	protected Point getRandomPoint(Rectangle rectangle) {
		int x = (int) random.between(rectangle.getMinX(), rectangle.getMaxX());
		int y = (int) random.between(rectangle.getMinY(), rectangle.getMaxY());

		return new Point(x, y);
	}

	protected Rectangle getRandomRectangleInside(Rectangle containing) {
		int width = random.betweenWeighted(1, containing.width - 1, 10);
		int height = random.betweenWeighted(1, containing.height - 1, 10);

		int x = random.betweenWeighted((int) containing.getMinX(), (int) containing.getMaxX() - width, 10);
		int y = random.betweenWeighted((int) containing.getMinY(), (int) containing.getMaxY() - height, 10);

		return new Rectangle(x, y, width, height);
	}

	protected Rectangle getSubRectangle(Rectangle original, int newX, int newY, double xPercent, double yPercent) {
		Rectangle newRect = new Rectangle(original);
		newRect.setLocation(newX, newY);

		newRect.setSize((int) Math.floor(original.width * xPercent), (int) Math.floor(original.height * yPercent));
		return newRect;
	}

	/**
	 * Returns true if the area represented by the rectangle is all walls so we can make a room here
	 * 
	 * @param rect
	 * @return
	 */
	protected boolean canCreateRoom(Rectangle rect) {
		if (!mapRect.contains(rect))
			return false;

		for (int x = (int) rect.getMinX(); x < rect.getMaxX(); x++) {
			for (int y = (int) rect.getMinY(); y < rect.getMaxY(); y++) {
				if (!map[x][y].isWall())
					return false;
			}
		}
		return true;
	}

	/**
	 * Fills entire map with the given character
	 * 
	 * @param map
	 */
	protected void fillMap(Symbol character) {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				map[x][y] = tb.buildTile(character);
			}
		}
	}

	protected void fillRect(Rectangle rect, Symbol character) {
		int startX = (int) rect.getMinX();
		int endX = (int) rect.getMaxX();
		int startY = (int) rect.getMinY();
		int endY = (int) rect.getMaxY();

		for (int x = startX; x < endX; x++) {
			for (int y = startY; y < endY; y++) {
				map[x][y] = tb.buildTile(character);
			}
		}
	}

	protected void setTile(Point point, Symbol character) {
		map[point.x][point.y] = tb.buildTile(character);
	}

	protected abstract void onBuildMap(Tile[][] map);
}

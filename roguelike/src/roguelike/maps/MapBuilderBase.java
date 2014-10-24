package roguelike.maps;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import roguelike.Game;
import roguelike.util.Symbol;
import squidpony.squidgrid.util.DirectionCardinal;
import squidpony.squidmath.RNG;

public abstract class MapBuilderBase implements Serializable {
	private static final long serialVersionUID = 1L;

	protected transient RNG random = Game.current().random();
	protected transient TileBuilder tb = new TileBuilder();
	protected transient Tile[][] map;

	protected transient int width;
	protected transient int height;
	protected transient Rectangle mapRect;

	protected String mapName;

	protected MapBuilderBase(String mapName) {
		this.mapName = mapName;
	}

	public final String buildMap(Tile[][] map) {
		this.map = map;
		this.width = map.length;
		this.height = map[0].length;
		this.mapRect = new Rectangle(0, 0, width, height);

		onBuildMap(map);

		return mapName;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();

		tb = new TileBuilder();
		random = Game.current().random();
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
	 * Returns a randomly chosen DirectionCardinal.
	 * 
	 * @return
	 */
	protected DirectionCardinal getRandomDirection() {
		int rnd = random.between(0, 4);
		return DirectionCardinal.CARDINALS[rnd];
	}

	/**
	 * Returns a Point that is randomly chosen from within the given Rectangle's area.
	 * 
	 * @param rectangle
	 * @return
	 */
	protected Point getRandomPoint(Rectangle rectangle) {
		int x = (int) random.between(rectangle.getMinX(), rectangle.getMaxX());
		int y = (int) random.between(rectangle.getMinY(), rectangle.getMaxY());

		return new Point(x, y);
	}

	/**
	 * Returns a rectangle of random size and location within the specified containing rectangle.
	 * 
	 * @param containing
	 * @return
	 */
	protected Rectangle getRandomRectangleInside(Rectangle containing) {
		int width = random.betweenWeighted(1, containing.width - 1, 5);
		int height = random.betweenWeighted(1, containing.height - 1, 5);

		int x = random.betweenWeighted((int) containing.getMinX(), (int) containing.getMaxX() - width, 5);
		int y = random.betweenWeighted((int) containing.getMinY(), (int) containing.getMaxY() - height, 5);

		return new Rectangle(x, y, width, height);
	}

	/**
	 * Returns a rectangle that is the specified percentage of the original rectangle in width and height.
	 * 
	 * @param original
	 * @param newX
	 * @param newY
	 * @param xPercent
	 * @param yPercent
	 * @return
	 */
	protected Rectangle getSubRectangle(Rectangle original, int newX, int newY, double xPercent, double yPercent) {
		Rectangle newRect = new Rectangle(original);
		newRect.setLocation(newX, newY);

		newRect.setSize((int) Math.floor(original.width * xPercent), (int) Math.floor(original.height * yPercent));
		return newRect;
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

	/**
	 * Fills the area given by rect with the specified character.
	 * 
	 * @param rect
	 * @param character
	 */
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

	protected int getRandomX(Rectangle area) {
		return (int) random.between(area.getMinX(), area.getMaxX());
	}

	protected int getRandomY(Rectangle area) {
		return (int) random.between(area.getMinY(), area.getMaxY());
	}

	protected void setTile(int x, int y, Symbol character) {
		if (MapHelpers.isWithinBounds(map, x, y))
			map[x][y] = tb.buildTile(character);
	}

	protected void setTile(Point point, Symbol character) {
		if (map[point.x][point.y].symbol == Symbol.WALL.symbol())
			map[point.x][point.y] = tb.buildTile(character);
	}

	protected abstract void onBuildMap(Tile[][] map);
}

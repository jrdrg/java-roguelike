package roguelike.maps;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import roguelike.Game;
import squidpony.squidgrid.util.DirectionCardinal;
import squidpony.squidmath.RNG;

public class Room {
	protected final RNG random = Game.current().random();
	protected final ArrayList<Point> floorTiles;

	public ArrayList<Point> doors;
	public Rectangle area;

	public Room(Rectangle area) {
		this.area = area;
		doors = new ArrayList<Point>();
		floorTiles = new ArrayList<Point>();
	}

	public int bottom() {
		return (int) area.getMaxY() - 1;
	}

	public int top() {
		return (int) area.getMinY();
	}

	public int left() {
		return (int) area.getMinX();
	}

	public int right() {
		return (int) area.getMaxX() - 1;
	}

	public int getRandomX() {
		return (int) random.between(area.getMinX() + 1, area.getMaxX() - 1);
	}

	public int getRandomY() {
		return (int) random.between(area.getMinY() + 1, area.getMaxY() - 1);
	}

	public void fillRoom(Tile[][] map, TileBuilder tb, char tile) {
		Rectangle rect = this.area;
		for (int x = (int) rect.getMinX() + 1; x < rect.getMaxX() - 1; x++) {
			for (int y = (int) rect.getMinY() + 1; y < rect.getMaxY() - 1; y++) {
				map[x][y] = tb.buildTile('-');

				if (!map[x][y].isWall()) {
					floorTiles.add(new Point(x, y));
				}
			}
		}
	}

	public Point getRandomFloorTile() {
		Point p = null;
		if (floorTiles.size() > 1) {
			return floorTiles.get(random.between(0, floorTiles.size()));
		}
		return p;
	}

	public Point getDoorCoordinate(Tile[][] map, DirectionCardinal direction) {
		Point p = null;
		switch (direction) {
		case DOWN:
			p = new Point(this.getRandomX(), this.bottom());
			break;
		case UP:
			p = new Point(this.getRandomX(), this.top());
			break;
		case LEFT:
			p = new Point(this.left(), this.getRandomY());
			break;
		case RIGHT:
			p = new Point(this.right(), this.getRandomY());
			break;
		default:
			return null;
		}

		if (map[p.x][p.y].isWall()) {
			this.doors.add(p);
			return p;
		}
		return null;
	}

}
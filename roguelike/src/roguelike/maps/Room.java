package roguelike.maps;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Queue;

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

	public void fillRoom(Tile[][] map, TileBuilder tb, char tile) {
		Rectangle rect = this.area;
		for (int x = (int) rect.getMinX() + 1; x < rect.getMaxX() - 1; x++) {
			for (int y = (int) rect.getMinY() + 1; y < rect.getMaxY() - 1; y++) {
				map[x][y] = tb.buildTile('-');

				if (!map[x][y].isWall() && isFloorAdjacentToWall(map, x, y)) {
					floorTiles.add(new Point(x, y));
				}
			}
		}
	}

	public void addFloorTile(Point point) {
		if (area.contains(point) && !floorTiles.contains(point)) {
			floorTiles.add(point);
		}
	}

	public final boolean connectTo(Room other, Point doorPoint, Tile[][] map, TileBuilder tb) {
		if (!onConnectingTo(other, doorPoint, map, tb))
			return false;

		if (!other.onBeingConnectedTo(other, doorPoint, map, tb))
			return false;

		return true;
	}

	protected boolean onConnectingTo(Room other, Point doorPoint, Tile[][] map, TileBuilder tb) {
		this.fillRoom(map, tb, '-');

		Point newRoomConnectingTile = this.getRandomFloorTile();
		// if (newRoomConnectingTile != null) {
		// Queue<Point> path = MapHelpers.findPath(random, doorPoint, newRoomConnectingTile);
		// fillPath(path, '.', this, map, tb);
		// }

		map[doorPoint.x][doorPoint.y] = tb.buildTile('+');

		return true;
	}

	protected boolean onBeingConnectedTo(Room other, Point doorPoint, Tile[][] map, TileBuilder tb) {
		return true;
	}

	protected boolean isFloorAdjacentToWall(Tile[][] map, int x, int y) {
		if (x > 0 && map[x - 1][y].isWall())
			return true;
		if (y > 0 && map[x][y - 1].isWall())
			return true;
		if (x < map.length - 1 && map[x + 1][y].isWall())
			return true;
		if (y < map.length - 1 && map[x][y + 1].isWall())
			return true;

		return false;
	}

	protected void fillPath(Queue<Point> points, char tile, Room room, Tile[][] map, TileBuilder tb) {
		Point p = points.poll();
		while (p != null) {
			room.addFloorTile(p);

			map[p.x][p.y] = tb.buildTile(tile);
			p = points.poll();
		}
	}
}
package roguelike.maps;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import roguelike.Game;
import roguelike.util.CollectionUtils;
import roguelike.util.Log;
import roguelike.util.Symbol;
import squidpony.squidgrid.util.DirectionCardinal;
import squidpony.squidmath.RNG;

public class Room {
	protected final RNG random = Game.current().random();
	protected final ArrayList<Point> floorTiles;

	public ArrayList<ConnectionPoint> doors;
	public Rectangle area;

	public Room(Rectangle area) {
		this.area = area;
		doors = new ArrayList<ConnectionPoint>();
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
		return p;
	}

	public Point createDoorCoordinate(Tile[][] map, DirectionCardinal direction) {
		ConnectionPoint p = new ConnectionPoint(getDoorCoordinate(map, direction), direction, this);

		if (map[p.x][p.y].isWall()) {
			this.doors.add(p);
			return p;
		}
		return null;
	}

	public Point getExistingDoor(DirectionCardinal direction) {
		final Point startPoint;
		final Point endPoint;
		switch (direction) {
		case DOWN:
			startPoint = new Point(this.left(), this.bottom());
			endPoint = new Point(this.right(), this.bottom());
			break;
		case UP:
			startPoint = new Point(this.left(), this.top());
			endPoint = new Point(this.right(), this.top());
			break;
		case LEFT:
			startPoint = new Point(this.left(), this.top());
			endPoint = new Point(this.left(), this.bottom());
			break;
		case RIGHT:
			startPoint = new Point(this.right(), this.top());
			endPoint = new Point(this.right(), this.bottom());
			break;
		default:
			startPoint = null;
			endPoint = null;
		}
		if (startPoint == null || endPoint == null)
			return null;

		List<Point> candidates = doors
				.stream()
				.filter(d -> d.x == startPoint.x || d.x == endPoint.x || d.y == startPoint.y || d.y == endPoint.y)
				.collect(Collectors.toList());

		if (candidates.size() > 0) {
			return CollectionUtils.getRandomElement(candidates);
		}
		return null;
	}

	public void fillRoom(Tile[][] map, TileBuilder tb, Symbol tile) {
		Rectangle rect = this.area;
		for (int x = (int) rect.getMinX() + 1; x < rect.getMaxX() - 1; x++) {
			for (int y = (int) rect.getMinY() + 1; y < rect.getMaxY() - 1; y++) {

				map[x][y] = tb.buildTile(tile);

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
		this.fillRoom(map, tb, Symbol.DUNGEON_FLOOR);
		Log.debug("ROOM onConnectingTo: " + this.doors.remove(doorPoint));

		// Point newRoomConnectingTile = this.getRandomFloorTile();
		// if (newRoomConnectingTile != null) {
		// Queue<Point> path = MapHelpers.findPath(random, doorPoint, newRoomConnectingTile);
		// fillPath(path, '.', this, map, tb);
		// }

		map[doorPoint.x][doorPoint.y] = tb.buildTile('+');

		return true;
	}

	protected boolean onBeingConnectedTo(Room other, Point doorPoint, Tile[][] map, TileBuilder tb) {
		Log.debug("ROOM onBeingConnectedTo: " + this.doors.remove(doorPoint));
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
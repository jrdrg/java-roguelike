package roguelike.maps;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import roguelike.Game;
import roguelike.util.Log;
import squidpony.squidgrid.util.DirectionCardinal;

public class DungeonMapBuilder extends MapBuilderBase {

	private int width;
	private int height;

	private Rectangle mapRect;

	private ArrayList<Room> rooms;
	private Tile[][] map;

	public DungeonMapBuilder() {
		rooms = new ArrayList<Room>();
	}

	@Override
	public void buildMap(Tile[][] map) {
		this.map = map;
		this.width = map.length;
		this.height = map[0].length;
		this.mapRect = new Rectangle(0, 0, width, height);

		// fill in entire map with walls
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				map[x][y] = tb.buildTile('#');
			}
		}

		int roomX = random.between(width / 3, 2 * (width / 3));
		int roomY = random.between(height / 3, 2 * (height / 3));

		Room startRoom = generateRoom(roomX, roomY);
		while (startRoom == null)
		{
			roomX = random.between(width / 3, 2 * (width / 3));
			roomY = random.between(height / 3, 2 * (height / 3));
			startRoom = generateRoom(roomX, roomY);
		}
		rooms.add(startRoom);

		// TODO: create the main path through the dungeon first by passing in the most recently created room, then
		// create random rooms

		// TODO: create loops in the map so that there are multiple paths to get to a room

		for (int x = 0; x < 100; x++) {
			createRandomRoom();
		}

		int startX = (int) startRoom.area.getCenterX();
		int startY = (int) startRoom.area.getCenterY();
		Game.current().getPlayer().setPosition(startX, startY);
	}

	private void createRandomRoom() {
		Room room;
		if (rooms.size() > 1) {
			room = rooms.get(random.between(0, rooms.size()));
		} else {
			room = rooms.get(0);
		}
		DirectionCardinal direction = getRandomDirection();

		Point door = getDoorCoordinate(room, direction);
		if (door != null) {
			Room newRoom = generateJoinedTo(room, direction, door);
			if (newRoom != null) {

				Log.info("Door at " + door.x + "," + door.y);
				map[door.x][door.y] = tb.buildTile('+');
				rooms.add(newRoom);

				Log.info("Room created, count=" + rooms.size());
			}
		}
	}

	private Room generateRoom(int x, int y) {

		Rectangle rect = new Rectangle(random.between(1, width - 2), random.between(1, height - 2), random.between(5, 20), random.between(5, 20));
		if (!mapRect.contains(rect))
			return null;

		Room room = new Room(rect);
		fillRoom(room, '-');

		return room;
	}

	private Room generateJoinedTo(Room room, DirectionCardinal direction, Point joinPoint) {
		Point pt = new Point(joinPoint);
		pt.translate(direction.deltaX, direction.deltaY);

		int width = random.between(5, 20);
		int height = random.between(5, 20);

		int x1 = pt.x - Math.abs(direction.deltaY * random.between(1, width - 1));
		int y1 = pt.y - Math.abs(direction.deltaX * random.between(1, height - 1));

		if (direction.deltaY < 0)
			y1 -= (height - 1);
		if (direction.deltaX < 0)
			x1 -= (width - 1);

		Rectangle rect = new Rectangle(x1, y1, width, height);
		if (!mapRect.contains(rect))
			return null;
		if (room.area.intersects(rect))
			return null;
		if (!canCreateRoom(rect))
			return null;

		Room newRoom = new Room(rect);
		fillRoom(newRoom, '-');

		map[pt.x][pt.y] = tb.buildTile('-');

		return newRoom;

	}

	private Point getDoorCoordinate(Room room, DirectionCardinal direction) {
		Point p = null;
		switch (direction) {
		case DOWN:
			p = new Point(room.getRandomX(), room.bottom());
			break;
		case UP:
			p = new Point(room.getRandomX(), room.top());
			break;
		case LEFT:
			p = new Point(room.left(), room.getRandomY());
			break;
		case RIGHT:
			p = new Point(room.right(), room.getRandomY());
			break;
		default:
			return null;
		}

		if (map[p.x][p.y].isWall()) {
			room.doors.add(p);
			return p;
		}
		return null;
	}

	private boolean canCreateRoom(Rectangle rect) {
		for (int x = (int) rect.getMinX(); x < rect.getMaxX(); x++) {
			for (int y = (int) rect.getMinY(); y < rect.getMaxY(); y++) {
				if (!map[x][y].isWall())
					return false;
			}
		}
		return true;
	}

	private void fillRoom(Room room, char tile) {
		room.fillRoom(map, tb, tile);
	}

	private DirectionCardinal getRandomDirection() {
		int rnd = random.between(0, 4);
		return DirectionCardinal.CARDINALS[rnd];
	}
}

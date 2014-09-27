package roguelike.maps;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import roguelike.Game;
import roguelike.util.CollectionUtils;
import roguelike.util.Log;
import squidpony.squidgrid.util.DirectionCardinal;
import squidpony.squidutility.ProbabilityTable;

public class DungeonMapBuilder extends MapBuilderBase {

	private int width;
	private int height;

	private Rectangle mapRect;

	private ArrayList<Room> rooms;
	private Tile[][] map;

	private ArrayList<Point> connectionPoints;

	public DungeonMapBuilder() {
		rooms = new ArrayList<Room>();
		connectionPoints = new ArrayList<Point>();
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

		for (int x = 0; x < 50; x++) {
			createRandomRoom();
		}

		int startX = (int) startRoom.area.getCenterX();
		int startY = (int) startRoom.area.getCenterY();
		Game.current().getPlayer().setPosition(startX, startY);

		/*
		 * create room
		 * 
		 * determine all possible connection points for the room, but do not put doors there yet
		 * 
		 * store all connection points in a list, choose one at random
		 * 
		 * instead of picking a room use this connection point along with a random direction that is a free square
		 * 
		 * when a point is connected to, remove it from the list
		 * 
		 * after all rooms are created, put walls back in any remaining connection points
		 * 
		 * clear the connection points list
		 */

		// fill all rooms
		for (Room room : rooms) {
			room.fillRoom(map, tb, '-');
		}
	}

	private void createRandomRoom() {
		Room room;
		room = CollectionUtils.getRandomElement(rooms);

		DirectionCardinal direction = getRandomDirection();

		Point door = room.getDoorCoordinate(map, direction);
		if (door != null) {
			Room newRoom = generateJoinedTo(room, direction, door);
			if (newRoom != null) {

				// Log.info("Door at " + door.x + "," + door.y);
				map[door.x][door.y] = tb.buildTile('-');
				rooms.add(newRoom);

				Log.info("Room created, count=" + rooms.size());
			}
		}
	}

	private Room generateRoom(int x, int y) {

		Rectangle rect = new Rectangle(random.between(1, width - 2), random.between(1, height - 2), random.between(5, 30), random.between(5, 20));
		if (!mapRect.contains(rect))
			return null;

		Room room = new Room(rect);
		// fillRoom(room, '-');

		int numDoors = random.between(2, 5);
		for (int i = 0; i < numDoors; i++) {
			Point door = room.getDoorCoordinate(map, getRandomDirection());
			if (door != null) {
				room.doors.add(door);
				connectionPoints.add(door);
			}
		}

		return room;
	}

	private Rectangle getJoinedToSize(Room room, DirectionCardinal direction, Point roomDoor) {
		Point originatingDoor = new Point(roomDoor);

		int width = random.between(this.width / 10, this.width / 5);
		int height = random.between(this.height / 10, this.height / 5);

		int x1 = originatingDoor.x - Math.abs(direction.deltaY * random.between(1, width - 1));
		int y1 = originatingDoor.y - Math.abs(direction.deltaX * random.between(1, height - 1));

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

		return rect;
	}

	private Room generateJoinedTo(Room room, DirectionCardinal direction, Point originalRoomDoor) {
		Point originatingDoor = new Point(originalRoomDoor);
		originatingDoor.translate(direction.deltaX, direction.deltaY);

		Rectangle rect = getJoinedToSize(room, direction, originatingDoor);
		if (rect == null)
			return null;

		// Room newRoom = new Room(rect);
		Room newRoom = getRandomRoomType(rect, originatingDoor);

		if (newRoom.connectTo(room, originatingDoor, map, tb)) {

			return newRoom;
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

	private Room getRandomRoomType(Rectangle rect, Point door) {
		int rnd = random.between(1, 7);
		if (rnd < 3) {
			return new Corridor(rect, door);
		} else {
			return new Room(rect);
		}
	}

	private Rectangle createRandomRectangle(Rectangle containing) {
		int width = random.betweenWeighted(1, containing.width - 1, 10);
		int height = random.betweenWeighted(1, containing.height - 1, 10);

		int x = random.betweenWeighted((int) containing.getMinX(), (int) containing.getMaxX() - width, 10);
		int y = random.betweenWeighted((int) containing.getMinY(), (int) containing.getMaxY() - height, 10);

		return new Rectangle(x, y, width, height);
	}
}

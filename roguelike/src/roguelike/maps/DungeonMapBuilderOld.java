package roguelike.maps;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import roguelike.Game;
import roguelike.util.CollectionUtils;
import roguelike.util.Log;
import roguelike.util.Symbol;
import squidpony.squidgrid.util.DirectionCardinal;
import squidpony.squidutility.ProbabilityTable;

public class DungeonMapBuilderOld extends MapBuilderBase {

	private int width;
	private int height;

	private Rectangle mapRect;

	private ArrayList<Room> rooms;

	private ArrayList<Point> connectionPoints;

	public DungeonMapBuilderOld() {
		rooms = new ArrayList<Room>();
		connectionPoints = new ArrayList<Point>();

	}

	@Override
	public void onBuildMap(Tile[][] map) {
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

		// int roomX = random.between(width / 3, 2 * (width / 3));
		// int roomY = random.between(height / 3, 2 * (height / 3));
		//
		// Room startRoom = generateRoom(roomX, roomY);
		// while (startRoom == null)
		// {
		// roomX = random.between(width / 3, 2 * (width / 3));
		// roomY = random.between(height / 3, 2 * (height / 3));
		// startRoom = generateRoom(roomX, roomY);
		// }

		Rectangle startRect = getRandomRectangleInside(mapRect);
		Room startRoom = generateRoom(startRect, startRect.x, startRect.y);
		while (startRect == null) {
			startRect = getRandomRectangleInside(mapRect);
			startRoom = generateRoom(startRect, startRect.x, startRect.y);
		}

		rooms.add(startRoom);

		// TODO: create the main path through the dungeon first by passing in the most recently created room, then
		// create random rooms

		// TODO: create loops in the map so that there are multiple paths to get to a room

		for (int x = 0; x < 5; x++) {
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

		ArrayList<Corridor> toRemove = new ArrayList<Corridor>();
		for (Room room : rooms) {
			if (room instanceof Corridor) {

			}
		}

		// fill all rooms
		// for (Room room : rooms) {
		// room.fillRoom(map, tb, '-');
		// }
	}

	private void createRandomRoom() {
		Room room;
		room = CollectionUtils.getRandomElement(rooms);

		DirectionCardinal direction = getRandomDirection();

		Point door = room.getExistingDoor(direction);
		if (door != null) {
			Room newRoom = generateJoinedTo(room, direction, door);
			if (newRoom != null) {

				// Log.info("Door at " + door.x + "," + door.y);
				map[door.x][door.y] = tb.buildTile('T');
				rooms.add(newRoom);

				Log.info("Room created, count=" + rooms.size());
			}
		}
	}

	private Room generateRoom(int x, int y) {
		Rectangle rect = new Rectangle(random.between(1, width - 2), random.between(1, height - 2), random.between(5, 30), random.between(5, 20));
		return generateRoom(rect, x, y);
	}

	private Room generateRoom(Rectangle rect, int x, int y) {

		if (!mapRect.contains(rect))
			return null;

		Room room = new Room(rect);
		// fillRoom(room, '-');
		room.fillRoom(map, tb, Symbol.DUNGEON_FLOOR);

		int numDoors = random.between(2, 5);
		int actualDoors = 0;
		for (int i = 0; i < numDoors; i++) {
			Point door = room.createDoorCoordinate(map, getRandomDirection());
			if (door != null) {
				connectionPoints.add(door);
				actualDoors++;
			}
		}
		return actualDoors > 0 ? room : null;
	}

	private Room generateJoinedTo(Room room, DirectionCardinal direction, Point originalRoomDoor) {

		map[originalRoomDoor.x][originalRoomDoor.y] = tb.buildTile('~');

		Point originatingDoor = new Point(originalRoomDoor);
		// int numSquares = random.between(2, 10);
		int numSquares = 1;
		originatingDoor.translate(direction.deltaX * numSquares, direction.deltaY * numSquares);

		MapHelpers.constrainToRectangle(originatingDoor, map.length, map[0].length);

//		Corridor corridor = new Corridor(originalRoomDoor, originatingDoor);
		// corridor.doors.stream().filter(d -> !connectionPoints.contains(d)).forEach(d -> connectionPoints.add(d));
//		corridor.fillRoom(map, tb, Symbol.TREE);
		// rooms.add(corridor);

		Rectangle rect = getJoinedToSize(room, direction, originatingDoor);
		if (rect == null)
			return null;

		Room newRoom = getRandomRoomType(rect, originatingDoor);

		if (newRoom.connectTo(room, originatingDoor, map, tb)) {

			return newRoom;
		}

		return null;
	}

	private Rectangle getJoinedToSize(Room room, DirectionCardinal direction, Point roomDoor) {
		Point originatingDoor = new Point(roomDoor);

		int width = random.between(this.width / 6, this.width / 3);
		int height = random.between(this.height / 6, this.height / 3);

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

	private Room getRandomRoomType(Rectangle rect, Point door) {
		int rnd = random.between(1, 7);
		// if (rnd < 3) {
		// return new Corridor(door);
		//
		// } else {
		return generateRoom(rect, rect.x, rect.y);

		// }
	}

	/**
	 * Determines which room a given point belongs to
	 * 
	 * @param point
	 * @return
	 */
	private Room getRoomForPoint(Point point) {
		return rooms.stream()
				.filter(r -> r.area.contains(point))
				.findFirst()
				.orElse(null);
	}
}

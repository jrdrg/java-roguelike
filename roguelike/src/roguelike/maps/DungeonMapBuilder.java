package roguelike.maps;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Stack;

import roguelike.Game;
import roguelike.util.Log;
import roguelike.util.Symbol;
import squidpony.squidgrid.util.DirectionCardinal;
import squidpony.squidutility.ProbabilityTable;

public class DungeonMapBuilder extends MapBuilderBase {

	/**
	 * Divide the map up into equal sections to try and get a reasonably even distribution of rooms
	 * 
	 * @author john
	 *
	 */
	private class MapSection {
		public Rectangle area;
		public int floorSpaces;
		public int totalSpaces;

		public MapSection(Rectangle area) {
			this.area = area;
			floorSpaces = 0;
			totalSpaces = area.width * area.height;
		}

		public boolean contains(Point point) {
			return area.contains(point);
		}

		public void add(Rectangle rectangle) {
			floorSpaces += (rectangle.width * rectangle.height);
		}
	}

	private ArrayList<Room> rooms;
	private ArrayList<MapSection> mapSections;

	public DungeonMapBuilder() {
		rooms = new ArrayList<Room>();

	}

	@Override
	protected void onBuildMap(Tile[][] map) {

		int roomCount = 10;
		int maxTries = 50;

		for (int i = 0; i < maxTries; i++) {

			/* Initialize map sections */
			mapSections = new ArrayList<DungeonMapBuilder.MapSection>();
			mapSections.add(new MapSection(getSubRectangle(mapRect, 0, 0, .5, .5)));
			mapSections.add(new MapSection(getSubRectangle(mapRect, mapRect.width / 2, 0, .5, .5)));
			mapSections.add(new MapSection(getSubRectangle(mapRect, mapRect.width / 2, mapRect.height / 2, .5, .5)));
			mapSections.add(new MapSection(getSubRectangle(mapRect, 0, mapRect.height / 2, .5, .5)));

			fillMap(Symbol.WALL);

			Room startRoom = null;

			while (startRoom == null)
				startRoom = chooseRandomStartRoom();

			buildDoors(startRoom);

			rooms.add(startRoom);

			int startX = (int) startRoom.area.getCenterX();
			int startY = (int) startRoom.area.getCenterY();
			Game.current().getPlayer().setPosition(startX, startY);

			generateMainPath(startRoom);

			if (rooms.size() >= roomCount)
				break;
		}
	}

	private void generateMainPath(Room startRoom) {
		int maxRooms = 20;
		Room currentRoom = null;

		Stack<Room> roomsOnPath = new Stack<Room>();
		roomsOnPath.push(startRoom);

		currentRoom = startRoom;
		for (int x = 0; x < maxRooms; x++) {
			if (roomsOnPath.size() == 0)
				break;

			boolean fail = false;

			ConnectionPoint door = generateRandomDoor(currentRoom);
			if (door == null)
				fail = true;

			if (!fail) {
				ConnectionPoint endPoint = buildCorridor(door, currentRoom);

				if (endPoint != null) {

					setTile(door, Symbol.BUILDING_FLOOR);
					setTile(endPoint, Symbol.GROUND);

					Rectangle rect = getRectangleForEndPoint(endPoint);

					Room newRoom = createRoom(rect);

					super.setTile(rect.getLocation(), Symbol.MOUNTAIN);
					Log.debug("Creating room");

					currentRoom.doors.add(door);
					rooms.add(newRoom);
					roomsOnPath.push(currentRoom);

					currentRoom = newRoom;
				} else {

					fail = true;
					System.out.println("endPoint==null, x=" + x);
				}
			}

			if (fail) {

				for (ConnectionPoint doorPoint : currentRoom.doors) {
					setTile(doorPoint, Symbol.DOOR);
				}

				currentRoom = roomsOnPath.pop();
			}
		}

		Point stairPoint = currentRoom.getRandomFloorTile();
		setTile(stairPoint, Symbol.STAIRS);

	}

	private ConnectionPoint generateRandomDoor(Room room) {
		ProbabilityTable<DirectionCardinal> directions = new ProbabilityTable<DirectionCardinal>();

		DirectionCardinal[] allDirections = DirectionCardinal.CARDINALS;
		for (DirectionCardinal dir : allDirections) {

			Rectangle area = new Rectangle(room.area);
			area.width *= random.between(0.75, 1.25);
			area.height *= random.between(0.75, 1.25);
			area.x += (dir.deltaX * (area.width));
			area.y += (dir.deltaY * (area.height));

			if (canCreateRoom(area))
			{
				directions.add(dir, area.width * area.height);
				// fillRect(area, Symbol.MOUNTAIN);

				Point doorp = room.getDoorCoordinate(map, dir);
				super.setTile(doorp, Symbol.BOX_BOTTOM_LEFT_SINGLE);
			}

		}

		DirectionCardinal direction = directions.random();
		if (direction == null)
			return null;

		ConnectionPoint existing = room.doors.stream().filter(d -> d.direction().equals(direction)).findAny().orElse(null);
		if (existing != null) {
			// TODO: allow doors to be in the same direction?
		}

		Point doorPoint = room.getDoorCoordinate(map, direction);
		ConnectionPoint point = new ConnectionPoint(doorPoint, direction, room);

		return point;
	}

	private ConnectionPoint buildCorridor(ConnectionPoint door, Room room) {
		DirectionCardinal direction = door.direction();

		int roomSize = 5;
		int corridorSize = random.between(3, 5);
		int totalSize = roomSize + corridorSize;

		Point endPoint = new Point(door.x + (direction.deltaX * totalSize), door.y + (direction.deltaY * totalSize));
		Point constrained = new Point(endPoint);
		MapHelpers.constrainToRectangle(constrained, mapRect.width - 1, mapRect.height - 1);
		if (!endPoint.equals(constrained))
			return null;

		endPoint.x -= (direction.deltaX * roomSize);
		endPoint.y -= (direction.deltaY * roomSize);

		Rectangle rect = new Rectangle();
		rect.setFrameFromDiagonal(door, endPoint);
		if (rect.width == 0)
			rect.width = 1;
		if (rect.height == 0)
			rect.height = 1;

		if (!canCreateRoom(rect))
			return null;

		fillRect(rect, Symbol.HILLS);

		return new ConnectionPoint(endPoint, direction.opposite(), room);
	}

	private Room createRoom(Rectangle area) {
		Room room = null;

		room = new Room(area);
		room.fillRoom(map, tb, Symbol.DUNGEON_FLOOR);

		return room;
	}

	private Rectangle getRectangleForEndPoint(ConnectionPoint endPoint) {
		int maxSize = 10;
		int centerPoint = random.between(1, maxSize - 1);
		Point connection = new Point(endPoint);
		DirectionCardinal direction = endPoint.direction().opposite();
		connection.translate(-direction.deltaX, -direction.deltaY);

		DirectionCardinal rightAngle = direction.counterClockwise();
		Point start = new Point(connection);
		start.translate(rightAngle.deltaX * centerPoint, rightAngle.deltaY * centerPoint);

		Point end = new Point(connection);
		end.translate(rightAngle.opposite().deltaX * (maxSize - centerPoint), rightAngle.opposite().deltaY * (maxSize - centerPoint));
		end.translate(direction.deltaX * maxSize, direction.deltaY * maxSize);

		MapHelpers.constrainToRectangle(start, mapRect.width, mapRect.height);
		MapHelpers.constrainToRectangle(end, mapRect.width, mapRect.height);

		Rectangle rect = new Rectangle();
		rect.setFrameFromDiagonal(start, end);
		return rect;
	}

	private void buildDoors(Room startRoom) {
		// TODO Auto-generated method stub

	}

	private MapSection randomMapSection() {
		ProbabilityTable<MapSection> sections = new ProbabilityTable<MapSection>();

		for (MapSection section : mapSections) {
			sections.add(section, (int) (((section.floorSpaces / (float) section.totalSpaces) + 1) * 100));
		}

		return sections.random();
	}

	private Room chooseRandomStartRoom() {
		MapSection startInSection = randomMapSection();
		Rectangle startingArea = getRandomRectangleInside(startInSection.area);

		if (!canCreateRoom(startingArea))
			return null;

		return createRoom(startingArea);
	}
}

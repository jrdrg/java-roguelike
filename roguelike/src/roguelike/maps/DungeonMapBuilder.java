package roguelike.maps;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Stack;

import roguelike.Game;
import roguelike.util.CollectionUtils;
import roguelike.util.Log;
import roguelike.util.Symbol;
import squidpony.squidcolor.SColor;
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
	private int level;

	public DungeonMapBuilder() {
		this(1);
	}

	public DungeonMapBuilder(int level) {
		super("Dungeon, floor " + level);
		rooms = new ArrayList<Room>();
		this.level = level;
	}

	@Override
	protected void onBuildMap(Tile[][] map) {

		int roomCount = 20;
		int maxTries = 50;

		for (int i = 0; i < maxTries; i++) {
			rooms.clear();

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

			rooms.add(startRoom);

			// TODO: add the stairs going up at the player's starting position

			int startX = (int) startRoom.area.getCenterX();
			int startY = (int) startRoom.area.getCenterY();
			Game.current().getPlayer().setPosition(startX, startY);

			int roomsGenerated = generateMainPath(startRoom);

			roomsGenerated += generateRandomRooms();

			if (roomsGenerated >= roomCount)
				break;
		}
	}

	private Room chooseRandomStartRoom() {
		MapSection startInSection = randomMapSection();
		Rectangle startingArea = getRandomRectangleInside(startInSection.area);

		if (!canCreateRoom(startingArea))
			return null;

		return createRoom(startingArea);
	}

	private int generateMainPath(Room startRoom) {
		int roomsGenerated = 0;
		int maxRooms = 15;
		Room currentRoom = null;

		Stack<Room> roomsOnPath = new Stack<Room>();
		roomsOnPath.push(startRoom);

		currentRoom = startRoom;
		for (int x = 0; x < maxRooms; x++) {
			if (roomsOnPath.size() == 0)
				break;

			boolean fail = false;
			DirectionCardinal direction = null;
			Rectangle area = null;

			for (int i = 0; i < 10; i++) {
				fail = false;
				direction = getRandomDirection();
				Point initialLocation = currentRoom.area.getLocation();

				area = getRectangleForRoom(direction, initialLocation);

				if (!canCreateRoom(area)) {
					fail = true;
				}
				if (!fail)
					break;
			}
			if (direction == null || area == null)
				continue;

			ConnectionPoint door = generateRandomDoor(currentRoom, direction);

			if (door == null)
				fail = true;

			if (!fail) {
				Rectangle rect = area;

				Room newRoom = createRoom(rect);

				ConnectionPoint endPoint = buildCorridor(door, newRoom, area);

				if (endPoint != null) {

					setTile(door, Symbol.DUNGEON_FLOOR);
					setTile(endPoint, Symbol.DUNGEON_FLOOR);

					currentRoom.doors.add(door);

					addRoom(newRoom);
					roomsOnPath.push(currentRoom);

					currentRoom = newRoom;

					roomsGenerated++;
				} else {

					fail = true;
					System.out.println("endPoint==null, x=" + x);
				}
			}

			if (fail) {

				for (ConnectionPoint doorPoint : currentRoom.doors) {
					setDoor(doorPoint);
					roomsGenerated--;
				}

				currentRoom = roomsOnPath.pop();
			}
		}

		/* put the stairs in the last room we generated */
		Point stairPoint = currentRoom.getRandomFloorTile();
		addStairsDown(stairPoint);
		return roomsGenerated;
	}

	private int generateRandomRooms() {
		int roomsGenerated = 0;
		int maxRooms = 20;

		Log.debug("Room count: " + rooms.size());

		for (int x = 0; x < maxRooms; x++) {

			Room randomRoom = CollectionUtils.getRandomElement(rooms);

			boolean fail = false;
			DirectionCardinal direction = null;
			Rectangle area = null;

			for (int i = 0; i < 10; i++) {
				fail = false;
				direction = getRandomDirection();
				Point initialLocation = randomRoom.area.getLocation();

				area = getRectangleForRoom(direction, initialLocation);

				if (!canCreateRoom(area)) {
					fail = true;
				}
				if (!fail)
					break;
			}
			if (direction == null || area == null || fail)
				continue;

			ConnectionPoint door = generateRandomDoor(randomRoom, direction);
			if (door == null)
				fail = true;

			if (!fail) {
				Rectangle rect = area;

				Room newRoom = createRoom(rect);

				ConnectionPoint endPoint = buildCorridor(door, newRoom, area);

				if (endPoint != null) {

					setTile(door, Symbol.BUILDING_FLOOR);
					setTile(endPoint, Symbol.BUILDING_FLOOR);

					randomRoom.doors.add(door);

					addRoom(newRoom);

					roomsGenerated++;

					connectToRandomRoom(randomRoom);
				} else {

					fail = true;
					System.out.println("endPoint==null, x=" + x);
				}
			}
		}

		return roomsGenerated;
	}

	private Rectangle getRectangleForRoom(DirectionCardinal direction, Point initialLocation) {
		Rectangle area;
		int xOffset;
		int yOffset;
		area = new Rectangle(initialLocation);

		area.width = random.between(6, 15);
		area.height = random.between(4, 10);

		xOffset = (direction.deltaX * (area.width));
		yOffset = (direction.deltaY * (area.height));

		xOffset += random.between(-3, 3);
		yOffset += random.between(-3, 3);

		area.x += xOffset;
		area.y += yOffset;
		return area;
	}

	/**
	 * Creates a ConnectionPoint for a door in the given direction.
	 * 
	 * @param room
	 * @return
	 */
	private ConnectionPoint generateRandomDoor(Room room, DirectionCardinal direction) {

		Point doorPoint = room.getDoorCoordinate(map, direction);
		ConnectionPoint point = new ConnectionPoint(doorPoint, direction, room);

		return point;
	}

	/**
	 * Fills the map with DUNGEON_FLOOR inside the given Rectangle and returns a Room with that area.
	 * 
	 * @param area
	 * @return
	 */
	private Room createRoom(Rectangle area) {
		Room room = null;

		room = new Room(area);
		room.fillRoom(map, tb, Symbol.DUNGEON_FLOOR);

		return room;
	}

	private ConnectionPoint buildCorridor(ConnectionPoint originatingPoint, Room room, Rectangle targetArea) {

		DirectionCardinal direction = originatingPoint.direction();

		Point endPoint = new Point(originatingPoint.x + (direction.deltaX), originatingPoint.y + (direction.deltaY));
		Point constrained = new Point(endPoint);
		MapHelpers.constrainToRectangle(constrained, mapRect.width - 1, mapRect.height - 1);
		if (!endPoint.equals(constrained)) {
			Log.debug("cannot construct corridor");
			return null;
		}
		setTile(endPoint, Symbol.DUNGEON_FLOOR);

		Rectangle floorTargetArea = new Rectangle(targetArea);
		floorTargetArea.grow(-1, -1);
		if (!floorTargetArea.contains(endPoint)) {
			boolean yFirst = random.nextBoolean();
			int targetX = (int) random.between(targetArea.getMinX() + 2, targetArea.getMaxX() - 2);
			int targetY = (int) random.between(targetArea.getMinY() + 2, targetArea.getMaxY() - 2);

			int xOffset = (int) (Math.signum(targetX - endPoint.x));
			int yOffset = (int) (Math.signum(targetY - endPoint.y));

			if (yFirst) {
				while (endPoint.y != targetY) {
					endPoint.translate(0, yOffset);
					setTile(endPoint, Symbol.DUNGEON_FLOOR);
				}
				while (endPoint.x != targetX) {
					endPoint.translate(xOffset, 0);
					setTile(endPoint, Symbol.DUNGEON_FLOOR);
				}

			} else {
				while (endPoint.x != targetX) {
					endPoint.translate(xOffset, 0);
					setTile(endPoint, Symbol.DUNGEON_FLOOR);
				}
				while (endPoint.y != targetY) {
					endPoint.translate(0, yOffset);
					setTile(endPoint, Symbol.DUNGEON_FLOOR);
				}

			}
		}
		return new ConnectionPoint(endPoint, direction, room);
	}

	private boolean connectToRandomRoom(Room room) {
		Room randomRoom = CollectionUtils.getRandomElement(rooms);
		if (randomRoom == null)
			return false;

		int maxDistance = 5;
		if (room.area.getLocation().distance(room.area.getLocation()) <= maxDistance) {
			// try to connect them

			if (room.doors.stream().anyMatch(d -> d.isDoor)) {
				if (randomRoom.doors.stream().anyMatch(d -> d.isDoor))
					return false;
			}

			ConnectionPoint randomDoor = CollectionUtils.getRandomElement(room.doors);
			if (randomDoor != null) {
				ConnectionPoint endPoint = buildCorridor(randomDoor, room, randomRoom.area);
				if (endPoint == null)
					Log.debug("connect to random room, null endpoint");
				else
					Log.debug("endPoint=" + endPoint);
				return true;
			}
		}
		return false;
	}

	private MapSection randomMapSection() {
		ProbabilityTable<MapSection> sections = new ProbabilityTable<MapSection>();

		for (MapSection section : mapSections) {
			sections.add(section, (int) (((section.floorSpaces / (float) section.totalSpaces) + 1) * 100));
		}

		return sections.random();
	}

	private void addStairsDown(Point point) {
		map[point.x][point.y] =
				new Stairs(new DungeonMapBuilder(level + 1)).setValues(Symbol.STAIRS.symbol(), true, SColor.WHITE);
	}

	private void addRoom(Room room) {
		rooms.add(room);
	}

	private void setDoor(ConnectionPoint doorPoint) {
		setTile(doorPoint, Symbol.DOOR);
		doorPoint.isDoor = true;
	}
}

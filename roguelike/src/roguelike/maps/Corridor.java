package roguelike.maps;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Queue;

import roguelike.util.CollectionUtils;
import roguelike.util.Log;

public class Corridor extends Room {

	public Corridor(Rectangle area, Point door) {
		super(area);
		this.doors.add(door);
	}

	@Override
	public void fillRoom(Tile[][] map, TileBuilder tb, char tile) {
		Rectangle rect = this.area;
		for (int x = (int) rect.getMinX() + 1; x < rect.getMaxX() - 1; x++) {
			for (int y = (int) rect.getMinY() + 1; y < rect.getMaxY() - 1; y++) {
				map[x][y] = tb.buildTile('~');

			}
		}

		int rndX = (int) random.between(area.getMinX() + 1, area.getMaxX() - 1);
		int rndY = (int) random.between(area.getMinY() + 1, area.getMaxY() - 1);

		map[rndX][rndY] = tb.buildTile('T');

		Point door = CollectionUtils.getRandomElement(doors);
		if (door == null)
			return;

		Point dest = new Point(rndX, rndY);
		Queue<Point> path = MapHelpers.findPath(random, door, dest);

		Point p = path.poll();
		while (p != null) {
			this.addFloorTile(p);

			map[p.x][p.y] = tb.buildTile('*');
			p = path.poll();
		}
	}

	@Override
	protected boolean onConnectingTo(Room other, Point doorPoint, Tile[][] map, TileBuilder tb) {

		this.fillRoom(map, tb, 'T');

		Point connectingTileThisRoom = this.getRandomFloorTile();
		if (connectingTileThisRoom != null) {

			Point otherDoorPoint = other.getRandomFloorTile();
			if (otherDoorPoint == null || connectingTileThisRoom == null) {
				Log.warning("Tile is null, cannot create corridor");
				return false;
			}
			// Queue<Point> path = MapHelpers.findPath(random, otherDoorPoint, connectingTileThisRoom);
			// fillPath(path, '.', this, map, tb);
		}

		map[doorPoint.x][doorPoint.y] = tb.buildTile('T');

		return true;
	}

	@Override
	protected boolean onBeingConnectedTo(Room other, Point doorPoint, Tile[][] map, TileBuilder tb) {

		Point connectingTileThisRoom = this.getRandomFloorTile();
		if (connectingTileThisRoom != null) {

			Queue<Point> path = MapHelpers.findPath(random, doorPoint, connectingTileThisRoom);
			fillPath(path, '.', this, map, tb);

		}
		return true;
	}
}

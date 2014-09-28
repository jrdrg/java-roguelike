package roguelike.maps;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Queue;
import java.util.stream.Collectors;

import roguelike.util.CollectionUtils;
import roguelike.util.Log;
import roguelike.util.Symbol;
import squidpony.squidgrid.util.DirectionCardinal;

public class Corridor extends Room {

	DirectionCardinal direction;
	int numTurns;

	public Corridor(ConnectionPoint start, ConnectionPoint end) {
		super(new Rectangle(Math.min(start.x, end.x), Math.min(start.y, end.y), Math.abs(end.x - start.x), Math.abs(end.y - start.y)));
		this.doors.add(start);
		this.doors.add(end);

	}

	@Override
	public void fillRoom(Tile[][] map, TileBuilder tb, Symbol tile) {
		Rectangle rect = this.area;
		for (int x = (int) rect.getMinX() + 1; x < rect.getMaxX() - 1; x++) {
			for (int y = (int) rect.getMinY() + 1; y < rect.getMaxY() - 1; y++) {
				map[x][y] = tb.buildTile('~');

			}
		}

		Point start = CollectionUtils.getRandomElement(doors);
		Point end = CollectionUtils.getRandomElement(
				doors.stream().filter(d -> !d.equals(start)).collect(Collectors.toList())
				);

		if (end == null) {
			Log.debug("Corridor start=end, " + start.x + ", " + start.y);
			return;
		}

		Queue<Point> path = MapHelpers.findPath(random, start, end);

		Point p = path.poll();
		try {
			while (p != null) {
				this.addFloorTile(p);

				map[p.x][p.y] = tb.buildTile(tile);
				p = path.poll();
			}

			map[start.x][start.y].symbol = 'S';
			map[end.x][end.y].symbol = 'E';
		} catch (Exception e) {
			System.out.println("path remaining: " + path.size());
			System.out.println("p=" + p.x + "," + p.y);
			System.out.println("start=" + start.x + "," + start.y);
			System.out.println("end=" + end.x + "," + end.y);
			throw e;
		}
	}

	@Override
	protected boolean onConnectingTo(Room other, Point doorPoint, Tile[][] map, TileBuilder tb) {

		// this.fillRoom(map, tb, 'T');
		//
		// Point connectingTileThisRoom = this.getRandomFloorTile();
		// if (connectingTileThisRoom != null) {
		//
		// Point otherDoorPoint = other.getRandomFloorTile();
		// if (otherDoorPoint == null || connectingTileThisRoom == null) {
		// Log.warning("Tile is null, cannot create corridor");
		// return false;
		// }
		// // Queue<Point> path = MapHelpers.findPath(random, otherDoorPoint, connectingTileThisRoom);
		// // fillPath(path, '.', this, map, tb);
		// }

		map[doorPoint.x][doorPoint.y] = tb.buildTile('T');

		return true;
	}

	@Override
	protected boolean onBeingConnectedTo(Room other, Point doorPoint, Tile[][] map, TileBuilder tb) {

		// Point connectingTileThisRoom = this.getRandomFloorTile();
		// if (connectingTileThisRoom != null) {
		//
		// Queue<Point> path = MapHelpers.findPath(random, doorPoint, connectingTileThisRoom);
		// fillPath(path, '.', this, map, tb);
		//
		// }
		Log.debug("onBeingConnectedTo: " + this.doors.remove(doorPoint));

		return true;
	}
}

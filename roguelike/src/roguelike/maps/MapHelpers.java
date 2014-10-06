package roguelike.maps;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import roguelike.util.Symbol;
import squidpony.squidmath.RNG;

public class MapHelpers {
	public static ArrayList<Point> getNeighbors(MapArea map, int xPos, int yPos, int range) {
		return getNeighbors(map.width(), map.height(), xPos, yPos, range);
	}

	public static ArrayList<Point> getNeighbors(int width, int height, int xPos, int yPos, int range) {
		int size = range * 2 + 1;
		ArrayList<Point> neighbors = new ArrayList<Point>(size * size + 1);

		for (int y = yPos - range; y <= yPos + range; y++) {
			for (int x = xPos - range; x <= xPos + range; x++) {

				if (x == xPos && y == yPos)
					continue;

				/* diagonals */
				// if (x != xPos && y != yPos)
				// continue;

				if (x >= 0 && y >= 0 && x < width && y < height)
					neighbors.add(new Point(x, y));

			}
		}

		return neighbors;
	}

	public static boolean isBlocked(MapArea map, int x, int y, boolean allowActors) {
		Tile tile = map.getTileAt(x, y);
		if (tile == null)
			return true;

		return (tile.getActor() != null && !allowActors) || !tile.canPass();
	}

	public static float distance(int x0, int y0, int x1, int y1) {
		return Math.abs(x1 - x0) + Math.abs(y1 - y0);
	}

	public static float distance(Point p1, Point p2) {
		return distance(p1.x, p1.y, p2.x, p2.y);
	}

	public static float distanceSq(int x0, int y0, int x1, int y1) {
		int a = Math.abs(x1 - x0);
		int b = Math.abs(y1 - y0);

		return (float) Math.sqrt((a * a) + (b * b));
	}

	public static Queue<Point> findPath(RNG random, Point p1, Point p2) {
		Queue<Point> points = new LinkedList<Point>();
		int xDist = p2.x - p1.x;
		int yDist = p2.y - p1.y;

		Point curPoint = (Point) p1.clone();
		while (curPoint.x != p2.x || curPoint.y != p2.y) {

			if (curPoint.x != p2.x && curPoint.y != p2.y) {
				if (random.nextBoolean()) {
					curPoint.x += Math.signum(xDist);
				} else {
					curPoint.y += Math.signum(yDist);
				}

			} else if (curPoint.x != p2.x) {
				curPoint.x += Math.signum(xDist);

			} else if (curPoint.y != p2.y) {
				curPoint.y += Math.signum(yDist);
			}

			points.add(new Point(curPoint.x, curPoint.y));
		}
		return points;
	}

	public static int getAdjacentTiles(Tile[][] map, int x, int y, Symbol tile, boolean noDiagonals) {
		int count = 0;

		ArrayList<Point> neighbors = getNeighbors(map.length, map[0].length, x, y, 1);

		for (Point p : neighbors) {
			if (map[p.x][p.y].symbol == tile.symbol()) {

				if (noDiagonals) {
					/* diagonals */
					if (x != p.x && y != p.y)
						continue;
				}

				count++;
			}
		}

		return count;
	}

	public static void constrainToRectangle(Point point, int width, int height) {
		if (point.x < 0)
			point.x = 0;
		if (point.y < 0)
			point.y = 0;
		if (point.x >= width)
			point.x = width - 1;
		if (point.y >= height)
			point.y = height - 1;
	}

	public static boolean isWithinBounds(Tile[][] map, int x, int y) {
		if (x < 0 || y < 0 || x >= map.length || y >= map[0].length)
			return false;

		return true;
	}
}

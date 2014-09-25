package roguelike.maps;

import java.awt.Point;
import java.util.ArrayList;

public class MapHelpers {
	public static ArrayList<Point> getNeighbors(MapArea map, int xPos, int yPos, int range) {
		int size = range * 2 + 1;
		ArrayList<Point> neighbors = new ArrayList<Point>(size * size + 1);

		for (int y = yPos - range; y <= yPos + range; y++) {
			for (int x = xPos - range; x <= xPos + range; x++) {

				if (x == xPos && y == yPos)
					continue;

				/* diagonals */
				// if (x != xPos && y != yPos)
				// continue;

				if (x >= 0 && y >= 0 && x < map.width() && y < map.height())
					neighbors.add(new Point(x, y));

			}
		}

		return neighbors;
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
}

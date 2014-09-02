package roguelike.maps;

import java.awt.Rectangle;
import java.util.ArrayList;

public class MapBuilder {
	TileBuilder tb = new TileBuilder();
	ArrayList<Rectangle> buildings = new ArrayList<Rectangle>();

	public void buildMap(Tile[][] map) {
		int width = map.length;
		int height = map[0].length;

		// fill in edges with walls
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (x == 0 || y == 0 || x == width - 1 || y == height - 1) {
					map[x][y] = tb.buildTile('#');
				} else {
					map[x][y] = tb.buildTile('.');
				}
			}
		}

		for (int i = 0; i < 30; i++) {
			int x = (int) Math.floor(Math.random() * width);
			int y = (int) Math.floor(Math.random() * height);

			createWater(map, x, y);
		}

		for (int i = 0; i < 30; i++) {
			int x = (int) Math.floor(Math.random() * width);
			int y = (int) Math.floor(Math.random() * height);

			createForest(map, x, y);
		}

		for (int i = 0; i < 10; i++) {
			int x = (int) Math.floor(Math.random() * width);
			int y = (int) Math.floor(Math.random() * height);

			createBuilding(map, x, y);
		}

		// // TileBuilder builder = new TileBuilder();
		// StringMapCreator mapCreator = new
		// StringMapCreator("..#...#.....TTT..T..TTTTT.....T.............TTTTTTT....#....#....#..####.###+######...");
		// for (int x = 0; x < width; x++) {
		// for (int y = 0; y < height; y++) {
		// map[x][y] = mapCreator.nextTile();
		// }
		// }

	}

	private void createBuilding(Tile[][] map, int x, int y) {
		int width = (int) Math.ceil(Math.random() * 10) + 3;
		int height = (int) Math.ceil(Math.random() * 10) + 3;

		Rectangle mapBounds = new Rectangle(0, 0, map.length, map[0].length);
		Rectangle buildingBounds = new Rectangle(x, y, width, height);
		if (!mapBounds.contains(buildingBounds))
			return;

		for (Rectangle building : buildings) {
			if (building.intersects(buildingBounds))
				return;
		}

		int doorX, doorY;
		if (Math.random() < 0.5) {
			// door on vertical axis
			doorX = Math.random() < 0.5 ? 0 : width - 1;
			doorY = (int) (Math.random() * height - 3) + 1;
		} else {
			// door on horizontal axis
			doorX = (int) (Math.random() * width - 3) + 1;
			doorY = Math.random() < 0.5 ? 0 : height - 1;
		}
		doorX += x;
		doorY += y;

		for (int bx = x; bx < buildingBounds.getMaxX(); bx++) {
			for (int by = y; by < buildingBounds.getMaxY(); by++) {
				if (bx == doorX && by == doorY) {
					map[bx][by] = tb.buildTile('+');
				} else if (bx == x || bx == buildingBounds.getMaxX() - 1 || by == y || by == buildingBounds.getMaxY() - 1) {
					map[bx][by] = tb.buildTile('#');
				} else {
					map[bx][by] = tb.buildTile('=');
				}
			}
		}

		buildings.add(buildingBounds);
	}

	private void createForest(Tile[][] map, int x, int y) {
		int width = (int) Math.ceil(Math.random() * 10);
		int height = (int) Math.ceil(Math.random() * 10);

		Rectangle mapBounds = new Rectangle(0, 0, map.length, map[0].length);
		Rectangle buildingBounds = new Rectangle(x, y, width, height);
		if (mapBounds.contains(buildingBounds)) {

			for (int bx = buildingBounds.x; bx < buildingBounds.getMaxX(); bx++) {
				for (int by = buildingBounds.y; by < buildingBounds.getMaxY(); by++) {
					map[bx][by] = tb.buildTile('T');
				}
			}
		}
	}

	private void createWater(Tile[][] map, int x, int y) {
		if (map[x][y].getSymbol() == '.') {

			createWater(map, x, y, (int) (Math.random() * 30));

		}
	}

	private void createWater(Tile[][] map, int x, int y, int recurseCount) {
		if (recurseCount <= 1)
			return;

		if (Math.random() < 0.2)
			return;

		if (map[x][y].getSymbol() == '.') {
			if (x < map.length - 1 && x > 0 && y < map[0].length - 1 && y > 0) {

				map[x][y] = tb.buildTile('~');

				createWater(map, x - 1, y, recurseCount - 1);
				createWater(map, x + 1, y, recurseCount - 1);
				createWater(map, x, y - 1, recurseCount - 1);
				createWater(map, x, y + 1, recurseCount - 1);
			}
		}
	}
}

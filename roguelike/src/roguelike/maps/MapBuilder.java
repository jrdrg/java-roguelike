package roguelike.maps;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import roguelike.Game;
import roguelike.util.Symbol;
import roguelike.util.WeightedCollection;
import squidpony.squidmath.PerlinNoise;
import squidpony.squidmath.RNG;
import squidpony.squidutility.SCollections;

public class MapBuilder extends MapBuilderBase {
	private static final long serialVersionUID = 1L;

	protected MapBuilder() {
		super("Outside");
	}

	private ArrayList<Rectangle> buildings = new ArrayList<Rectangle>();

	@Override
	public void onBuildMap(Tile[][] map) {
		int width = map.length;
		int height = map[0].length;

		// fill in edges with walls
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (x == 0 || y == 0 || x == width - 1 || y == height - 1) {
					map[x][y] = tb.buildTile(Symbol.WALL);
				} else {
					map[x][y] = tb.buildTile(Symbol.GROUND);
				}
			}
		}

		ArrayList<Point> startingPoints = createLandscape(random, map);

		// for (int i = 0; i < 60; i++) {
		// int x = (int) Math.floor(Math.random() * width);
		// int y = (int) Math.floor(Math.random() * height);
		//
		// createWater(map, x, y);
		// }
		//
		// for (int i = 0; i < 70; i++) {
		// int x = (int) Math.floor(Math.random() * width);
		// int y = (int) Math.floor(Math.random() * height);
		//
		// createForest(map, x, y);
		// }

		for (int i = 0; i < 30; i++) {
			int x = (int) Math.floor(random.between(0, width));
			int y = (int) Math.floor(random.between(0, height));

			createBuilding(map, x, y);
		}

		Point playerPos = SCollections.getRandomElement(startingPoints);

		// place stairs

		Game.current().getPlayer().setPosition(playerPos.x, playerPos.y);
	}

	private void createBuilding(Tile[][] map, int x, int y) {
		int width = (int) Math.ceil(random.between(5, 40));
		int height = (int) Math.ceil(random.between(5, 40));

		Rectangle mapBounds = new Rectangle(0, 0, map.length, map[0].length);
		Rectangle buildingBounds = new Rectangle(x, y, width, height);
		if (!mapBounds.contains(buildingBounds))
			return;

		for (Rectangle building : buildings) {
			if (building.intersects(buildingBounds))
				return;
		}

		int doorX, doorY;
		if (random.nextDouble() < 0.5) {
			// door on vertical axis
			doorX = random.nextDouble() < 0.5 ? 0 : width - 1;
			doorY = random.between(2, height - 2);
		} else {
			// door on horizontal axis
			doorX = random.between(2, width - 2);
			doorY = random.nextDouble() < 0.5 ? 0 : height - 1;
		}
		doorX += x;
		doorY += y;

		for (int bx = x; bx < buildingBounds.getMaxX(); bx++) {
			for (int by = y; by < buildingBounds.getMaxY(); by++) {
				if (bx == doorX && by == doorY) {
					map[bx][by] = tb.buildTile(Symbol.DOOR);
				} else if (bx == x || bx == buildingBounds.getMaxX() - 1 || by == y || by == buildingBounds.getMaxY() - 1) {
					map[bx][by] = tb.buildTile(Symbol.WALL);
				} else {
					map[bx][by] = tb.buildTile(Symbol.BUILDING_FLOOR);
				}
			}
		}

		buildings.add(buildingBounds);
	}

	// private void createWater(Tile[][] map, int x, int y) {
	// if (map[x][y].getSymbol() == '.') {
	//
	// createWater(map, x, y, (int) (Math.random() * 30));
	//
	// }
	// }

	// private void createWater(Tile[][] map, int x, int y, int recurseCount) {
	// if (recurseCount <= 1)
	// return;
	//
	// if (Math.random() < 0.2)
	// return;
	//
	// if (map[x][y].getSymbol() == '.') {
	// if (x < map.length - 1 && x > 0 && y < map[0].length - 1 && y > 0) {
	//
	// map[x][y] = tb.buildTile('~');
	//
	// createWater(map, x - 1, y, recurseCount - 1);
	// createWater(map, x + 1, y, recurseCount - 1);
	// createWater(map, x, y - 1, recurseCount - 1);
	// createWater(map, x, y + 1, recurseCount - 1);
	// }
	// }
	// }

	private ArrayList<Point> createLandscape(RNG rng, Tile[][] map) {
		// (1/15)(noise(x, y) + (2/15)(noise(2x, 2y) + (4/15)(noise(4x, 4y) +
		// (8/15)(noise(8x, 8y)

		int width = map.length;
		int height = map[0].length;

		float z = rng.nextFloat();
		float factor = (float) rng.between(0.005, 0.03);

		System.out.println("Z=" + z);
		System.out.println("Factor=" + factor);

		// collection whose max weight should not be greater than 100
		WeightedCollection<Symbol> tiles = new WeightedCollection<Symbol>();
		tiles.add(Symbol.WATER, -50);
		tiles.add(Symbol.TREE, 30);
		tiles.add(Symbol.GROUND, 40);
		tiles.add(Symbol.HILLS, 50);
		tiles.add(Symbol.MOUNTAIN, 90);

		ArrayList<Point> validStartingPoints = new ArrayList<Point>();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {

				float x1 = x * factor;
				float y1 = y * factor;

				// double oct1 = PerlinNoise.noise(x1, y1, z);
				double oct1 = (1 / 15f) * PerlinNoise.noise(x1, y1, z);
				double oct2 = (2 / 15f) * PerlinNoise.noise(x1 * 2, y1 * 2, z);
				double oct3 = (4 / 15f) * PerlinNoise.noise(x1 * 4, y1 * 4, z);
				double oct4 = (8 / 15f) * PerlinNoise.noise(x1 * 8, y1 * 8, z);

				double total = oct1 + oct2 + oct3 + oct4;
				// double total = oct1;

				float t = ((float) (total) * 100);

				Symbol tileChar = tiles.getItem((int) t);

				if (tileChar != Symbol.WATER) {
					validStartingPoints.add(new Point(x, y));
				}

				map[x][y] = tb.buildTile(tileChar);
			}
		}
		return validStartingPoints;
	}
}

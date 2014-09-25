package roguelike.maps;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import roguelike.Game;
import squidpony.squidmath.RNG;

public class Room {
	protected RNG random = Game.current().random();

	public Rectangle area;
	public ArrayList<Point> doors;

	public Room(Rectangle area) {
		this.area = area;
		doors = new ArrayList<Point>();
	}

	public int bottom() {
		return (int) area.getMaxY() - 1;
	}

	public int top() {
		return (int) area.getMinY();
	}

	public int left() {
		return (int) area.getMinX();
	}

	public int right() {
		return (int) area.getMaxX() - 1;
	}

	public int getRandomX() {
		return (int) random.between(area.getMinX() + 1, area.getMaxX() - 1);
	}

	public int getRandomY() {
		return (int) random.between(area.getMinY() + 1, area.getMaxY() - 1);
	}

	public void fillRoom(Tile[][] map, TileBuilder tb, char tile) {
		Rectangle rect = this.area;
		for (int x = (int) rect.getMinX() + 1; x < rect.getMaxX() - 1; x++) {
			for (int y = (int) rect.getMinY() + 1; y < rect.getMaxY() - 1; y++) {
				map[x][y] = tb.buildTile('-');
			}
		}
	}
}
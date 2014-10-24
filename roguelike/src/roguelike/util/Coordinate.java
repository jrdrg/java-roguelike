package roguelike.util;

import java.awt.Point;

import squidpony.squidgrid.util.DirectionIntercardinal;
import squidpony.squidgrid.util.RadiusStrategy;

public class Coordinate extends Point {
	private static final long serialVersionUID = 9154408085653477925L;

	public Coordinate() {
		this(0, 0);
	}

	public Coordinate(int x, int y) {
		super(x, y);
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void offsetPosition(int xAmount, int yAmount) {
		this.x += xAmount;
		this.y += yAmount;
	}

	public Coordinate createOffsetPosition(int xAmount, int yAmount) {
		return new Coordinate(x + xAmount, y + yAmount);
	}

	public Coordinate createOffsetPosition(DirectionIntercardinal direction) {
		return createOffsetPosition(direction.deltaX, direction.deltaY);
	}

	public boolean isPosition(int x, int y) {
		return this.x == x && this.y == y;
	}

	public float distance(Point other, RadiusStrategy radiusStrategy) {
		return radiusStrategy.radius(x, y, other.x, other.y);
	}
}

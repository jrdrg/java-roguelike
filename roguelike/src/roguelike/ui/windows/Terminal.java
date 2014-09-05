package roguelike.ui.windows;

import java.awt.Point;
import java.awt.Rectangle;

import squidpony.squidcolor.SColor;

public abstract class Terminal {

	protected char[][] data;
	protected Rectangle size;
	protected TerminalCursor cursor;

	public Point location() {
		return this.size.getLocation();
	}

	public Rectangle size() {
		return this.size;
	}

	public abstract Terminal getWindow(int x, int y, int width, int height);

	public abstract Terminal withColor(SColor color);

	public abstract Terminal withColor(SColor foreground, SColor background);

	public abstract Terminal write(int x, int y, String text);

	public abstract Terminal put(int x, int y, char[][] c);

	public abstract Terminal put(int x, int y, char c);

	public abstract Terminal fill(int x, int y, int width, int height);

	public abstract Terminal fill(int x, int y, int width, int height, char c);
}

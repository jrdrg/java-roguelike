package roguelike.ui.windows;

import java.awt.Point;
import java.awt.Rectangle;

import squidpony.squidcolor.SColor;

public abstract class TerminalBase {

	protected CharEx[][] data;
	protected Rectangle size;
	protected ColorPair colors;
	protected TerminalCursor cursor;

	protected TerminalChangeNotification terminalChanged;

	protected TerminalBase(TerminalChangeNotification terminalChanged) {
		setTerminalChanged(terminalChanged);
	}

	public void setTerminalChanged(TerminalChangeNotification terminalChanged) {
		if (terminalChanged == null)
			throw new IllegalArgumentException("terminal changed notification is null");

		this.terminalChanged = terminalChanged;
	}

	public Point location() {
		return this.size.getLocation();
	}

	public Rectangle size() {
		return this.size;
	}

	public abstract TerminalBase getWindow(int x, int y, int width, int height);

	public abstract TerminalBase withColor(SColor color);

	public abstract TerminalBase withColor(SColor foreground, SColor background);

	public TerminalBase write(int x, int y, String text) {
		return write(x, y, new StringEx(text, colors.foreground(), colors.background()));
	}

	public TerminalBase write(int x, int y, StringEx text) {
		CharEx[][] temp = new CharEx[text.size()][1];
		for (int i = 0; i < text.size(); i++) {
			temp[i][0] = text.get(i);
		}
		put(x, y, temp);
		return this;
	}

	public TerminalBase put(int x, int y, CharEx[][] c) {
		for (int i = 0; i < c.length; i++) {
			for (int j = 0; j < c[0].length; j++) {
				put(x + i, y + j, c[i][j]);
			}
		}
		return this;
	}

	public TerminalBase put(int x, int y, CharEx c) {
		if (cursor.put(x, y, c)) {
			terminalChanged.onChanged(x + size.x, y + size.y, c);
		}
		return this;
	}

	public TerminalBase put(int x, int y, char c) {
		CharEx ch = new CharEx(c, colors.foreground(), colors.background());
		put(x, y, ch);
		return this;
	}

	public TerminalBase fill(int x, int y, int width, int height, char c) {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				put(i + x, j + y, new CharEx(c, colors.foreground(), colors.background()));
			}
		}
		return this;
	}

	public TerminalBase fill(int x, int y, int width, int height) {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				cursor.bg(i + x, j + y);
			}
		}
		return this;
	}

}

package roguelike.ui.windows;

import java.awt.Point;
import java.awt.Rectangle;

import roguelike.util.CharEx;
import roguelike.util.StringEx;
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

	public TerminalBase cloneTerminal() {
		final TerminalBase self = this;
		TerminalBase clone = new TerminalBase(terminalChanged) {

			private TerminalBase parent = self;

			@Override
			public TerminalBase withColor(SColor foreground, SColor background) {
				return parent.withColor(foreground, background);
			}

			@Override
			public TerminalBase withColor(SColor color) {
				return parent.withColor(color);
			}

			@Override
			public TerminalBase getWindow(int x, int y, int width, int height) {
				return parent.getWindow(x, y, width, height);
			}
		};

		clone.data = new CharEx[data.length][data[0].length];
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[0].length; j++) {
				clone.data[i][j] = data[i][j];
			}
		}
		clone.size = this.size;
		clone.colors = this.colors;
		clone.cursor = this.cursor;

		return clone;
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

	public TerminalBase refresh(int x, int y, int width, int height) {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				CharEx oldChar = this.data[i + x][j + y];
				put(i + x, j + y, oldChar);
			}
		}
		return this;
	}
}

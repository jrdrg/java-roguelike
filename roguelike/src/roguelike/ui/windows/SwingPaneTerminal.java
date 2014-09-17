package roguelike.ui.windows;

import java.awt.Rectangle;
import squidpony.squidcolor.SColor;
import squidpony.squidgrid.gui.SwingPane;

public class SwingPaneTerminal extends Terminal {

	private SwingPane foreground;
	private SwingPane background;

	public SwingPaneTerminal(int width, int height, SwingPane foreground, SwingPane background, TerminalChangeNotification terminalChanged) {
		this(new Rectangle(0, 0, width, height), foreground, background, new CharEx[width][height], terminalChanged);
	}

	SwingPaneTerminal(
			final Rectangle area, final SwingPane foreground,
			final SwingPane background, final CharEx[][] data,
			final TerminalChangeNotification terminalChanged)
	{
		super(terminalChanged);

		this.colors = new ColorPair(SColor.WHITE, SColor.BLACK);
		this.foreground = foreground;
		this.background = background;
		this.size = area;
		this.data = data;
		this.cursor = new TerminalCursor() {

			@Override
			public boolean put(int x, int y, CharEx c) {
				int sx = getX(x);
				int sy = getY(y);
				if (data[sx][sy] != null && data[sx][sy].equals(c)) {
					return false;
				}
				data[sx][sy] = c;
				return true;
			}

			@Override
			public boolean bg(int x, int y) {
				int sx = getX(x);
				int sy = getY(y);
				CharEx c = data[getX(x)][getY(y)];
				CharEx c2 = new CharEx(c.symbol(), c.foregroundColor(), colors.background());
				data[sx][sy] = c2;

				terminalChanged.onChanged(sx, sy, c2);

				return true;
			}

			private int getX(int x) {
				return size.x + x;
			}

			private int getY(int y) {
				return size.y + y;
			}
		};
	}

	@Override
	public Terminal getWindow(int x, int y, int width, int height) {
		Rectangle area = new Rectangle(x, y, width, height);
		return new SwingPaneTerminal(area, foreground, background, this.data, this.terminalChanged);
	}

	@Override
	public Terminal withColor(SColor color) {
		SwingPaneTerminal term = new SwingPaneTerminal(size, foreground, background, data, this.terminalChanged);
		term.colors = new ColorPair(color);
		return term;
	}

	@Override
	public Terminal withColor(SColor fgColor, SColor bgColor) {
		SwingPaneTerminal term = new SwingPaneTerminal(size, foreground, background, data, this.terminalChanged);
		term.colors = new ColorPair(fgColor, bgColor);
		return term;
	}

	@Override
	public Terminal fill(int x, int y, int width, int height) {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				cursor.bg(i + x, j + y);
			}
		}
		return this;
	}

}

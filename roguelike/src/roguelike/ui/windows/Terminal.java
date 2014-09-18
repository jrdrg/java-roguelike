package roguelike.ui.windows;

import java.awt.Rectangle;

import roguelike.util.CharEx;
import squidpony.squidcolor.SColor;

public class Terminal extends TerminalBase {

	public Terminal(int width, int height, TerminalChangeNotification terminalChanged) {
		this(new Rectangle(0, 0, width, height), new CharEx[width][height], terminalChanged);
	}

	Terminal(
			final Rectangle area, final CharEx[][] data,
			final TerminalChangeNotification terminalChanged)
	{
		super(terminalChanged);

		this.colors = new ColorPair(SColor.WHITE, SColor.BLACK);
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
	public TerminalBase getWindow(int x, int y, int width, int height) {
		Rectangle area = new Rectangle(x, y, width, height);
		return new Terminal(area, this.data, this.terminalChanged);
	}

	@Override
	public TerminalBase withColor(SColor color) {
		Terminal term = new Terminal(size, data, this.terminalChanged);
		term.colors = new ColorPair(color);
		return term;
	}

	@Override
	public TerminalBase withColor(SColor fgColor, SColor bgColor) {
		Terminal term = new Terminal(size, data, this.terminalChanged);
		term.colors = new ColorPair(fgColor, bgColor);
		return term;
	}

}

package roguelike.ui.windows;

import java.awt.Rectangle;

import squidpony.squidcolor.SColor;
import squidpony.squidgrid.gui.SwingPane;

public class SwingPaneTerminal extends Terminal {

	private SwingPane foreground;
	private SwingPane background;
	private SColor fgColor;
	private SColor bgColor;
	private TerminalChangeNotification terminalChanged;

	public SwingPaneTerminal(int width, int height, SwingPane foreground, SwingPane background, TerminalChangeNotification terminalChanged) {
		this(new Rectangle(0, 0, width, height), foreground, background, new char[width][height], terminalChanged);
	}

	SwingPaneTerminal(
			final Rectangle area, final SwingPane foreground,
			final SwingPane background, final char[][] data,
			final TerminalChangeNotification terminalChanged)
	{
		this.terminalChanged = terminalChanged;
		this.fgColor = SColor.WHITE;
		this.bgColor = SColor.BLACK;
		this.foreground = foreground;
		this.background = background;
		this.size = area;
		this.data = data;
		this.cursor = new TerminalCursor() {

			@Override
			public TerminalCursor put(int x, int y, char c) {
				data[size.x + x][size.y + y] = c;
				// if (c != ' ') {
				foreground.put(size.x + x, size.y + y, c, fgColor);
				background.put(size.x + x, size.y + y, bgColor);
				// } else {
				// foreground.clear(x, y);
				// background.put(size.x + x, size.y + y, bgColor);
				// }

				terminalChanged.onChanged();
				return this;
			}

			@Override
			public TerminalCursor put(int xPos, int yPos, char[][] c) {
				for (int i = 0; i < c.length; i++) {
					for (int j = 0; j < c[0].length; j++) {
						this.put(xPos + i, yPos + j, c[i][j]);
					}
				}
				return this;
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
		term.fgColor = color;
		return term;
	}

	@Override
	public Terminal withColor(SColor fgColor, SColor bgColor) {
		SwingPaneTerminal term = new SwingPaneTerminal(size, foreground, background, data, this.terminalChanged);
		term.fgColor = fgColor;
		term.bgColor = bgColor;
		return term;
	}

	@Override
	public Terminal write(int x, int y, String text) {
		char[][] temp = new char[text.length()][1];
		for (int i = 0; i < text.length(); i++) {
			temp[i][0] = text.charAt(i);
		}
		cursor.put(x, y, temp);
		return this;
	}

	@Override
	public Terminal put(int x, int y, char[][] c) {
		cursor.put(x, y, c);
		return this;
	}

	@Override
	public Terminal put(int x, int y, char c) {
		cursor.put(x, y, c);
		return this;
	}
}

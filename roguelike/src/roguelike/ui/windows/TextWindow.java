package roguelike.ui.windows;

import java.awt.Rectangle;

import roguelike.util.Symbol;

public class TextWindow {

	private static Symbol[] doubleLines = new Symbol[] {
			Symbol.BOX_TOP_LEFT_DOUBLE, Symbol.BOX_BOTTOM_LEFT_DOUBLE, Symbol.BOX_TOP_RIGHT_DOUBLE, Symbol.BOX_BOTTOM_RIGHT_DOUBLE,
			Symbol.BOX_TOP_DOUBLE, Symbol.BOX_LEFT_DOUBLE };
	private static Symbol[] singleLines = new Symbol[] {
			Symbol.BOX_TOP_LEFT_SINGLE, Symbol.BOX_BOTTOM_LEFT_SINGLE, Symbol.BOX_TOP_RIGHT_SINGLE, Symbol.BOX_BOTTOM_RIGHT_SINGLE,
			Symbol.BOX_TOP_SINGLE, Symbol.BOX_LEFT_SINGLE };

	protected Rectangle size;

	protected TextWindow(int width, int height) {
		this.size = new Rectangle(0, 0, width, height);
	}

	protected void drawBoxShape(TerminalBase term) {
		drawBoxShape(term, 0, size.height, false);
	}

	protected void drawBoxShape(TerminalBase term, int top, int height, boolean doubleLines) {
		int width = size.width;

		TextWindow.drawBoxShape(term, top, height, width, doubleLines);
	}

	public static void drawBoxShape(TerminalBase term, int top, int height, int width, boolean doubleLines) {
		Symbol[] lines = doubleLines ? TextWindow.doubleLines : TextWindow.singleLines;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int sY = top + y;

				if (y == 0 || y == height - 1) {
					if (x == 0) {
						if (y == 0)
							// term.put(x, sY, Symbol.BOX_TOP_LEFT_DOUBLE.symbol());
							term.put(x, sY, lines[0].symbol());
						else
							// term.put(x, sY, Symbol.BOX_BOTTOM_LEFT_DOUBLE.symbol());
							term.put(x, sY, lines[1].symbol());

					} else if (x == width - 1) {
						if (y == 0)
							// term.put(x, sY, Symbol.BOX_TOP_RIGHT_DOUBLE.symbol());
							term.put(x, sY, lines[2].symbol());
						else
							// term.put(x, sY, Symbol.BOX_BOTTOM_RIGHT_DOUBLE.symbol());
							term.put(x, sY, lines[3].symbol());
					}
					else {
						// term.put(x, sY, Symbol.BOX_TOP_DOUBLE.symbol());
						term.put(x, sY, lines[4].symbol());
					}
				}
				else if (x == 0 || x == width - 1) {
					// term.put(x, sY, Symbol.BOX_LEFT_DOUBLE.symbol());
					term.put(x, sY, lines[5].symbol());
				}
			}
		}
	}

}

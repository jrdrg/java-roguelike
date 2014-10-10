package roguelike.ui.windows;

import java.awt.Rectangle;

import roguelike.util.Symbol;

public class TextWindow {

	protected Rectangle size;

	protected TextWindow(int width, int height) {
		this.size = new Rectangle(0, 0, width, height);
	}

	protected void drawBoxShape(TerminalBase term) {
		drawBoxShape(term, 0, size.height);
	}

	protected void drawBoxShape(TerminalBase term, int top, int height) {
		int width = size.width;

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int sY = top + y;

				if (y == 0 || y == height - 1) {
					if (x == 0) {
						if (y == 0)
							term.put(x, sY, Symbol.BOX_TOP_LEFT_SINGLE.symbol());
						else
							term.put(x, sY, Symbol.BOX_BOTTOM_LEFT_SINGLE.symbol());

					} else if (x == width - 1) {
						if (y == 0)
							term.put(x, sY, Symbol.BOX_TOP_RIGHT_SINGLE.symbol());
						else
							term.put(x, sY, Symbol.BOX_BOTTOM_RIGHT_SINGLE.symbol());
					}
					else {
						term.put(x, sY, Symbol.BOX_TOP_SINGLE.symbol());
					}
				}
				else if (x == 0 || x == width - 1) {
					term.put(x, sY, Symbol.BOX_LEFT_SINGLE.symbol());
				}
			}
		}
	}

}

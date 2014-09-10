package roguelike.ui.windows;

import java.awt.Point;
import java.awt.Rectangle;

public abstract class Dialog {
	// Box drawing tiles: "┻┗┛┫┳┣┃━┏┓╋"

	protected Rectangle size;
	protected Terminal terminal;

	protected Dialog(int width, int height) {
		this.size = new Rectangle(0, 0, width, height);
	}

	public void showInPane(Terminal terminal) {

		int width = terminal.size.width;
		int height = terminal.size.height;

		int x = (int) ((width / 2f) - (size.width / 2f));
		int y = (int) ((height / 2f) - (size.height / 2f));

		size.setLocation(x, y);

		this.terminal = terminal.getWindow(x, y, width, height);
	}

	public final void draw() {
		if (terminal == null)
			return;

		onDraw();
	}

	public Point getLocation() {
		return size.getLocation();
	}

	protected void drawBoxShape(Terminal term) {
		int width = size.width;
		int height = size.height;

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (y == 0 || y == height - 1)
					term.put(x, y, '-');
				else if (x == 0 || x == width - 1)
					term.put(x, y, '|');
			}
		}
	}

	protected abstract void onDraw();
}

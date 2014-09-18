package roguelike.ui.windows;

import java.awt.Point;
import java.awt.Rectangle;

import roguelike.ui.InputManager;
import roguelike.ui.KeyMap;
import roguelike.ui.Menu;

public abstract class Dialog<T> {
	// Box drawing tiles: "┻┗┛┫┳┣┃━┏┓╋"

	protected Rectangle size;
	protected TerminalBase terminal;

	protected Dialog(int width, int height) {
		this.size = new Rectangle(0, 0, width, height);
	}

	public void showInPane(TerminalBase terminal) {

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

	protected void drawBoxShape(TerminalBase term) {
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

	public KeyMap getKeyBindings() {
		return Menu.KeyBindings;
	}

	public final DialogResult<T> result() {
		DialogResult<T> result = onResult();

		if (result != null) {
			InputManager.previousKeyMap();
		}

		return result;
	}

	protected abstract DialogResult<T> onResult();

	protected abstract void onDraw();
}

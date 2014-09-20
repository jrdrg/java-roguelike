package roguelike;

import java.awt.Point;
import java.awt.Rectangle;

import roguelike.ui.InputCommand;
import roguelike.ui.InputManager;
import roguelike.ui.KeyMap;
import roguelike.ui.Menu;
import roguelike.ui.windows.TerminalBase;

public abstract class Dialog<T> {
	// Box drawing tiles: "┻┗┛┫┳┣┃━┏┓╋"

	private boolean isOpen;

	protected Rectangle size;
	protected TerminalBase terminal;

	private DialogResult<T> result;

	protected Dialog(int width, int height) {
		this.size = new Rectangle(0, 0, width, height);
		this.isOpen = false;
	}

	public Point getLocation() {
		return size.getLocation();
	}

	public boolean waitingForResult() {
		return isOpen;
	}

	public final void showInPane(TerminalBase terminal) {

		int width = terminal.size().width;
		int height = terminal.size().height;

		Point loc = getLocation(width, height);
		size.setLocation(loc);

		this.terminal = terminal.getWindow(loc.x, loc.y, width, height);
	}

	public final void draw() {
		if (terminal == null)
			return;

		onDraw();
	}

	public final void show() {
		if (Game.current().setActiveDialog(this)) {
			InputManager.setActiveKeybindings(this.getKeyBindings());
			isOpen = true;
			onShow();
		}
	}

	public final boolean process() {
		InputCommand nextCommand = InputManager.nextCommandPreserveKeyData();
		DialogResult<T> result = onProcess(nextCommand);

		if (result != null) {
			InputManager.previousKeyMap();
			isOpen = false;
			Game.current().setActiveDialog(null);
		}
		this.result = result;

		return !isOpen;
	}

	public final DialogResult<T> result() {
		return this.result;
	}

	protected void drawBoxShape(TerminalBase term) {
		int width = size.width;
		int height = size.height;

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (y == 0 || y == height - 1)
					term.put(x, y, '-');
				else if (x == 0 || x == width - 1)
					term.put(x, y, '│');
			}
		}
	}

	protected Point getLocation(int width, int height) {
		int x = (int) ((width / 2f) - (size.width / 2f));
		int y = (int) ((height / 2f) - (size.height / 2f));
		return new Point(x, y);
	}

	protected KeyMap getKeyBindings() {
		return Menu.KeyBindings;
	}

	protected void onShow() {
	}

	protected abstract DialogResult<T> onProcess(InputCommand command);

	protected abstract void onDraw();
}

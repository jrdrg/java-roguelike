package roguelike;

import java.awt.Point;

import roguelike.ui.InputCommand;
import roguelike.ui.InputManager;
import roguelike.ui.KeyMap;
import roguelike.ui.Menu;
import roguelike.ui.windows.TerminalBase;
import roguelike.ui.windows.TextWindow;

public abstract class Dialog<T> extends TextWindow {
	// Box drawing tiles: "┻┗┛┫┳┣┃━┏┓╋"

	private boolean isOpen;
	private DialogResult<T> result;

	private boolean fullScreen;

	protected TerminalBase terminal;

	protected Dialog(int width, int height) {
		this(width, height, false);
	}

	protected Dialog(int width, int height, boolean fullScreen) {
		super(width, height);
		this.isOpen = false;
		this.fullScreen = fullScreen;
	}

	public Point getLocation() {
		return size.getLocation();
	}

	public boolean showFullscreen() {
		return fullScreen;
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
		InputManager.setActiveKeybindings(this.getKeyBindings());
		isOpen = true;
		onShow();
	}

	public final boolean process() {
		InputCommand nextCommand = InputManager.nextCommandPreserveKeyData();
		DialogResult<T> result = onProcess(nextCommand);

		if (result != null) {
			InputManager.previousKeyMap();
			isOpen = false;
		}
		this.result = result;

		return !isOpen;
	}

	public final DialogResult<T> result() {
		return this.result;
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

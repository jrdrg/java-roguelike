package roguelike;

import roguelike.ui.DisplayManager;
import roguelike.ui.MainWindow;
import roguelike.ui.windows.TerminalBase;

public abstract class Screen {
	int width = MainWindow.width;
	int height = MainWindow.height;
	int outputLines = MainWindow.outputLines;

	protected TerminalBase terminal;
	private Screen nextScreen;

	protected Screen() {
	}

	protected Screen nextScreen() {
		return nextScreen;
	}

	protected final void setNextScreen(Screen screen) {
		nextScreen = screen;
		DisplayManager.instance().setDirty();
	}

	public final long drawScreen() {
		long start = System.currentTimeMillis();
		draw();
		DisplayManager.instance().refresh();
		long time = System.currentTimeMillis() - start;
		return time;
	}

	public abstract long draw();

	public abstract void process();

	public abstract Screen getScreen();
}

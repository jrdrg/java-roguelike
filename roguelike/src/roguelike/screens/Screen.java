package roguelike.screens;

import roguelike.ui.DisplayManager;
import roguelike.ui.MainWindow;
import roguelike.ui.windows.TerminalBase;

public abstract class Screen {
	final static int width = MainWindow.width;
	final static int height = MainWindow.height;

	protected TerminalBase terminal;
	private Screen nextScreen;

	protected Screen(TerminalBase terminal) {
		this.terminal = terminal;
		setNextScreen(this);
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

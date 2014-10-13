package roguelike.screens;

import roguelike.ui.DisplayManager;
import roguelike.ui.MainWindow;
import roguelike.ui.windows.TerminalBase;

public abstract class Screen {
	final static int WIDTH = MainWindow.WIDTH;
	final static int HEIGHT = MainWindow.HEIGHT;

	protected TerminalBase terminal;
	private Screen nextScreen;
	private Screen previousScreen;

	protected Screen(TerminalBase terminal) {
		this.terminal = terminal;
		setNextScreen(this);
	}

	protected Screen nextScreen() {
		return nextScreen;
	}

	protected final void setNextScreen(Screen screen) {
		setNextScreen(screen, false);
	}

	protected final void setNextScreen(Screen screen, boolean storePrevious) {
		nextScreen = screen;
		if (storePrevious) {
			nextScreen.previousScreen = this;
		}
		DisplayManager.instance().setDirty();
	}

	protected final void restorePreviousScreen() {
		if (previousScreen != null) {
			setNextScreen(previousScreen, false);
			previousScreen.nextScreen = previousScreen;
			previousScreen = null;
		}
	}

	public Screen getScreen() {
		return nextScreen();
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
}

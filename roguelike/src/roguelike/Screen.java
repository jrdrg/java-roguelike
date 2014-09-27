package roguelike;

import javax.swing.JPanel;

import roguelike.ui.DisplayManager;
import roguelike.ui.MainWindow;

public abstract class Screen {
	int width = MainWindow.width;
	int height = MainWindow.height;
	int outputLines = MainWindow.outputLines;

	private Screen nextScreen;

	protected JPanel panel;

	public Screen() {
		this.panel = new JPanel();
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

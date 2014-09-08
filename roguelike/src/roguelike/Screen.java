package roguelike;

import javax.swing.JPanel;

import roguelike.ui.MainWindow;

public abstract class Screen {
	int fontSize = MainWindow.fontSize;
	int cellWidth = MainWindow.cellWidth;
	int cellHeight = MainWindow.cellHeight;
	int width = MainWindow.width;
	int height = MainWindow.height;
	int outputLines = MainWindow.outputLines;

	protected JPanel panel;

	public Screen() {
		this.panel = new JPanel();
	}

	public abstract long draw();

	public abstract void process();

	public abstract Screen getScreen();
}

package roguelike.ui.windows;

import java.awt.Rectangle;

import squidpony.squidgrid.gui.SwingPane;

public abstract class Window {
	protected Rectangle size;

	protected Window(SwingPane pane, int width, int height) {
		this.size = new Rectangle(0, 0, width, height);
	}

	public void draw(int x, int y) {
		size.setLocation(x, y);
	}
}

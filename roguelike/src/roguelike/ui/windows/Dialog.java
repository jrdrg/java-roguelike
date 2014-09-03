package roguelike.ui.windows;

import java.awt.Rectangle;

import squidpony.squidgrid.gui.SwingPane;

public abstract class Dialog {

	protected Rectangle size;
	protected SwingPane pane;

	protected Dialog(int width, int height) {
		this.size = new Rectangle(0, 0, width, height);
	}

	public void showInPane(SwingPane pane) {
		this.pane = pane;
	}

	public final void draw(int x, int y) {
		if (pane == null)
			return;

		size.setLocation(x, y);

		onDraw();
	}

	protected abstract void onDraw();
}

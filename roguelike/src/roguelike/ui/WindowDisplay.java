package roguelike.ui;

import squidpony.squidgrid.gui.SGKeyListener;
import squidpony.squidgrid.gui.SwingPane;

public class WindowDisplay {

	private SGKeyListener keyListener;
	private SwingPane pane;

	public WindowDisplay(SwingPane pane, SGKeyListener keyListener) {
		this.pane = pane;
		this.keyListener = keyListener;
	}
}

package roguelike.ui.windows;

import roguelike.ui.DisplayManager;
import roguelike.util.CharEx;
import squidpony.squidgrid.gui.SwingPane;

public class SwingPaneTerminalView {

	private SwingPane foreground;
	private SwingPane background;

	private TerminalBase terminal;

	public SwingPaneTerminalView(TerminalBase terminal, SwingPane fgPane, SwingPane bgPane) {
		this.terminal = terminal;
		this.foreground = fgPane;
		this.background = bgPane;

		this.terminal.setTerminalChanged(new TerminalChangeNotification() {

			@Override
			public void onChanged(int x, int y, CharEx c) {

				background.put(x, y, c.backgroundColor());
				foreground.put(x, y, c.symbol(), c.foregroundColor());

			}
		});
	}
}

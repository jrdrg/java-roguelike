package roguelike.ui.windows;

import squidpony.squidgrid.gui.SwingPane;

public class SwingPaneTerminalView {

	private SwingPane foreground;
	private SwingPane background;

	private Terminal terminal;

	public SwingPaneTerminalView(Terminal terminal, SwingPane fgPane, SwingPane bgPane) {
		this.terminal = terminal;
		this.foreground = fgPane;
		this.background = bgPane;

		this.terminal.setTerminalChanged(new TerminalChangeNotification() {

			@Override
			public void onChanged(int x, int y, CharEx c) {

				foreground.put(x, y, c.symbol(), c.foregroundColor());
				background.put(x, y, c.backgroundColor());
			}
		});
	}
}

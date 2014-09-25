package roguelike;

import roguelike.ui.DisplayManager;
import roguelike.ui.InputCommand;
import roguelike.ui.InputManager;
import roguelike.ui.windows.SwingPaneTerminalView;
import roguelike.ui.windows.TerminalBase;
import roguelike.util.Log;
import squidpony.squidcolor.SColor;

public class TitleScreen extends Screen {

	private TerminalBase terminal;
	private SwingPaneTerminalView terminalView;

	public TitleScreen(TerminalBase terminal) {
		terminalView = DisplayManager.instance().getTerminalView();
		Log.debug("TitleScreen: terminal size " + terminal.size().width + "x" + terminal.size().height);

		this.terminal = terminal.withColor(SColor.WHITE, SColor.BLACK);
		this.setNextScreen(this);

		terminal.fill(0, 0, terminal.size().width, terminal.size().height, ' ');

		InputManager.setInputEnabled(true);
		DisplayManager.instance().setDirty();
	}

	@Override
	public long draw() {
		long start = System.currentTimeMillis();

		String title = "Title Screen";
		int x = (int) ((terminal.size().width / 2f) - (title.length() / 2f));

		terminal.write(x, 10, "`Yellow`Title Screen");
		terminal.write(x, 15, "<press Enter>");

		long end = System.currentTimeMillis() - start;
		return end;
	}

	@Override
	public void process() {
		InputCommand cmd = InputManager.nextCommand();
		if (cmd != null) {
			switch (cmd) {

			case CONFIRM:
				setNextScreen(new MainScreen(DisplayManager.instance().getTerminal()));
				break;

			case CANCEL:
				System.exit(0);

			default:
			}
		}
	}

	@Override
	public Screen getScreen() {
		return nextScreen();
	}
}

package roguelike;

import java.awt.event.KeyEvent;

import roguelike.ui.DisplayManager;
import roguelike.ui.InputCommand;
import roguelike.ui.InputManager;
import roguelike.ui.windows.TerminalBase;
import roguelike.util.Log;
import squidpony.squidcolor.SColor;

public class TitleScreen extends Screen {

	public TitleScreen(TerminalBase terminal) {
		DisplayManager.instance().getTerminalView();

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
		terminal.write(x, 15, "`Yellow`n) `White`New");
		terminal.write(x, 16, "`Yellow`l) `White`Load");

		long end = System.currentTimeMillis() - start;
		return end;
	}

	@Override
	public void process() {
		// TODO: change key mapping for title screen

		InputCommand cmd = InputManager.nextCommandPreserveKeyData();
		if (cmd != null) {
			switch (cmd) {

			case FROM_KEYDATA:
				switch (cmd.getKeyData()) {

				case KeyEvent.VK_L:
					setNextScreen(new MainScreen(this.terminal, loadGame()));
					break;

				case KeyEvent.VK_N:
					setNextScreen(new MainScreen(this.terminal));
					break;
				}
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

	private Game loadGame() {
		return GameLoader.instance().load();
	}
}

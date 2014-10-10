package roguelike.screens;

import roguelike.ui.InputCommand;
import roguelike.ui.InputManager;
import roguelike.ui.windows.TerminalBase;
import squidpony.squidcolor.SColor;

public class InventoryScreen extends Screen {

	private Screen previousScreen;

	public InventoryScreen(Screen previousScreen, TerminalBase terminal) {
		super(terminal);
		this.previousScreen = previousScreen;
		this.terminal = terminal;
	}

	@Override
	public long draw() {
		long start = System.currentTimeMillis();

		String title = "Title Screen";
		int x = (int) ((terminal.size().width / 2f) - (title.length() / 2f));

		TerminalBase term = terminal.getWindow(5, 5, 20, 20).withColor(SColor.BLUE);
		term.write(x, 10, "Inventory");

		long end = System.currentTimeMillis() - start;
		return end;
	}

	@Override
	public void process() {
		InputCommand cmd = InputManager.nextCommandPreserveKeyData();
		if (cmd != null) {
			switch (cmd) {
			case CANCEL:
				setNextScreen(previousScreen);
				break;

			default:
			}
		}
	}

	@Override
	public Screen getScreen() {
		return nextScreen();
	}

}

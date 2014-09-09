package roguelike;

import java.awt.event.KeyEvent;

import roguelike.ui.DisplayManager;
import roguelike.ui.InputManager;
import roguelike.ui.windows.Terminal;
import squidpony.squidcolor.SColor;

public class TitleScreen extends Screen {

	private Terminal terminal;
	private Screen nextScreen;

	public TitleScreen(Terminal terminal) {
		this.terminal = terminal.withColor(SColor.WHITE, SColor.BLACK);
		this.nextScreen = this;

		terminal.fill(0, 0, terminal.size().width, terminal.size().height, ' ');

		DisplayManager.instance().setDirty();
	}

	@Override
	public long draw() {
		long start = System.currentTimeMillis();

		String title = "Title Screen";
		int x = (int) ((terminal.size().width / 2f) - (title.length() / 2f));

		terminal.write(x, 10, "Title Screen");
		terminal.write(x, 15, "<press Enter>");

		long end = System.currentTimeMillis() - start;
		return end;
	}

	@Override
	public void process() {
		KeyEvent nextKey = InputManager.nextKey();
		if (nextKey != null) {
			switch (nextKey.getKeyCode()) {

			case KeyEvent.VK_ENTER:
				nextScreen = new MainScreen(terminal);
				break;

			case KeyEvent.VK_ESCAPE:
				System.exit(0);
			}
		}
	}

	@Override
	public Screen getScreen() {
		return nextScreen;
	}
}

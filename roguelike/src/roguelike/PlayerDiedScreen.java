package roguelike;

import roguelike.actors.Actor;
import roguelike.ui.DisplayManager;
import roguelike.ui.InputCommand;
import roguelike.ui.InputManager;
import roguelike.ui.windows.Terminal;
import squidpony.squidcolor.SColor;

public class PlayerDiedScreen extends Screen {

	private Terminal terminal;
	private Terminal fullTerminal;
	private Actor killedBy;

	public PlayerDiedScreen(Actor killedBy, Terminal terminal) {
		this.nextScreen = this;

		this.killedBy = killedBy;
		this.fullTerminal = terminal;
		this.terminal = fullTerminal.withColor(SColor.RED, SColor.BLACK);
		this.nextScreen = this;

		terminal.fill(0, 0, terminal.size().width, terminal.size().height, ' ');

		InputManager.setInputEnabled(true);
		DisplayManager.instance().setDirty();
	}

	@Override
	public long draw() {
		long start = System.currentTimeMillis();

		String title = "You died";
		// int x = (int) ((terminal.size().width / 2f) - (title.length() / 2f));
		int x = 5;

		terminal.write(x, 10, title);
		terminal.write(x, 15, "Killed by: " + killedBy.getName());

		long end = System.currentTimeMillis() - start;
		return end;
	}

	@Override
	public void process() {
		InputCommand cmd = InputManager.nextCommand();
		if (cmd != null) {
			switch (cmd) {

			case CONFIRM:
				System.out.println("Switching to TitleScreen");
				nextScreen = new TitleScreen(DisplayManager.instance().getTerminal());
				break;

			case CANCEL:
				System.exit(0);

			default:
			}
		}
	}

	@Override
	public Screen getScreen() {
		return nextScreen;
	}

}

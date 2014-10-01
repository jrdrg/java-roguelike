package roguelike;

import roguelike.actors.Actor;
import roguelike.ui.DisplayManager;
import roguelike.ui.InputCommand;
import roguelike.ui.InputManager;
import roguelike.ui.windows.TerminalBase;
import squidpony.squidcolor.SColor;

public class PlayerDiedScreen extends Screen {

	private TerminalBase fullTerminal;
	private Actor killedBy;

	public PlayerDiedScreen(Actor killedBy, TerminalBase terminal) {
		setNextScreen(this);

		this.killedBy = killedBy;
		this.fullTerminal = terminal;
		this.terminal = fullTerminal.withColor(SColor.RED, SColor.BLACK);
		setNextScreen(this);

		terminal.fill(0, 0, terminal.size().width, terminal.size().height, ' ');

		InputManager.setInputEnabled(true);
		DisplayManager.instance().setDirty();
	}

	@Override
	public long draw() {
		long start = System.currentTimeMillis();

		String title = "You died";
		int x = 5;

		terminal.write(x, 10, title);
		if (killedBy != null)
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
				setNextScreen(new TitleScreen(DisplayManager.instance().getTerminal()));
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

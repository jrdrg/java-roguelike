package roguelike.screens;

import roguelike.GameLoader;
import roguelike.ui.windows.TerminalBase;

public class NewGameScreen extends Screen {

	protected NewGameScreen(TerminalBase terminal) {
		super(terminal);
	}

	@Override
	public void onDraw() {
		terminal.fill(0, 0, terminal.size().width, terminal.size().height, ' ');
	}

	@Override
	public void process() {

		setNextScreen(new MainScreen(terminal, GameLoader.newGame()), false);

	}
}

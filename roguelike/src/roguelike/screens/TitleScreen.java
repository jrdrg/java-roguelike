package roguelike.screens;

import java.awt.event.KeyEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import roguelike.Game;
import roguelike.GameLoader;
import roguelike.ui.DisplayManager;
import roguelike.ui.InputCommand;
import roguelike.ui.InputManager;
import roguelike.ui.KeyMap;
import roguelike.ui.windows.TerminalBase;
import squidpony.squidcolor.SColor;

public class TitleScreen extends Screen {
    private static final Logger LOG = LogManager.getLogger(TitleScreen.class);

	public static KeyMap KeyBindings = new KeyMap("Menu")
			.bindKey(KeyEvent.VK_ENTER, InputCommand.CONFIRM)
			.bindKey(KeyEvent.VK_ESCAPE, InputCommand.CANCEL)
			.bindKey(KeyEvent.VK_N, InputCommand.NEW)
			.bindKey(KeyEvent.VK_L, InputCommand.LOAD)
			.bindKey(KeyEvent.VK_UP, InputCommand.UP)
			.bindKey(KeyEvent.VK_DOWN, InputCommand.DOWN)
			.bindKey(KeyEvent.VK_LEFT, InputCommand.PREVIOUS_PAGE)
			.bindKey(KeyEvent.VK_RIGHT, InputCommand.NEXT_PAGE);

	public TitleScreen(TerminalBase terminal) {
		super(terminal);

		DisplayManager.instance().getTerminalView();

		LOG.debug("TitleScreen: terminal size {} x {}", terminal.size().width, terminal.size().height);

		this.terminal = terminal.withColor(SColor.WHITE, SColor.BLACK);

		terminal.fill(0, 0, terminal.size().width, terminal.size().height, ' ');

		InputManager.setInputEnabled(true);
		InputManager.setActiveKeybindings(KeyBindings);
		DisplayManager.instance().setDirty();
	}

	@Override
	public void onDraw() {
		String title = "Title Screen";
		int x = (int) ((terminal.size().width / 2f) - (title.length() / 2f));

		terminal.write(x, 10, "`Yellow`" + title);
		terminal.write(x, 15, "`Yellow`n) `White`New");
		terminal.write(x, 16, "`Yellow`l) `White`Load");
	}

	@Override
	public void process() {
		// TODO: change key mapping for title screen

		InputCommand cmd = InputManager.nextCommandPreserveKeyData();
		if (cmd != null) {
			switch (cmd) {

			case NEW:
				setNextScreen(new NewGameScreen(terminal));
				break;

			case LOAD:
				setNextScreen(new MainScreen(this.terminal, loadGame()), false);
				break;

			case CANCEL:
				System.exit(0);

			default:
			}
		}
	}

	private Game loadGame() {
		return GameLoader.load();
	}
}

package roguelike.actions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import roguelike.Cursor;
import roguelike.actors.Actor;
import roguelike.maps.MapArea;
import roguelike.screens.LookScreen;
import roguelike.screens.Screen;
import roguelike.ui.InputCommand;
import roguelike.ui.LookCursor;
import roguelike.ui.windows.TerminalBase;

public class LookAction extends CursorInputRequiredAction<InputCommand> {
    private static final Logger LOG = LogManager.getLogger(LookAction.class);

	public LookAction(Actor actor, MapArea mapArea) {
		super(actor);
		this.usesEnergy = false;

		this.cursor = new LookCursor(actor.getPosition(), mapArea);
		showCursor(cursor);
	}

	@Override
	protected void showCursor(Cursor cursor) {
		Screen currentScreen = Screen.currentScreen();
		TerminalBase screenTerm = currentScreen.terminal().getWindow(0, 0, currentScreen.getDrawableArea().width, currentScreen.getDrawableArea().height);
		currentScreen.setNextScreen(new LookScreen(screenTerm, (LookCursor) cursor, r -> result = r));
	}

	@Override
	protected ActionResult onPerform() {
		LOG.debug("LookAction");
		return ActionResult.success();
	}
}

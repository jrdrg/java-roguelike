package roguelike.actions;

import roguelike.Cursor;
import roguelike.actors.Actor;
import roguelike.maps.MapArea;
import roguelike.screens.LookScreen;
import roguelike.screens.Screen;
import roguelike.ui.InputCommand;
import roguelike.ui.LookCursor;
import roguelike.ui.windows.TerminalBase;
import roguelike.util.Log;

public class LookAction extends CursorInputRequiredAction<InputCommand> {

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
		Log.debug("LookAction");
		return ActionResult.success();
	}
}

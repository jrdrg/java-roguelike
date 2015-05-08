package roguelike.actions;

import roguelike.Cursor;
import roguelike.CursorResult;
import roguelike.actors.Actor;
import roguelike.screens.CursorScreen;
import roguelike.screens.Screen;

public abstract class CursorInputRequiredAction<T> extends Action {

	protected Cursor cursor;
	protected CursorResult result;

	protected CursorInputRequiredAction(Actor actor) {
		super(actor);
	}

	@Override
	public boolean checkForIncomplete() {
		if (cursor != null && cursor.waitingForResult())
			return true;

		return false;
	}

	protected void showCursor(Cursor cursor) {
		Screen currentScreen = Screen.currentScreen();
		currentScreen.setNextScreen(new CursorScreen(currentScreen.terminal(), cursor, r -> result = r));
	}
}

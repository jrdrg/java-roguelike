package roguelike.actions;

import roguelike.Dialog;
import roguelike.DialogResult;
import roguelike.actors.Actor;
import roguelike.screens.DialogScreen;
import roguelike.screens.Screen;

public abstract class DialogInputRequiredAction<T> extends CursorInputRequiredAction<T> {

	protected Dialog<T> dialog;
	protected DialogResult<T> result;

	protected DialogInputRequiredAction(Actor actor) {
		super(actor);
	}

	@Override
	public boolean checkForIncomplete() {
		if (dialog != null && dialog.waitingForResult())
			return true;

		return super.checkForIncomplete();
	}

	protected final void showDialog(Dialog<T> dialog) {
		Screen currentScreen = Screen.currentScreen();
		currentScreen.setNextScreen(new DialogScreen<T>(currentScreen.terminal(), dialog, r -> result = r));
	}
}

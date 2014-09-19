package roguelike.actions;

import roguelike.Cursor;
import roguelike.Dialog;
import roguelike.actors.Actor;

public abstract class InputRequiredAction<T> extends Action {

	protected Dialog<T> dialog;
	protected Cursor cursor;

	protected InputRequiredAction(Actor actor) {
		super(actor);
	}

	@Override
	public boolean checkForIncomplete() {
		if (cursor != null && cursor.waitingForResult())
			return true;

		if (dialog != null && dialog.waitingForResult())
			return true;

		return false;
	}

}

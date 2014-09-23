package roguelike.actions;

import roguelike.DialogResult;
import roguelike.Game;
import roguelike.actors.Actor;
import roguelike.ui.InputCommand;
import roguelike.ui.windows.MessageLogWindow;

public class ShowMessagesAction extends InputRequiredAction<InputCommand> {

	public ShowMessagesAction(Actor actor) {
		super(actor);

		dialog = new MessageLogWindow(60, 30, Game.current().messages());
		dialog.show();
	}

	@Override
	protected ActionResult onPerform() {
		DialogResult<InputCommand> result = dialog.result();
		if (result != null)
			return ActionResult.success();

		return ActionResult.incomplete().setMessage("No result from show messages dialog");
	}

}

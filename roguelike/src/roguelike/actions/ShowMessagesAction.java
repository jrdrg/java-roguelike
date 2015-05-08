package roguelike.actions;

import roguelike.Game;
import roguelike.actors.Actor;
import roguelike.ui.InputCommand;
import roguelike.ui.windows.MessageLogWindow;

public class ShowMessagesAction extends DialogInputRequiredAction<InputCommand> {

	public ShowMessagesAction(Actor actor) {
		super(actor);

		this.usesEnergy = false;

		dialog = new MessageLogWindow(60, 30, Game.current().messages());
		showDialog(dialog);
	}

	@Override
	protected ActionResult onPerform() {
		if (result != null)
			return ActionResult.success();

		return ActionResult.incomplete().setMessage("No result from show messages dialog");
	}

}

package roguelike.actions;

import roguelike.Dialog;
import roguelike.DialogResult;
import roguelike.actors.Actor;
import roguelike.items.InventoryMenu;
import roguelike.items.Item;
import roguelike.ui.InputCommand;
import roguelike.ui.windows.InventoryDialog;

public class InventoryAction extends InputRequiredAction<Item> {

	private Dialog<InputCommand> itemActionDialog;

	public InventoryAction(Actor actor) {
		super(actor);

		this.usesEnergy = false;
		dialog = new InventoryDialog(new InventoryMenu(actor.inventory()));
		dialog.show();
	}

	@Override
	protected ActionResult onPerform() {

		DialogResult<Item> choice = dialog.result();
		if (choice != null) {

			ActionResult result;
			if (choice.isCanceled()) {

				result = ActionResult.failure().setMessage("Closed inventory menu");

			} else {

				Item activeItem = choice.item();
				if (activeItem != null) {
					result = ActionResult.alternate(new ChooseItemCommandAction(actor, activeItem));

				} else {
					result = ActionResult.failure().setMessage("Empty inventory...");

				}
			}
			return result;
		}
		return ActionResult.incomplete();
	}

	@Override
	public boolean checkForIncomplete() {
		boolean incomplete = super.checkForIncomplete();
		return incomplete || (itemActionDialog != null && itemActionDialog.waitingForResult());
	}

}

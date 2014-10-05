package roguelike.actions;

import roguelike.DialogResult;
import roguelike.actors.Actor;
import roguelike.items.Item;
import roguelike.items.RangedWeapon;
import roguelike.items.Equipment.ItemSlot;
import roguelike.ui.InputCommand;
import roguelike.ui.windows.ChooseItemCommandDialog;

public class ChooseItemCommandAction extends InputRequiredAction<InputCommand> {

	private Item selectedItem;

	protected ChooseItemCommandAction(Actor actor, Item selectedItem) {
		super(actor);
		this.selectedItem = selectedItem;

		this.dialog = new ChooseItemCommandDialog();
		dialog.show();
	}

	@Override
	protected ActionResult onPerform() {
		DialogResult<InputCommand> choice = dialog.result();
		if (choice != null) {

			ActionResult result;
			if (choice.isCanceled()) {

				result = ActionResult.alternate(new InventoryAction(actor));

			} else {

				InputCommand activeItem = choice.item();
				if (activeItem != null) {
					switch (activeItem) {
					case EQUIP:
						if (selectedItem instanceof RangedWeapon) {
							ItemSlot.RANGED.equipItem(actor, selectedItem);
							result = ActionResult.success().setMessage("Equipped ranged weapon: " + selectedItem.getName());
						} else {
							ItemSlot.RIGHT_ARM.equipItem(actor, selectedItem);
							result = ActionResult.success().setMessage("Selected item: " + selectedItem.getName());
						}
						break;

					default:
						result = ActionResult.failure().setMessage("No command selected");
					}

				} else {
					result = ActionResult.failure().setMessage("No command selected");

				}
			}
			return result;
		}
		return ActionResult.incomplete();
	}

}

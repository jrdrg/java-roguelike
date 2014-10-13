package roguelike.actions;

import roguelike.ScreenManager;
import roguelike.actors.Actor;
import roguelike.items.Item;
import roguelike.screens.InventoryScreen;
import roguelike.ui.DisplayManager;

public class InventoryAction extends InputRequiredAction<Item> {

	public InventoryAction(Actor actor) {
		super(actor);

		this.usesEnergy = false;
	}

	@Override
	protected ActionResult onPerform() {
		ScreenManager.setNextScreen(self -> new InventoryScreen(self, DisplayManager.instance().getTerminal()));
		return ActionResult.success();
	}

	@Override
	public boolean checkForIncomplete() {
		boolean incomplete = super.checkForIncomplete();
		return incomplete;// || (itemActionDialog != null && itemActionDialog.waitingForResult());
	}

}

package roguelike.actions;

import roguelike.actors.Actor;
import roguelike.items.Item;
import roguelike.screens.InventoryScreen;
import roguelike.screens.Screen;
import roguelike.util.Log;

public class InventoryAction extends DialogInputRequiredAction<Item> {

	public InventoryAction(Actor actor) {
		super(actor);

		this.usesEnergy = false;
		Screen screen = Screen.currentScreen();
		screen.setNextScreen(new InventoryScreen(screen, screen.terminal()));
	}

	@Override
	protected ActionResult onPerform() {
		Log.debug("InventoryAction");
		return ActionResult.success();
	}

	@Override
	public boolean checkForIncomplete() {
		boolean incomplete = super.checkForIncomplete();
		return incomplete;
	}

}

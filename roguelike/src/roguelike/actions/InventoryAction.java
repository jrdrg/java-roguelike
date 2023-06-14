package roguelike.actions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import roguelike.actors.Actor;
import roguelike.items.Item;
import roguelike.screens.InventoryScreen;
import roguelike.screens.Screen;

public class InventoryAction extends DialogInputRequiredAction<Item> {
    private static final Logger LOG = LogManager.getLogger(InventoryAction.class);
    
	public InventoryAction(Actor actor) {
		super(actor);

		this.usesEnergy = false;
		Screen screen = Screen.currentScreen();
		screen.setNextScreen(new InventoryScreen(screen, screen.terminal()));
	}

	@Override
	protected ActionResult onPerform() {
	    LOG.debug("InventoryAction");
		return ActionResult.success();
	}

	@Override
	public boolean checkForIncomplete() {
		boolean incomplete = super.checkForIncomplete();
		return incomplete;
	}

}

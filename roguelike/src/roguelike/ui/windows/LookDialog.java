package roguelike.ui.windows;

import roguelike.actors.Actor;
import roguelike.items.Inventory;
import roguelike.items.Item;
import roguelike.items.Equipment.ItemSlot;
import roguelike.maps.MapArea;
import roguelike.ui.InputManager;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

public class LookDialog extends Dialog {

	private MapArea mapArea;
	private int x;
	private int y;

	public LookDialog(MapArea mapArea, int x, int y) {
		super(50, 20);
		this.mapArea = mapArea;
		this.x = x;
		this.y = y;

		System.out.println("LookDialog created");
	}

	@Override
	protected void onDraw() {
		SColor menuBgColor = SColorFactory.asSColor(30, 30, 30);

		// Terminal border = terminal.withColor(SColor.WHITE, SColor.GRAPE_MOUSE);
		Terminal background = terminal.withColor(menuBgColor, menuBgColor);
		Terminal text = terminal.withColor(SColor.WHITE, menuBgColor);

		background.fill(0, 0, size.width, size.height, ' ');
		// border.fill(0, 0, size.width, 1, ' ');

		Actor actor = mapArea.getActorAt(x, y);
		if (actor != null) {
			text.write(2, 4, "There is a " + actor.getName() + " here.");
			text.write(3, 5, "Weapon: " + ItemSlot.RIGHT_ARM.getEquippedWeapon(actor).getDescription());
		}
		int textY = 8;
		Inventory inventory = mapArea.getItemsAt(x, y);
		text.write(2, 7, "On ground:");
		if (inventory != null && inventory.any()) {
			for (Item i : inventory.allItems()) {
				text.write(3, textY, i.getName());
				textY++;

				if (textY > 10)
				{
					text.write(2, textY, "...");
					break;
				}
			}
		}
	}

	public boolean shouldClose() {
		return InputManager.nextKey() != null;
	}
}

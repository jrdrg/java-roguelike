package roguelike.ui.windows;

import java.util.ArrayList;

import roguelike.actors.Actor;
import roguelike.actors.Statistics;
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

		ArrayList<String> textList = new ArrayList<String>();

		background.fill(0, 0, size.width, size.height, ' ');
		// border.fill(0, 0, size.width, 1, ' ');

		Actor actor = mapArea.getActorAt(x, y);
		if (actor != null) {
			textList.add(actor.getDescription());
			textList.add(" Weapon: " + ItemSlot.RIGHT_ARM.getEquippedWeapon(actor).getDescription());

			Statistics stats = actor.getStatistics();
			textList.add(String.format(" MP:%3d RP:%3d Ref:%3d Aim:%3d Spd:%3d",
					stats.baseMeleePool(0), stats.baseRangedPool(0), stats.reflexes(), stats.aiming(), stats.speed.getTotalValue()));

			textList.add(String.format(" To:%3d Co:%3d Pe:%3d Qu:%3d Wi:%3d Pr:%3d",
					stats.toughness.getTotalValue(), stats.conditioning.getTotalValue(), stats.perception.getTotalValue(),
					stats.quickness.getTotalValue(), stats.willpower.getTotalValue(), stats.presence.getTotalValue()));

			textList.add(String.format(" H:%3d", actor.getHealth().getCurrent()));
		}
		int textY = 2;
		Inventory inventory = mapArea.getItemsAt(x, y);
		textList.add("");
		textList.add("On ground:");
		if (inventory != null && inventory.any()) {
			for (Item i : inventory.allItems()) {
				textList.add(i.getName());
			}
		}

		for (int x = 0; x < textList.size(); x++) {
			text.write(2, x + textY, textList.get(x));
			if (x >= 15) {
				text.write(3, x + 1, "...");
				break;
			}
		}
	}

	public boolean shouldClose() {
		return InputManager.nextKey() != null;
	}
}

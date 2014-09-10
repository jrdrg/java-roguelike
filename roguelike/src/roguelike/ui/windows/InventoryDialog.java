package roguelike.ui.windows;

import roguelike.items.InventoryMenu;
import roguelike.items.Item;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

public class InventoryDialog extends Dialog {

	private InventoryMenu menu;

	public InventoryDialog(InventoryMenu menu) {
		super(40, 10);
		this.menu = menu;
	}

	@Override
	protected void onDraw() {
		SColor menuBgColor = SColorFactory.asSColor(30, 30, 30);

		Terminal border = terminal.withColor(SColor.WHITE, SColor.GRAPE_MOUSE);
		Terminal background = terminal.withColor(menuBgColor, menuBgColor);
		Terminal text = terminal.withColor(SColor.WHITE, menuBgColor);

		background.fill(0, 0, size.width, size.height, ' ');
		border.fill(0, 0, size.width, 1, ' ');

		// drawBoxShape(border);

		border.write(1, 0, "Inventory");

		int activeIndex = menu.getActiveItemIndex();

		int totalItems = menu.totalItems();
		int itemCount = Math.min(this.size.height - 5, totalItems);

		int offset = Math.max(0, activeIndex - (itemCount - 1));

		Terminal activeText = terminal.withColor(SColor.ALIZARIN, menuBgColor);
		for (int x = 0; x < itemCount; x++) {
			int itemIdx = x + offset;
			Item item = menu.getItemAt(itemIdx);
			if (item == null)
				break;

			Terminal t = text;
			if (itemIdx == activeIndex) {
				t = activeText;
			}
			int displayY = 3 + x;
			text.write(1, displayY, getCharForIndex(itemIdx) + ")");
			t.write(4, displayY, item.getName());
		}

		if (activeIndex < totalItems - 1 && totalItems > itemCount) {
			int remainingCount = Math.min((totalItems - activeIndex - 1), (totalItems - itemCount));
			text.write(6, itemCount + 4, "- " + remainingCount + " more -");
		}
	}

	private char getCharForIndex(int index) {
		return (char) (index + 65);
	}
}

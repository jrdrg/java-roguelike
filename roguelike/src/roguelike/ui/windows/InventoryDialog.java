package roguelike.ui.windows;

import roguelike.items.InventoryMenu;
import roguelike.items.Item;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

public class InventoryDialog extends Dialog {

	private InventoryMenu menu;

	public InventoryDialog(InventoryMenu menu) {
		super(40, 30);
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

		border.write(1, 0, "Inventory");

		int activeIndex = menu.getActiveItemIndex();

		int totalItems = menu.totalItems();
		int itemCount = Math.min(5, totalItems);

		Terminal activeText = terminal.withColor(SColor.ALIZARIN, menuBgColor);
		for (int x = 0; x < itemCount; x++) {
			Item item = menu.getItemAt(x);
			if (item == null)
				break;

			Terminal t = text;
			if (x == activeIndex) {
				t = activeText;
			}
			t.write(3, 3 + x, item.getName());
		}
	}
}

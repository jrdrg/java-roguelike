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
		int firstIndex = menu.getFirstItemIndex();
		int lastIndex = menu.getLastItemIndex();
		int currentPage = menu.getCurrentPage();
		int pageCount = menu.getPageCount();

		int totalItems = menu.totalItems();
		int itemCount = lastIndex - firstIndex;

		Terminal activeText = terminal.withColor(SColor.ALIZARIN, menuBgColor);
		for (int x = firstIndex; x < lastIndex; x++) {
			int offset = x - firstIndex;
			int itemIdx = x;
			Item item = menu.getItemAt(itemIdx);
			if (item == null)
				break;

			Terminal t = text;
			if (itemIdx == activeIndex) {
				t = activeText;
			}
			int displayY = 3 + offset;
			text.write(1, displayY, getCharForIndex(offset) + ")");
			t.write(4, displayY, item.getName());
		}

		text.write(6, itemCount + 5, String.format("%2d/%2d", currentPage, pageCount));
	}

	private char getCharForIndex(int index) {
		return (char) (index + 65);
	}
}

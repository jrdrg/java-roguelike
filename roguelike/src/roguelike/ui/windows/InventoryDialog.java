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

		TerminalBase border = terminal.withColor(SColor.WHITE, SColor.GRAPE_MOUSE);
		TerminalBase background = terminal.withColor(menuBgColor, menuBgColor);
		TerminalBase text = terminal.withColor(SColor.WHITE, menuBgColor);

		background.fill(0, 0, size.width, size.height, ' ');
		border.fill(0, 0, size.width, 1, ' ');

		// drawBoxShape(border);

		int activeIndex = menu.getActiveItemIndex();
		int firstIndex = menu.getFirstItemIndex();
		int lastIndex = menu.getLastItemIndex();
		int currentPage = menu.getCurrentPage();
		int pageCount = menu.getPageCount();

		border.write(1, 0, String.format("Inventory `Gray`(%d/%d)", currentPage, pageCount));

		TerminalBase activeText = terminal.withColor(SColor.ALIZARIN, menuBgColor);
		for (int x = firstIndex; x < lastIndex; x++) {
			int offset = x - firstIndex;
			int itemIdx = x;
			Item item = menu.getItemAt(itemIdx);
			if (item == null)
				break;

			TerminalBase t = text;
			if (itemIdx == activeIndex) {
				t = activeText;
			}
			int displayY = 3 + offset;
			text.write(1, displayY, menu.getCharForIndex(offset) + ")");
			t.write(4, displayY, item.getName());
		}
	}

}

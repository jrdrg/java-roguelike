package roguelike.ui.windows;

import roguelike.Dialog;
import roguelike.DialogResult;
import roguelike.items.InventoryMenu;
import roguelike.items.Item;
import roguelike.ui.InputCommand;
import roguelike.ui.MenuItem;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

public class InventoryDialog extends Dialog<Item> {

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

		int currentPage = menu.getCurrentPage();
		int pageCount = menu.getPageCount();

		border.write(1, 0, String.format("Inventory `Gray`(%d/%d)", currentPage, pageCount));

		// TerminalBase activeText = terminal.withColor(SColor.ALIZARIN, menuBgColor);
		int displayY = 3;
		for (MenuItem<Item> item : menu.currentPageItems()) {
			String color = "";
			if (item.isActive()) {
				color = "`Alizarin`";
			}
			text.write(1, displayY, color + item.getText());
			displayY++;
		}
	}

	@Override
	protected DialogResult<Item> onProcess(InputCommand command) {
		DialogResult<Item> result = null;

		if (command != null) {
			switch (command) {
			case CONFIRM:

				Item activeItem = menu.getActiveItem();
				if (activeItem != null) {
					result = DialogResult.ok(activeItem);

				} else {
					result = DialogResult.ok(null);

				}

			case CANCEL:
				if (result == null)
					result = DialogResult.cancel();

				break;

			default:
				menu.processCommand(command);
			}
		}
		return result;
	}

}

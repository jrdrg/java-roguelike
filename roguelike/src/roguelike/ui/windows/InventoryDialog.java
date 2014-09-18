package roguelike.ui.windows;

import roguelike.Game;
import roguelike.items.InventoryMenu;
import roguelike.items.Item;
import roguelike.ui.InputCommand;
import roguelike.ui.InputManager;
import roguelike.ui.MenuItem;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

public class InventoryDialog extends Dialog<Item> {

	private InventoryMenu menu;

	public InventoryDialog(InventoryMenu menu) {
		super(40, 10);
		this.menu = menu;
	}

	public void show() {
		Game.current().setActiveDialog(this);
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
	public DialogResult<Item> result() {
		DialogResult<Item> result = null;

		InputCommand nextCommand = InputManager.nextCommandPreserveKeyData();
		if (nextCommand != null) {
			switch (nextCommand) {
			case CONFIRM:

				Item activeItem = menu.getActiveItem();
				if (activeItem != null) {
					result = ok(activeItem);

				} else {
					result = ok(null);

				}

			case CANCEL:
				if (result == null)
					result = cancel();

				break;

			default:
				menu.processCommand(nextCommand);
			}
		}
		return result;
	}

}

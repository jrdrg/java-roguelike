package roguelike.ui.windows;

import roguelike.items.InventoryMenu;

public class InventoryDialog extends Dialog {

	private InventoryMenu menu;

	public InventoryDialog(InventoryMenu menu) {
		super(30, 10);
		this.menu = menu;
	}

	@Override
	protected void onDraw() {
		// TODO Auto-generated method stub
		
	}
}

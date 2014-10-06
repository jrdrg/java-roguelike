package roguelike.ui.windows;

import roguelike.Dialog;
import roguelike.DialogResult;
import roguelike.items.Equipment.ItemSlot;
import roguelike.ui.InputCommand;

public class EquipItemWindow extends Dialog<ItemSlot> {

	public EquipItemWindow(int width, int height) {
		super(width, height, false);
	}

	@Override
	protected DialogResult<ItemSlot> onProcess(InputCommand command) {

		return null;
	}

	@Override
	protected void onDraw() {
		// TODO Auto-generated method stub

	}

}

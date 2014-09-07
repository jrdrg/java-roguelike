package roguelike.ui;

import java.util.Arrays;

import roguelike.actors.Player;
import roguelike.items.Equipment;
import roguelike.items.Equipment.ItemSlot;
import roguelike.items.Weapon;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;
import squidpony.squidgrid.gui.SwingPane;

public class StatsDisplay {

	private SwingPane pane;
	private Player player;

	public StatsDisplay(SwingPane pane) {
		this.pane = pane;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void draw() {
		if (player == null) {
			return;
		}

		pane.put(0, 0, String.format("P:%3d,%3d", player.getPosition().x, player.getPosition().y));
		pane.put(0, 1, String.format("E:%3d", player.getEnergy().getCurrent()));

		drawHealth();
		drawEquipped();

		pane.refresh();
	}

	private void drawHealth() {

		pane.put(0, 4, "[", SColor.WHITE);
		pane.put(10, 4, "]", SColor.WHITE);

		float floatPct = player.getHealth().getCurrent() / (float) player.getHealth().getMaximum();
		int pct = (int) (floatPct * 9);

		char[] bar = new char[pct];
		Arrays.fill(bar, '*');
		String result = new String(bar);

		String blank = "         ";
		pane.put(1, 4, blank, SColor.BLACK);
		pane.put(1, 4, result, SColorFactory.blend(SColor.RED, SColor.GREEN, floatPct));

		pane.put(0, 3, String.format("H: %3d/%3d", player.getHealth().getCurrent(), player.getHealth().getMaximum()));
	}

	private void drawEquipped() {

		SColor headerColor = SColor.BLOOD;

		pane.put(0, 7, "L  ", headerColor);
		pane.put(0, 8, "R  ", headerColor);
		pane.put(0, 9, "Ar ", headerColor);

		Equipment eq = player.getEquipment();
		Weapon left = eq.getEquippedWeapon(ItemSlot.LEFT_ARM);
		Weapon right = eq.getEquippedWeapon(ItemSlot.RIGHT_ARM);

		if (left != null)
			pane.put(3, 7, String.format("%1$-15s", left.getName()));

		if (right != null)
			pane.put(3, 8, String.format("%1$-15s", right.getName()));

	}
}

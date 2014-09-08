package roguelike.ui;

import java.util.Arrays;

import roguelike.actors.Player;
import roguelike.items.Equipment;
import roguelike.items.Equipment.ItemSlot;
import roguelike.items.Weapon;
import roguelike.ui.windows.Terminal;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

public class StatsDisplay {

	private Terminal terminal;
	private Player player;

	public StatsDisplay(Terminal terminal) {
		this.terminal = terminal;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void draw() {
		if (player == null) {
			return;
		}

		terminal.write(0, 0, String.format("P:%3d,%3d", player.getPosition().x, player.getPosition().y));
		terminal.write(0, 1, String.format("E:%3d", player.getEnergy().getCurrent()));

		drawHealth();
		drawEquipped();
	}

	private void drawHealth() {
		Terminal bracketTerm = terminal.withColor(SColor.WHITE);

		bracketTerm.put(0, 4, '[');
		bracketTerm.put(10, 4, ']');

		float floatPct = player.getHealth().getCurrent() / (float) player.getHealth().getMaximum();
		int pct = (int) (floatPct * 9);

		char[] bar = new char[pct];
		Arrays.fill(bar, '*');
		String result = new String(bar);

		Terminal barTerm = terminal.withColor(SColorFactory.blend(SColor.RED, SColor.GREEN, floatPct));

		String blank = "         ";
		barTerm.withColor(SColor.BLACK).write(1, 4, blank);
		barTerm.write(1, 4, result);

		bracketTerm.write(0, 3, String.format("H: %3d/%3d", player.getHealth().getCurrent(), player.getHealth().getMaximum()));
	}

	private void drawEquipped() {

		SColor headerColor = SColor.BLOOD;
		Terminal headerTerm = terminal.withColor(headerColor);

		headerTerm.write(0, 7, "L  ");
		headerTerm.write(0, 8, "R  ");
		headerTerm.write(0, 9, "Ar ");

		Equipment eq = player.getEquipment();
		Weapon left = eq.getEquippedWeapon(ItemSlot.LEFT_ARM);
		Weapon right = eq.getEquippedWeapon(ItemSlot.RIGHT_ARM);

		if (left != null)
			terminal.write(3, 7, String.format("%1$-15s", left.getName()));

		if (right != null)
			terminal.write(3, 8, String.format("%1$-15s", right.getName()));

	}
}

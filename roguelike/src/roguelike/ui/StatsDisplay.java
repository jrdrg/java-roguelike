package roguelike.ui;

import java.util.Arrays;

import roguelike.actors.Player;
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

		System.out.println("Stats display: " + terminal.size().width + "x" + terminal.size().height);
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
		drawStats();
	}

	private void drawHealth() {
		Terminal bracketTerm = terminal.withColor(SColor.WHITE);

		bracketTerm.put(0, 4, '[');
		bracketTerm.put(10, 4, ']');

		float floatPct = player.getHealth().getCurrent() / (float) player.getHealth().getMaximum();
		int pct = Math.max(0, (int) (floatPct * 9));

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

		headerTerm.write(1, 7, "L  ");
		headerTerm.write(1, 8, "R  ");
		headerTerm.write(1, 9, "Ar ");

		Weapon left = ItemSlot.LEFT_ARM.getEquippedWeapon(player);
		Weapon right = ItemSlot.RIGHT_ARM.getEquippedWeapon(player);

		if (left != null)
			terminal.write(3, 7, String.format("%1$-15s", left.getName()));

		if (right != null)
			terminal.write(3, 8, String.format("%1$-15s", right.getName()));

		headerTerm.write(1, 14, "MP");
		int weaponProficiency = 0; // TODO: calculate this
		terminal.write(3, 14, String.format("%3d", player.getStatistics().baseMeleePool(weaponProficiency)));

		headerTerm.write(1, 15, "RP");
		int rangedProficiency = 0; // TODO: calculate this
		terminal.write(3, 15, String.format("%3d", player.getStatistics().baseMeleePool(rangedProficiency)));
	}

	private void drawStats() {

		SColor headerColor = SColor.BRONZE;
		Terminal headerTerm = terminal.withColor(headerColor);
		Terminal displayTerm = terminal;
		// toughness conditioning perception quickness willpower presence
		headerTerm.write(1, 11, "To");
		displayTerm.write(3, 11, String.format("%3d", player.getStatistics().toughness.getTotalValue()));
		headerTerm.write(7, 11, "Co");
		displayTerm.write(9, 11, String.format("%3d", player.getStatistics().conditioning.getTotalValue()));
		headerTerm.write(13, 11, "Pe");
		displayTerm.write(15, 11, String.format("%3d", player.getStatistics().perception.getTotalValue()));

		headerTerm.write(1, 12, "Qu");
		displayTerm.write(3, 12, String.format("%3d", player.getStatistics().quickness.getTotalValue()));
		headerTerm.write(7, 12, "Wi");
		displayTerm.write(9, 12, String.format("%3d", player.getStatistics().willpower.getTotalValue()));
		headerTerm.write(13, 12, "Pr");
		displayTerm.write(15, 12, String.format("%3d", player.getStatistics().presence.getTotalValue()));
	}
}

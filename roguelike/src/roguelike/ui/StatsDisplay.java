package roguelike.ui;

import java.util.Arrays;

import roguelike.actors.Player;
import roguelike.actors.Statistics;
import roguelike.items.Equipment.ItemSlot;
import roguelike.items.Weapon;
import roguelike.ui.windows.TerminalBase;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

public class StatsDisplay {

	private TerminalBase terminal;
	private Player player;

	public StatsDisplay(TerminalBase terminal) {
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

		terminal.withColor(SColor.TRANSPARENT, SColor.BLACK).fill(0, 0, terminal.size().width, terminal.size().height, ' ');

		terminal.write(0, 0, String.format("P:%3d,%3d", player.getPosition().x, player.getPosition().y));
		terminal.write(0, 1, String.format("E:%3d", player.getEnergy().getCurrent()));

		drawHealth();
		drawEquipped();
		drawStats();
	}

	private void drawHealth() {
		TerminalBase bracketTerm = terminal.withColor(SColor.WHITE);

		bracketTerm.put(0, 4, '[');
		bracketTerm.put(10, 4, ']');

		float floatPct = player.getHealth().getCurrent() / (float) player.getHealth().getMaximum();
		int pct = Math.max(0, (int) (floatPct * 9));

		char[] bar = new char[pct];
		Arrays.fill(bar, '*');
		String result = new String(bar);

		TerminalBase barTerm = terminal.withColor(SColorFactory.blend(SColor.RED, SColor.GREEN, floatPct));

		String blank = "         ";
		barTerm.withColor(SColor.BLACK).write(1, 4, blank);
		barTerm.write(1, 4, result);

		bracketTerm.write(0, 3, String.format("H: %3d/%3d", player.getHealth().getCurrent(), player.getHealth().getMaximum()));
	}

	private void drawEquipped() {

		int leftX = 4;

		SColor headerColor = SColor.BLOOD;
		TerminalBase headerTerm = terminal.withColor(headerColor);

		headerTerm.write(1, 7, "LH ");
		headerTerm.write(1, 8, "RH ");
		headerTerm.write(1, 9, "Ar  ");
		headerTerm.write(1, 10, "Rn ");

		Weapon left = ItemSlot.LEFT_ARM.getEquippedWeapon(player);
		Weapon right = ItemSlot.RIGHT_ARM.getEquippedWeapon(player);
		Weapon ranged = ItemSlot.RANGED.getEquippedWeapon(player);

		if (left != null)
			terminal.write(leftX, 7, String.format("%1$-15s", left.getName()));

		if (right != null)
			terminal.write(leftX, 8, String.format("%1$-15s", right.getName()));

		if (ranged != null)
			terminal.write(leftX, 10, String.format("%1$-15s", ranged.getName()));

		headerTerm.write(1, 15, "MP");
		int weaponProficiency = 0; // TODO: calculate this
		terminal.write(leftX, 15, String.format("%3d", player.getStatistics().baseMeleePool(weaponProficiency)));

		headerTerm.write(1, 16, "RP");
		int rangedProficiency = 0; // TODO: calculate this
		terminal.write(leftX, 16, String.format("%3d", player.getStatistics().baseMeleePool(rangedProficiency)));
	}

	private void drawStats() {

		int startY = 12;

		SColor headerColor = SColor.BRONZE;
		TerminalBase headerTerm = terminal.withColor(headerColor);
		TerminalBase displayTerm = terminal;

		Statistics statistics = player.getStatistics();

		headerTerm.write(1, startY, "To");
		displayTerm.write(3, startY, String.format("%3d", statistics.toughness.getTotalValue()));
		headerTerm.write(7, startY, "Co");
		displayTerm.write(9, startY, String.format("%3d", statistics.conditioning.getTotalValue()));
		headerTerm.write(13, startY, "Pe");
		displayTerm.write(15, startY, String.format("%3d", statistics.perception.getTotalValue()));

		startY++;

		headerTerm.write(1, startY, "Qu");
		displayTerm.write(3, startY, String.format("%3d", statistics.agility.getTotalValue()));
		headerTerm.write(7, startY, "Wi");
		displayTerm.write(9, startY, String.format("%3d", statistics.willpower.getTotalValue()));
		headerTerm.write(13, startY, "Pr");
		displayTerm.write(15, startY, String.format("%3d", statistics.presence.getTotalValue()));
	}
}

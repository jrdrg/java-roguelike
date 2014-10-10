package roguelike.ui;

import java.util.Arrays;

import roguelike.Game;
import roguelike.actors.Player;
import roguelike.actors.Statistics;
import roguelike.items.Equipment.ItemSlot;
import roguelike.items.Weapon;
import roguelike.ui.windows.TerminalBase;
import roguelike.ui.windows.TextWindow;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

public class StatsDisplay extends TextWindow {

	private final int leftMargin = 1;

	private TerminalBase terminal;
	private Player player;

	public StatsDisplay(TerminalBase terminal) {
		super(terminal.size().width, terminal.size().height);
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

		drawBoxShape(terminal.withColor(SColor.DARK_GRAY));

		// terminal.withColor(SColor.TRANSPARENT, SColor.BLACK).fill(0, 0, terminal.size().width,
		// terminal.size().height, ' ');

		terminal.write(leftMargin, 1, String.format("P:%3d,%3d", player.getPosition().x, player.getPosition().y));
		terminal.write(leftMargin, 2, String.format("E:%3d", player.energy().getCurrent()));
		terminal.write(leftMargin, 3, String.format("%s", Game.current().getCurrentMapArea().name()));

		drawHealth();
		drawEquipped();
		drawStats();
		drawConditions();
	}

	private void drawHealth() {
		TerminalBase bracketTerm = terminal.withColor(SColor.WHITE);

		int startY = 5;
		int barWidth = 20;

		bracketTerm.put(leftMargin, startY + 1, '[');
		bracketTerm.put(leftMargin + barWidth, startY + 1, ']');

		float floatPct = player.health().getCurrent() / (float) player.health().getMaximum();
		int pct = Math.max(0, (int) (floatPct * (barWidth - 1)));

		char[] bar = new char[pct];
		Arrays.fill(bar, '*');
		String result = new String(bar);

		TerminalBase barTerm = terminal.withColor(SColorFactory.blend(SColor.RED, SColor.GREEN, floatPct));

		barTerm.withColor(SColor.BLACK).fill(leftMargin + 1, startY + 1, barWidth - 1, 1, ' ');

		barTerm.write(leftMargin + 1, startY + 1, result);
		bracketTerm.write(leftMargin, startY, String.format("H: %3d/%3d", player.health().getCurrent(), player.health().getMaximum()));
	}

	private void drawEquipped() {

		int leftX = 4;
		int startY = 8;

		SColor headerColor = SColor.BLOOD;
		TerminalBase headerTerm = terminal.withColor(headerColor);

		headerTerm.write(1, startY, "LH ");
		headerTerm.write(1, startY + 1, "RH ");
		headerTerm.write(1, startY + 2, "Ar  ");
		headerTerm.write(1, startY + 3, "Rn ");

		Weapon left = ItemSlot.LEFT_ARM.getEquippedWeapon(player);
		Weapon right = ItemSlot.RIGHT_ARM.getEquippedWeapon(player);
		Weapon ranged = ItemSlot.RANGED.getEquippedWeapon(player);

		if (left != null)
			terminal.write(leftX, startY, String.format("%1$-15s", left.name()));

		if (right != null)
			terminal.write(leftX, startY + 1, String.format("%1$-15s", right.name()));

		if (ranged != null)
			terminal.write(leftX, startY + 3, String.format("%1$-15s", ranged.name()));

		headerTerm.write(leftX + 16, startY + 5, "MP");
		int weaponProficiency = 0; // TODO: calculate this
		terminal.write(leftX + 18, startY + 5, String.format("%3d", player.statistics().baseMeleePool(weaponProficiency)));

		headerTerm.write(leftX + 16, startY + 6, "RP");
		int rangedProficiency = 0; // TODO: calculate this
		terminal.write(leftX + 18, startY + 6, String.format("%3d", player.statistics().baseMeleePool(rangedProficiency)));
	}

	private void drawStats() {

		int startY = 13;

		SColor headerColor = SColor.BRONZE;
		TerminalBase headerTerm = terminal.withColor(headerColor);
		TerminalBase displayTerm = terminal;

		Statistics statistics = player.statistics();

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

	private void drawConditions() {

	}
}

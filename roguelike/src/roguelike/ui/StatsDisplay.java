package roguelike.ui;

import java.util.Arrays;

import roguelike.actors.Player;
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

		pane.put(0, 0, String.format("(%3d,%3d)", player.getPosition().x, player.getPosition().y));

		// pane.put(1, 4, String.format("Health:%3d",
		// player.getHealth().getCurrent()));
		drawHealth();
		pane.put(0, 1, String.format("E:%3d", player.getEnergy().getCurrent()));

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
}

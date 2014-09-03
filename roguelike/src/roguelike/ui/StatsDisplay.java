package roguelike.ui;

import roguelike.Game;
import roguelike.actors.Player;
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

		pane.put(0, 4, String.format("Health:%3d", player.getHealth().getCurrent()));
		pane.put(0, 5, String.format("Energy:%3d", player.getEnergy().getCurrent()));

		pane.refresh();
	}
}

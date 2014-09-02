package roguelike.actors;

import roguelike.Game;
import roguelike.actors.behaviors.RandomWalkBehavior;
import roguelike.items.InventoryBuilder;
import squidpony.squidcolor.SColor;

public class NpcBuilder {

	private Game game;
	private InventoryBuilder inventoryBuilder;

	public NpcBuilder(Game game) {
		this.game = game;
		this.inventoryBuilder = new InventoryBuilder();
	}

	public Actor buildNpc() {
		int x = (int) (Math.random() * 50);
		int y = (int) (Math.random() * 30);

		Npc npc = new Npc('f', SColor.RED, game, "Red f " + x + "," + y);

		npc.setPosition(x, y);
		npc.getStatistics().speed.setBase(10).setBonus(0);
		npc.setBehavior(new RandomWalkBehavior(npc));

		inventoryBuilder.getRandomInventory(npc);

		return npc;
	}
}

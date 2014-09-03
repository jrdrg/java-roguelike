package roguelike.actors;

import roguelike.Game;
import roguelike.actors.behaviors.RandomWalkBehavior;
import roguelike.items.InventoryBuilder;
import roguelike.items.Item;
import roguelike.items.Weapon;
import roguelike.items.Equipment.ItemSlot;
import roguelike.maps.MapArea;
import squidpony.squidcolor.SColor;

public class NpcBuilder {

	private Game game;
	private InventoryBuilder inventoryBuilder;

	public NpcBuilder(Game game) {
		this.game = game;
		this.inventoryBuilder = new InventoryBuilder();
	}

	public Actor buildNpc(MapArea map) {
		int x = (int) (Math.random() * 50);
		int y = (int) (Math.random() * 30);

		Npc npc = new Npc('b', SColor.AMETHYST, game, "Bandit " + x + "," + y);

		npc.setPosition(x, y);
		npc.getStatistics().speed.setBase(10).setBonus(0);
		npc.setBehavior(new RandomWalkBehavior(npc));

		inventoryBuilder.populateRandomInventory(npc);

		/* if we have any items, equip the first weapon */
		if (npc.getInventory().getCount() > 0) {
			Item first = npc.getInventory().getItem(0);
			if (first instanceof Weapon) {
				npc.getEquipment().equipItem(ItemSlot.RIGHT_ARM, first, npc.getInventory());
			}
		}

		return npc;
	}
}

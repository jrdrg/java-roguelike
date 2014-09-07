package roguelike.monsters;

import java.util.HashMap;
import java.util.Map;

import roguelike.Game;
import roguelike.actors.Actor;
import roguelike.actors.Npc;
import roguelike.actors.NpcBuilder;
import roguelike.actors.behaviors.RandomWalkBehavior;
import roguelike.items.InventoryBuilder;
import roguelike.items.Item;
import roguelike.items.MeleeWeapon;
import roguelike.items.Weapon;
import roguelike.items.Equipment.ItemSlot;
import squidpony.squidcolor.SColor;
import squidpony.squidmath.RNG;

public class MonsterFactory {

	private static InventoryBuilder inventoryBuilder = new InventoryBuilder();
	private Map<Character, Actor> monsterData;

	public MonsterFactory() {
		this.monsterData = new HashMap<Character, Actor>();
	}

	public static Actor createMonster(int x, int y) {
		Npc npc = getRandomMonster();

		npc.getStatistics().speed.setBase(10).setBonus(0);
		npc.setBehavior(new RandomWalkBehavior(npc));

		/* if we have any items, equip the first weapon */
		if (npc.getInventory().getCount() > 0) {
			Item first = npc.getInventory().getItem(0);
			if (first instanceof Weapon) {
				npc.getEquipment().equipItem(ItemSlot.RIGHT_ARM, first, npc.getInventory());
			}
		}

		npc.setPosition(x, y);
		return npc;
	}

	private static Npc getRandomMonster() {
		RNG rng = Game.current().random();

		int type = rng.between(0, 3);
		switch (type) {
		case 0:
			Npc bandit = new Npc('b', SColor.BRIGHT_PINK, "Bandit");
			inventoryBuilder.populateRandomInventory(bandit);
			return bandit;
		case 1:
			Npc fireAnt = new Npc('a', SColor.RED, "Fire ant");
			Weapon mandibles = new MeleeWeapon("Mandibles", "Vicious ant-like jaws", 10, "%s snaps its mandibles at %s");
			fireAnt.getInventory().add(mandibles);
			return fireAnt;
		case 2:
		default:
			Npc wolf = new Npc('w', SColor.LIGHT_GRAY, "Wolf");
			Weapon bite = new MeleeWeapon("Bite", "The bite of a wolf", 10, "%s bites %s");
			wolf.getInventory().add(bite);
			return wolf;
		}
	}

	private void initMonsterData() {

		Npc bandit = new Npc('b', SColor.BRIGHT_PINK, "Bandit");

		Npc wolf = new Npc('w', SColor.LIGHT_GRAY, "Wolf");

		Npc fireAnt = new Npc('a', SColor.RED, "Fire ant");

	}
}

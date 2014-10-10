package roguelike.items;

import roguelike.Game;
import roguelike.actors.Actor;
import squidpony.squidmath.RNG;

public class InventoryBuilder {

	public void populateRandomInventory(Actor actor) {
		RNG random = Game.current().random();
		Inventory inv = actor.inventory();
		int itemCount = (int) (random.between(1, 4));

		for (int x = 0; x < itemCount; x++) {
			double f = random.nextDouble();
			if (f < 0.1) {
				MeleeWeapon sword = (MeleeWeapon) WeaponFactory.create("sword");
				inv.add(sword);

			} else if (f < 0.3) {
				MeleeWeapon axe = (MeleeWeapon) WeaponFactory.create("axe");
				inv.add(axe);

			} else if (f < 0.7) {
				MeleeWeapon spear = (MeleeWeapon) WeaponFactory.create("spear");
				inv.add(spear);

			} else {
				MeleeWeapon dagger = (MeleeWeapon) WeaponFactory.create("dagger");
				inv.add(dagger);

			}
		}
	}
}

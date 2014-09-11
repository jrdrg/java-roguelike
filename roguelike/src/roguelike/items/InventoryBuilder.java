package roguelike.items;

import roguelike.actors.Actor;

public class InventoryBuilder {

	public void populateRandomInventory(Actor actor) {

		Inventory inv = actor.getInventory();
		int itemCount = (int) (Math.random() * 5);

		for (int x = 0; x < itemCount; x++) {
			double f = Math.random();
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

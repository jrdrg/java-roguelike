package roguelike.items;

import roguelike.actors.Actor;

public class InventoryBuilder {

	public void getRandomInventory(Actor actor) {

		Inventory inv = actor.getInventory();
		int itemCount = (int) (Math.random() * 5);

		for (int x = 0; x < itemCount; x++) {
			double f = Math.random();
			if (f < 0.1) {
				MeleeWeapon sword = new MeleeWeapon("Sword", "A large sword", 30);
				inv.add(sword);

			} else if (f < 0.3) {
				MeleeWeapon axe = new MeleeWeapon("Axe", "A battle axe", 25);
				inv.add(axe);

			} else if (f < 0.7) {
				MeleeWeapon spear = new MeleeWeapon("Spear", "A short spear", 20);
				inv.add(spear);

			} else {
				MeleeWeapon dagger = new MeleeWeapon("Dagger", "A dagger", 15);
				inv.add(dagger);

			}
		}
	}
}

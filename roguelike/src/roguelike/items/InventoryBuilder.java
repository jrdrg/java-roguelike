package roguelike.items;

import roguelike.Game;
import roguelike.actors.Actor;
import squidpony.squidmath.RNG;

public class InventoryBuilder {

	public Inventory populateRandomInventory() {
		RNG random = Game.current().random();
		Inventory inv = new Inventory();
		int itemCount = (int) (random.between(1, 4));

		for (int x = 0; x < itemCount; x++) {
			double f = random.nextDouble();
			if (f < 0.1) {
				MeleeWeapon sword = (MeleeWeapon) WeaponFactory.create(WeaponType.SHORT_SWORD);
				inv.add(sword);

			} else if (f < 0.3) {
				MeleeWeapon axe = (MeleeWeapon) WeaponFactory.create(WeaponType.AXE);
				inv.add(axe);

			} else if (f < 0.7) {
				MeleeWeapon spear = (MeleeWeapon) WeaponFactory.create(WeaponType.SPEAR);
				inv.add(spear);

			} else {
				MeleeWeapon dagger = (MeleeWeapon) WeaponFactory.create(WeaponType.DAGGER);
				inv.add(dagger);

			}
		}
		return inv;
	}

	public void populateRandomInventory(Actor actor) {
		RNG random = Game.current().random();
		Inventory inv = actor.inventory();
		int itemCount = (int) (random.between(1, 4));

		for (int x = 0; x < itemCount; x++) {
			double f = random.nextDouble();
			if (f < 0.1) {
				MeleeWeapon sword = (MeleeWeapon) WeaponFactory.create(WeaponType.SHORT_SWORD);
				inv.add(sword);

			} else if (f < 0.3) {
				MeleeWeapon axe = (MeleeWeapon) WeaponFactory.create(WeaponType.AXE);
				inv.add(axe);

			} else if (f < 0.7) {
				MeleeWeapon spear = (MeleeWeapon) WeaponFactory.create(WeaponType.SPEAR);
				inv.add(spear);

			} else {
				MeleeWeapon dagger = (MeleeWeapon) WeaponFactory.create(WeaponType.DAGGER);
				inv.add(dagger);

			}
		}
	}
}

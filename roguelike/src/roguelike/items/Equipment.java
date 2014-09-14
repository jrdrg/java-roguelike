package roguelike.items;

import java.util.HashMap;

import roguelike.actors.Actor;

public class Equipment {
	public enum ItemSlot {
		HEAD, TORSO, LEFT_ARM, RIGHT_ARM, LEGS;

		public Item getItem(Actor actor) {
			return actor.getEquipment().getEquipped(this);
		}

		public Weapon getEquippedWeapon(Actor actor) {
			return actor.getEquipment().getEquippedWeapon(this);
		}

		public Item equipItem(Actor actor, Item item) {
			return actor.getEquipment().equipItem(this, item, actor.getInventory());
		}
	}

	private HashMap<ItemSlot, Item> equipped;
	private Weapon[] equippedWeapons = new Weapon[2];

	public Equipment() {
		this.equipped = new HashMap<Equipment.ItemSlot, Item>();
	}

	public Weapon[] getEquippedWeapons() {
		Weapon right = getEquippedWeapon(ItemSlot.RIGHT_ARM);
		Weapon left = getEquippedWeapon(ItemSlot.LEFT_ARM);

		equippedWeapons[0] = right;
		equippedWeapons[1] = left;

		return equippedWeapons;
	}

	Item equipItem(ItemSlot slot, Item item, Inventory inventory) {
		Item oldItem = equipped.put(slot, item);
		if (oldItem != null) {
			// inventory.add(oldItem);
		} else {
			// inventory.remove(item);
		}

		Item existingItem = inventory.getItem(item.itemId());
		if (existingItem == null) {
			System.out.println("equipItem: Existing item=null, adding " + item.itemId());
			inventory.add(item);
		}
		// TODO: add equipped indicator to items

		return oldItem;
	}

	Item getEquipped(ItemSlot slot) {
		return equipped.getOrDefault(slot, null);
	}

	Weapon getEquippedWeapon(ItemSlot slot) {
		if (slot == ItemSlot.RIGHT_ARM || slot == ItemSlot.LEFT_ARM) {
			Item weapon = equipped.getOrDefault(slot, null);
			if (weapon instanceof Weapon)
				return (Weapon) weapon;
		}
		return null;
	}
}

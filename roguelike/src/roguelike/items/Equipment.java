package roguelike.items;

import java.util.HashMap;

public class Equipment {
	public enum ItemSlot {
		HEAD, TORSO, LEFT_ARM, RIGHT_ARM, LEGS
	}

	private HashMap<ItemSlot, Item> equipped;

	public Equipment() {
		this.equipped = new HashMap<Equipment.ItemSlot, Item>();
	}

	public Item getEquipped(ItemSlot slot) {
		return equipped.getOrDefault(slot, null);
	}

	public Weapon getEquippedWeapon(ItemSlot slot) {
		if (slot == ItemSlot.RIGHT_ARM || slot == ItemSlot.LEFT_ARM) {
			Item weapon = equipped.getOrDefault(slot, null);
			if (weapon instanceof Weapon)
				return (Weapon) weapon;
		}
		return null;
	}

	public Item equipItem(ItemSlot slot, Item item, Inventory inventory) {
		Item oldItem = equipped.put(slot, item);
		if (oldItem != null) {
			inventory.add(oldItem);
		} else {
			inventory.remove(item);
		}
		return oldItem;
	}
}

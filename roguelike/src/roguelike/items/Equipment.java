package roguelike.items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import roguelike.actors.Actor;
import roguelike.util.Log;

public class Equipment implements Serializable {
	private static final long serialVersionUID = 1006420730103267096L;

	public enum ItemSlot {
		HEAD, TORSO, LEFT_ARM, RIGHT_ARM, LEGS, RANGED, AMMUNITION;

		public Item getItem(Actor actor) {
			return actor.equipment().getEquipped(this);
		}

		public Weapon getEquippedWeapon(Actor actor) {
			return actor.equipment().getEquippedWeapon(this);
		}

		public Item equipItem(Actor actor, Item item) {
			return actor.equipment().equipItem(this, item, actor.inventory());
		}
	}

	private HashMap<ItemSlot, Item> equipped;
	private ArrayList<Weapon> equippedWeapons = new ArrayList<Weapon>();

	public Equipment() {
		this.equipped = new HashMap<Equipment.ItemSlot, Item>();
		this.equippedWeapons.add(null);
		this.equippedWeapons.add(null);
	}

	public List<Weapon> getEquippedWeapons() {
		Weapon right = getEquippedWeapon(ItemSlot.RIGHT_ARM);
		Weapon left = getEquippedWeapon(ItemSlot.LEFT_ARM);

		equippedWeapons.set(0, right);
		equippedWeapons.set(1, left);

		return equippedWeapons;
	}

	public RangedWeapon getRangedWeapon() {
		Weapon wpn = getEquippedWeapon(ItemSlot.RANGED);
		if (wpn != null) {
			if (wpn instanceof RangedWeapon)
				return (RangedWeapon) wpn;
		}
		return null;
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
			Log.debug("equipItem: Existing item=null, adding " + item.itemId());
			inventory.add(item);
		}
		// TODO: add equipped indicator to items

		return oldItem;
	}

	Item getEquipped(ItemSlot slot) {
		return equipped.getOrDefault(slot, null);
	}

	Weapon getEquippedWeapon(ItemSlot slot) {
		if (slot == ItemSlot.RIGHT_ARM || slot == ItemSlot.LEFT_ARM || slot == ItemSlot.RANGED) {
			Item weapon = equipped.getOrDefault(slot, null);
			if (weapon instanceof Weapon)
				return (Weapon) weapon;
		}
		return null;
	}
}

package roguelike.items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import roguelike.actors.Actor;

public class Equipment implements Serializable {
    private static final Logger LOG = LogManager.getLogger(Equipment.class);
    
	private static final long serialVersionUID = 1006420730103267096L;

	public enum ItemSlot {
		HEAD(1 << 0), TORSO(1 << 1), LEFT_HAND(1 << 2), RIGHT_HAND(1 << 3), LEGS(1 << 4), RANGED(1 << 5), PROJECTILE(1 << 6),
		HANDS(LEFT_HAND.value & RIGHT_HAND.value);

		public final int value;

		ItemSlot(int value) {
			this.value = value;
		}

		public Item getItem(Actor actor) {
			return actor.equipment().getEquipped(this);
		}

		public Weapon getEquippedWeapon(Actor actor) {
			return actor.equipment().getEquippedWeapon(this);
		}

		public Item equipItem(Actor actor, Item item) {
			Item old = actor.equipment().equipItem(this, item, actor.inventory());
			if (old != null) {
				old.onRemoved(actor);
			}
			item.onEquipped(actor);
			return old;
		}

		public Item removeItem(Actor actor) {
			Item item = actor.equipment().removeItem(this, actor.inventory());
			if (item != null)
				item.onRemoved(actor);
			return item;
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
		Weapon right = getEquippedWeapon(ItemSlot.RIGHT_HAND);
		Weapon left = getEquippedWeapon(ItemSlot.LEFT_HAND);

		equippedWeapons.set(0, right);
		equippedWeapons.set(1, left);

		return equippedWeapons;
	}

	public RangedWeapon getRangedWeapon() {
		Weapon wpn = getEquippedWeapon(ItemSlot.RANGED);
		if (wpn != null) {
			if (wpn.type() == ItemType.RANGED_WEAPON)
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
		    LOG.debug("equipItem: Existing item = null, adding {}", item.itemId());
			inventory.add(item);
		}
		// TODO: add equipped indicator to items

		return oldItem;
	}

	Item removeItem(ItemSlot slot, Inventory inventory) {
		Item item = equipped.getOrDefault(slot, null);
		equipped.put(slot, null);
		return item;
	}

	Item getEquipped(ItemSlot slot) {
		return equipped.getOrDefault(slot, null);
	}

	Weapon getEquippedWeapon(ItemSlot slot) {
		if (slot == ItemSlot.RIGHT_HAND || slot == ItemSlot.LEFT_HAND || slot == ItemSlot.RANGED || slot == ItemSlot.PROJECTILE) {
			Item weapon = equipped.getOrDefault(slot, null);
			if (weapon != null) {
				if (weapon.type() == ItemType.WEAPON || weapon.type() == ItemType.RANGED_WEAPON)
					return weapon.asWeapon();
				else if (weapon.type() == ItemType.PROJECTILE)
					return weapon.asProjectile();
			}
		}
		return null;
	}
}

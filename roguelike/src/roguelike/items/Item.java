package roguelike.items;

import java.io.Serializable;
import java.util.UUID;

import roguelike.actors.Actor;
import roguelike.items.Equipment.ItemSlot;
import squidpony.squidcolor.SColor;
import squidpony.squidutility.Pair;

public abstract class Item implements Serializable {
	private static final long serialVersionUID = 1L;

	private UUID itemId = UUID.randomUUID();

	protected String name;
	protected char symbol = '?';
	protected SColor color = SColor.WHITE;
	protected int weight;
	protected boolean droppable;
	protected ItemSlot equippable;

	final boolean stackable;

	protected Item(boolean stackable) {
		this.droppable = true;
		this.stackable = stackable;
	}

	public final UUID itemId() {
		return this.itemId;
	}

	public boolean isSameItem(UUID otherId) {
		return itemId() == otherId;
	}

	public abstract ItemType type();

	public String name() {
		if (name == null)
			return "???";

		return this.name;
	}

	public char symbol() {
		return this.symbol;
	}

	public SColor color() {
		return this.color;
	}

	public int weight() {
		return this.weight;
	}

	public abstract String getDescription();

	boolean isDroppable() {
		return droppable;
	}

	/**
	 * Methods to cast to a specific subtype
	 * 
	 * @return
	 */
	public Weapon asWeapon() {
		return null;
	}

	public Projectile asProjectile() {
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T> T as(Class<T> type) {
		return (T) this;
	}

	public boolean canEquip(ItemSlot slot) {
		if (equippable == null)
			return true;

		if ((slot.value & equippable.value) == slot.value) {
			return true;
		}
		return false;
	}

	public abstract boolean canUse(Actor user, Actor target);

	/**
	 * Called when the item is used, returns an item that is the result of using this one. If null, the item should be
	 * removed from inventory
	 * 
	 * @param actor
	 * @return
	 */
	public Pair<Item, Boolean> onUsed() {
		return new Pair<Item, Boolean>(this, false);
	}

	public void onEquipped(Actor actor) {
	}

	public void onRemoved(Actor actor) {
	}

	public void onThrown(Actor actor, Actor target) {
	}

	public void onAttacked(Actor actor, Actor target) {
	}

	public void onAttacking(Actor actor, Actor target) {
	}
}

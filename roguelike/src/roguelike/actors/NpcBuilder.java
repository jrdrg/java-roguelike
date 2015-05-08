package roguelike.actors;

import roguelike.functionalInterfaces.BehaviorProvider;
import roguelike.items.Equipment.ItemSlot;
import roguelike.items.Inventory;
import roguelike.items.Item;
import squidpony.squidcolor.SColor;

public class NpcBuilder {

	private Npc npc;

	private NpcBuilder(String name, char symbol, SColor color) {
		npc = new Npc(symbol, color, name);
	}

	public Npc buildNpc() {
		return npc;
	}

	public static NpcBuilder withIdentifiers(String name, char symbol, SColor color, int difficulty) {
		NpcBuilder nb = new NpcBuilder(name, symbol, color);
		nb.npc.difficulty = difficulty;
		return nb;
	}

	public NpcBuilder withDescription(String description) {
		npc.description = description;
		return this;
	}

	public NpcBuilder withVisionRadius(int visionRadius) {
		npc.visionRadius = visionRadius;
		return this;
	}

	public NpcBuilder withBehavior(BehaviorProvider behavior) {
		npc.behavior = behavior.create(npc);
		return this;
	}

	public NpcBuilder withInventory(Inventory inventory) {
		npc.inventory.allItems().addAll(inventory.allItems());
		return this;
	}

	public NpcBuilder addItem(Item item) {
		npc.inventory.add(item);
		return this;
	}

	public NpcBuilder equipItem(Item item, ItemSlot slot) {
		slot.equipItem(npc, item);
		return this;
	}

	public NpcBuilder withSpeed(int speed) {
		npc.statistics.speed.setBase(speed);
		return this;
	}

	public NpcBuilder withStats(int toughness, int conditioning, int perception, int agility, int willpower, int presence) {
		npc.statistics.toughness.setBase(toughness);
		npc.statistics.conditioning.setBase(conditioning);
		npc.statistics.perception.setBase(perception);
		npc.statistics.agility.setBase(agility);
		npc.statistics.willpower.setBase(willpower);
		npc.statistics.presence.setBase(presence);
		return this;
	}

	public NpcBuilder withHealth(int health) {
		npc.health.setMaximum(health, true);
		return this;
	}

	public NpcBuilder withReflexBonus(int reflexBonus) {
		npc.statistics.reflexBonus = reflexBonus;
		return this;
	}

	public NpcBuilder withAimingBonus(int aimingBonus) {
		npc.statistics.aimingBonus = aimingBonus;
		return this;
	}
}

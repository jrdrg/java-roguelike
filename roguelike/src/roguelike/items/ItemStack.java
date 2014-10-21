package roguelike.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import roguelike.util.Log;
import squidpony.squidutility.Pair;

public class ItemStack extends Item {
	private static final long serialVersionUID = -3935638455081216485L;

	private List<Item> items;

	private ItemStack(String name, Item item) {
		super(true);
		this.name = name;
		this.items = new ArrayList<Item>();

		items.add(item);
	}

	private ItemStack(String name, List<Item> items) {
		super(true);
		this.name = name;
		this.items = new ArrayList<Item>(items);
	}

	public static List<Item> getItemStack(List<Item> items) {
		List<Item> stacks = new ArrayList<Item>();
		items.stream()
				.filter(i -> !i.stackable)
				.forEach(i -> stacks.add(i));

		Map<Object, List<Item>> groups = items.stream()
				.flatMap(i -> (i instanceof ItemStack ? ((ItemStack) i).unstack() : Arrays.asList(i)).stream())
				.filter(i -> i.stackable)
				.collect(Collectors.groupingBy(i -> i.name));

		for (Object key : groups.keySet()) {
			List<Item> groupedItems = groups.get(key);

			stacks.add(new ItemStack(key.toString(), groupedItems));
		}
		Log.debug("Item stack count: " + stacks.size());
		return stacks;
	}

	@Override
	public Weapon asWeapon() {
		if (items.size() == 0)
			return null;
		// return items.get(0).asWeapon();
		return items.get(0).as(Weapon.class);
	}

	@Override
	public Projectile asProjectile() {
		if (items.size() == 0)
			return null;
		return items.get(0).asProjectile();
	}

	@Override
	public ItemType type() {
		if (items.size() == 0)
			return ItemType.UNDEFINED;

		return items.get(0).type();
	}

	@Override
	public String name() {
		return super.name() + " x" + items.size();
	}

	@Override
	public String getDescription() {
		if (items.size() == 0) {
			return "empty";
		}
		return items.get(0).getDescription();
	}

	@Override
	public boolean isSameItem(UUID otherId) {
		if (this.itemId().equals(otherId))
			return true;

		for (Item i : items) {
			if (i.isSameItem(otherId))
				return true;
			Log.debug("i.id=" + i.itemId() + ", other.id=" + otherId);
		}
		return false;
	}

	@Override
	public Pair<Item, Boolean> onUsed() {
		if (items.size() == 0)
			return null;

		return new Pair<Item, Boolean>(items.remove(items.size() - 1), items.size() == 0);
	}

	public List<Item> unstack() {
		return items;
	}
}

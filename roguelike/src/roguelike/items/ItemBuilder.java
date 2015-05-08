package roguelike.items;

public abstract class ItemBuilder {

	protected Item item;

	protected ItemBuilder(Item item) {
		if (item == null)
			throw new IllegalArgumentException("item cannot be null");

		this.item = item;
	}

	public ItemBuilder withDroppable(boolean droppable) {
		item.droppable = droppable;
		return this;
	}

	public ItemBuilder withWeight(int weight) {
		item.weight = weight;
		return this;
	}

}

package roguelike.ui;

import roguelike.util.StringEx;

public class MenuItem<T> {

	private StringEx text;
	private T item;
	private boolean active;

	public MenuItem(String text, T item) {
		this.text = new StringEx(text);
		this.item = item;
	}

	public MenuItem(String text, T item, boolean active) {
		this(text, item);
		this.active = active;
	}

	public MenuItem<T> setActive(boolean active)
	{
		this.active = active;
		return this;
	}

	public StringEx getText() {
		return text;
	}

	public T item() {
		return this.item;
	}

	public boolean isActive() {
		return active;
	}
}

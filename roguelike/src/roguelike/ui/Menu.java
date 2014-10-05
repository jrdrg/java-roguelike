package roguelike.ui;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import roguelike.util.StringEx;

public abstract class Menu<T> {

	public static KeyMap KeyBindings = new KeyMap("Menu")
			.bindKey(KeyEvent.VK_ENTER, InputCommand.CONFIRM)
			.bindKey(KeyEvent.VK_ESCAPE, InputCommand.CANCEL)
			.bindKey(KeyEvent.VK_UP, InputCommand.UP)
			.bindKey(KeyEvent.VK_DOWN, InputCommand.DOWN)
			.bindKey(KeyEvent.VK_LEFT, InputCommand.PREVIOUS_PAGE)
			.bindKey(KeyEvent.VK_RIGHT, InputCommand.NEXT_PAGE);

	protected ArrayList<T> items;
	private int activeIndex;
	private int currentPage;
	private int pageCount;
	private int pageSize;
	private int pageIndex;

	public Menu(List<T> items) {
		this(items, 26);
	}

	public Menu(List<T> items, int pageSize) {
		this.items = new ArrayList<T>(items);
		this.activeIndex = 0;
		this.currentPage = 1;
		this.pageSize = pageSize;

		recalculatePageCount();
	}

	public void processCommand(InputCommand command) {
		int maxItems = getLastItemIndex() - getFirstItemIndex();
		pageIndex = activeIndex % pageSize;

		if (items.size() > 0) {

			switch (command) {
			case UP:
				pageIndex = Math.max(0, pageIndex - 1);
				break;
			case DOWN:
				pageIndex = Math.min(maxItems - 1, pageIndex + 1);
				break;

			case PREVIOUS_PAGE:
				currentPage = Math.max(currentPage - 1, 1);
				break;

			case NEXT_PAGE:
				currentPage = Math.min(currentPage + 1, pageCount);
				break;

			case FROM_KEYDATA:
				pageIndex = Math.min(maxItems - 1, Math.max(0, getIndexOfChar(command.getKeyChar())));
				break;

			default:

			}

			activeIndex = Math.min(items.size() - 1, getPageOffset(pageIndex));
		}
	}

	public T getActiveItem() {
		if (activeIndex >= 0 && items.size() > 0)
			return items.get(activeIndex);

		return null;
	}

	public T getItemAt(int index) {
		if (index >= 0)
			return items.get(index);

		return null;
	}

	public int getActiveItemIndex() {
		return activeIndex;
	}

	public int getFirstItemIndex() {
		return getPageOffset(0);
	}

	public int getLastItemIndex() {
		return getPageOffset(pageSize);
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public int getPageCount() {
		return pageCount;
	}

	public int size() {
		return items.size();
	}

	public List<MenuItem<T>> currentPageItems() {
		ArrayList<MenuItem<T>> menuItems = new ArrayList<MenuItem<T>>();
		int firstIndex = getFirstItemIndex();
		int lastIndex = getLastItemIndex();

		for (int x = firstIndex; x < lastIndex; x++) {
			T item = items.get(x);
			if (item == null)
				break;

			boolean isActive = (x == activeIndex);
			menuItems.add(new MenuItem<T>(getTextFor(item, x - firstIndex), item, isActive));
		}
		return menuItems;
	}

	protected char getCharForIndex(int index) {
		return (char) (index + 97);
	}

	protected int getIndexOfChar(char keyChar) {
		if (keyChar >= 97 && keyChar <= 122)
			return keyChar - 97; // a-z, returns 0-26
		if (keyChar >= 65 && keyChar <= 90)
			return keyChar - 65; // A-Z, returns 0-26

		return -1; // invalid character pressed
	}

	protected void recalculatePageCount() {
		pageCount = (int) Math.ceil(items.size() / (float) pageSize);
	}

	private int getPageOffset(int index) {
		return Math.min(((currentPage - 1) * pageSize) + index, items.size());
	}

	protected abstract StringEx getTextFor(T item, int position);
}

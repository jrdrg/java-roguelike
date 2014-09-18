package roguelike.ui;

import java.util.ArrayList;
import java.util.List;

import com.sun.glass.events.KeyEvent;

public abstract class Menu<T> {

	public static KeyMap KeyBindings = new KeyMap("Menu")
			.bindKey(KeyEvent.VK_ENTER, InputCommand.CONFIRM)
			.bindKey(KeyEvent.VK_ESCAPE, InputCommand.CANCEL)
			.bindKey(KeyEvent.VK_UP, InputCommand.UP)
			.bindKey(KeyEvent.VK_DOWN, InputCommand.DOWN)
			.bindKey(KeyEvent.VK_LEFT, InputCommand.LEFT)
			.bindKey(KeyEvent.VK_RIGHT, InputCommand.RIGHT);

	private ArrayList<T> items;
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

		pageCount = (int) Math.ceil(items.size() / (float) pageSize);
	}

	public void processCommand(InputCommand command) {
		int maxItems = getLastItemIndex() - getFirstItemIndex() + 1;
		pageIndex = activeIndex % pageSize;

		if (items.size() > 0) {

			switch (command) {
			case UP:
				pageIndex = Math.max(0, pageIndex - 1);
				break;
			case DOWN:
				pageIndex = Math.min(maxItems - 1, pageIndex + 1);
				break;

			case LEFT:
				currentPage = Math.max(currentPage - 1, 1);
				break;

			case RIGHT:
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
		if (activeIndex >= 0)
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
		// return getFirstItemIndex() + Math.min(pageSize, items.size());
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

	public char getCharForIndex(int index) {
		return (char) (index + 65);
	}

	private int getPageOffset(int index) {
		return Math.min(((currentPage - 1) * pageSize) + index, items.size());
	}

	private int getIndexOfChar(char keyChar) {
		if (keyChar >= 97 && keyChar <= 122)
			return keyChar - 97; // a-z, returns 0-26
		if (keyChar >= 65 && keyChar <= 90)
			return keyChar - 65; // A-Z, returns 0-26

		return -1; // invalid character pressed
	}
}

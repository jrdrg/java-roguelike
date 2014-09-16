package roguelike.ui;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class KeyMap {

	private Map<Integer, InputCommand> keyBindings;
	private Map<Integer, InputCommand> shiftKeyBindings;

	private String name;

	public KeyMap(String name) {
		this.name = name;
		this.keyBindings = new HashMap<Integer, InputCommand>();
		this.shiftKeyBindings = new HashMap<Integer, InputCommand>();
	}

	public String getName() {
		return name;
	}

	public KeyMap bindKey(Integer key, InputCommand command) {
		return bindKey(key, false, command);
	}

	public KeyMap bindKey(Integer key, boolean shift, InputCommand command) {
		if (shift) {
			shiftKeyBindings.put(key, command);
		} else {
			keyBindings.put(key, command);
		}
		return this;
	}

	public InputCommand getCommand(KeyEvent key) {
		if (key == null)
			return null;

		if (key.isShiftDown()) {
			return shiftKeyBindings.getOrDefault(key, null);
		}
		else {
			return keyBindings.getOrDefault(key, null);
		}
	}
}

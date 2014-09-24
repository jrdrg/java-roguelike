package roguelike.ui;

import squidpony.squidgrid.util.DirectionIntercardinal;

public enum InputCommand {

	CANCEL,
	CONFIRM,

	LEFT,
	UP,
	RIGHT,
	DOWN,
	UP_LEFT,
	UP_RIGHT,
	DOWN_LEFT,
	DOWN_RIGHT,

	USE_STAIRS,

	NEXT_PAGE,
	PREVIOUS_PAGE,

	OPEN_DOOR,
	CLOSE_DOOR,

	INVENTORY,
	LOOK,
	SHOW_MESSAGES,

	ATTACK,
	RANGED_ATTACK,
	PREVIOUS_TARGET,
	NEXT_TARGET,

	SEARCH,
	REST,

	TALK,
	PICK_UP,

	FROM_KEYDATA;

	private int keyData;
	private char keyChar;

	InputCommand() {
		this(-999, ' ');
	}

	InputCommand(int keyData, char keyChar) {
		this.keyData = keyData;
		this.keyChar = keyChar;
	}

	public static InputCommand fromKey(int keyData, char keyChar) {
		return FROM_KEYDATA.setKeyData(keyData, keyChar);
	}

	public int getKeyData() {
		return keyData;
	}

	public char getKeyChar() {
		return keyChar;
	}

	public DirectionIntercardinal toDirection() {
		switch (this) {
		case UP:
			return DirectionIntercardinal.UP;
		case LEFT:
			return DirectionIntercardinal.LEFT;
		case RIGHT:
			return DirectionIntercardinal.RIGHT;
		case DOWN:
			return DirectionIntercardinal.DOWN;
		case UP_LEFT:
			return DirectionIntercardinal.UP_LEFT;
		case UP_RIGHT:
			return DirectionIntercardinal.UP_RIGHT;
		case DOWN_LEFT:
			return DirectionIntercardinal.DOWN_LEFT;
		case DOWN_RIGHT:
			return DirectionIntercardinal.DOWN_RIGHT;
		default:
			return DirectionIntercardinal.NONE;
		}
	}

	private InputCommand setKeyData(int keyData, char keyChar) {
		this.keyData = keyData;
		this.keyChar = keyChar;
		return this;
	}
}

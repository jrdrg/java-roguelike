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

	OPEN_DOOR,
	CLOSE_DOOR,

	INVENTORY,
	LOOK,

	ATTACK,
	SEARCH,
	REST,

	TALK,
	PICK_UP;

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
}

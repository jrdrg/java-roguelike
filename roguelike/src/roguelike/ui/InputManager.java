package roguelike.ui;

import java.awt.event.KeyEvent;

import javax.swing.JFrame;

import squidpony.squidgrid.gui.SGKeyListener;
import squidpony.squidgrid.gui.SGKeyListener.CaptureType;
import squidpony.squidgrid.util.DirectionIntercardinal;

public class InputManager {

	private static SGKeyListener keyListener = new SGKeyListener(false, CaptureType.DOWN);
	private static boolean inputReceived;
	private static boolean inputEnabled = true;
	private static KeyMap activeKeyMap = new KeyMap("Default");

	public static KeyEvent nextKey() {
		if (!inputEnabled)
			return null;

		KeyEvent key = keyListener.next();
		if (key != null) {
			DisplayManager.instance().setDirty();
		}
		return key;
	}

	public static InputCommand nextCommand() {
		return activeKeyMap.getCommand(nextKey());
	}

	public static DirectionIntercardinal nextDirection() {
		KeyEvent key = nextKey();
		return nextDirection(key);
	}

	public static DirectionIntercardinal nextDirection(KeyEvent key) {
		DirectionIntercardinal direction;
		if (key != null) {
			switch (key.getKeyCode()) {
			case KeyEvent.VK_UP:
				direction = DirectionIntercardinal.UP;
				break;

			case KeyEvent.VK_DOWN:
				direction = DirectionIntercardinal.DOWN;
				break;

			case KeyEvent.VK_LEFT:
				direction = DirectionIntercardinal.LEFT;
				break;

			case KeyEvent.VK_RIGHT:
				direction = DirectionIntercardinal.RIGHT;
				break;

			default:
				direction = DirectionIntercardinal.NONE;
				break;
			}
			return direction;
		}
		return null;
	}

	public static void registerWithFrame(JFrame frame) {
		frame.addKeyListener(keyListener);
	}

	public static boolean inputReceived() {
		boolean input = inputReceived;
		inputReceived = !inputReceived;
		return input;
	}

	public static void setInputEnabled(boolean enabled) {
		inputEnabled = enabled;
	}

	public static void setActiveKeybindings(KeyMap keyMap) {
		activeKeyMap = keyMap;
	}

	/*
	 * 
	 * potential key bindings
	 * 
	 * COMBAT
	 * 
	 * ++++++++
	 * 
	 * s: slash
	 * 
	 * t: thrust
	 * 
	 * b: blunt
	 * 
	 * a / <move into opponent>: use attack type with lowest TN of equipped weapon
	 * 
	 * r: ranged attack
	 * 
	 * S: change stance
	 * 
	 * M: change active maneuvers (attack/defense)
	 * 
	 * m: use active attack maneuver
	 * 
	 * 
	 * 
	 * MOVEMENT
	 * 
	 * ++++++++++
	 * 
	 * wasd: up/left/down/right
	 * 
	 * q: diagonal up-left
	 * 
	 * e: diagonal up-right
	 * 
	 * z: diagonal down-left
	 * 
	 * x: diagonal down-right
	 * 
	 * 
	 * OTHER
	 * 
	 * +++++++++
	 * 
	 * c: character info
	 * 
	 * i: inventory
	 * 
	 * T: talk
	 */
}

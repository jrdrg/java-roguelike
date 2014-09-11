package roguelike.ui;

import java.awt.event.KeyEvent;

import javax.swing.JFrame;

import squidpony.squidgrid.gui.SGKeyListener;
import squidpony.squidgrid.gui.SGKeyListener.CaptureType;

public class InputManager {

	private static SGKeyListener keyListener = new SGKeyListener(false, CaptureType.DOWN);
	private static boolean inputReceived;
	private static boolean inputEnabled = true;

	public static KeyEvent nextKey() {
		if (!inputEnabled)
			return null;

		KeyEvent key = keyListener.next();
		if (key != null) {
			DisplayManager.instance().setDirty();
		}
		return key;
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
	 * p: pierce
	 * 
	 * b: blunt
	 * 
	 * a / <move into opponent>: use attack type with lowest TN of equipped
	 * weapon
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
	 */
}

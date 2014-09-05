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
}

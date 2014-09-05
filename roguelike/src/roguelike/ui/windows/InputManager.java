package roguelike.ui.windows;

import java.awt.event.KeyEvent;

import javax.swing.JFrame;

import roguelike.ui.DisplayManager;
import squidpony.squidgrid.gui.SGKeyListener;
import squidpony.squidgrid.gui.SGKeyListener.CaptureType;

public class InputManager {

	private static SGKeyListener keyListener = new SGKeyListener(false, CaptureType.DOWN);
	private static boolean inputReceived;

	public static KeyEvent nextKey() {
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
}

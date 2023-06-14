package roguelike.ui;

import java.awt.event.KeyEvent;
import java.util.Stack;

import javax.swing.JFrame;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import squidpony.squidgrid.gui.SGKeyListener;
import squidpony.squidgrid.gui.SGKeyListener.CaptureType;
import squidpony.squidgrid.util.DirectionIntercardinal;

public class InputManager {
    private static final Logger LOG = LogManager.getLogger(InputManager.class);

    public static KeyMap DefaultKeyBindings;

    private static SGKeyListener keyListener = new SGKeyListener(false, CaptureType.DOWN);
    private static boolean inputReceived;
    private static boolean inputEnabled = true;
    private static KeyMap activeKeyMap = new KeyMap(".");
    private static Stack<KeyMap> keyBindings = new Stack<KeyMap>();

    private InputManager() {
    }

    public static void registerWithFrame(JFrame frame) {
        frame.addKeyListener(keyListener);
    }

    public static InputCommand nextCommand() {
        return nextCommand(nextKey(), false);
    }

    public static InputCommand nextCommandPreserveKeyData() {
        return nextCommand(nextKey(), true);
    }

    public static DirectionIntercardinal nextDirection() {
        InputCommand cmd = nextCommandPreserveKeyData();
        if (cmd == null)
            return null;

        return cmd.toDirection();
    }

    public static boolean inputReceived() {
        boolean input = inputReceived;
        inputReceived = !inputReceived;
        return input;
    }

    public static void setInputEnabled(boolean enabled) {
        inputEnabled = enabled;
    }

    public static KeyMap setActiveKeybindings(KeyMap keyMap) {
        KeyMap old = activeKeyMap;
        if (!keyMap.getName().equals(old.getName())) {
            LOG.debug("switching keyMap to {}", keyMap.getName());
            activeKeyMap = keyMap;

            if (!".".equals(old.getName()))
                keyBindings.push(old);
        }
        return old;
    }

    /**
     * Sets the active key map to the previous one in the stack.
     * 
     * @return The new active key map.
     */
    public static KeyMap previousKeyMap() {
        if (keyBindings.isEmpty())
            return null;

        activeKeyMap = keyBindings.pop();
        return activeKeyMap;
    }

    private static InputCommand nextCommand(KeyEvent key, boolean getKeyData) {
        InputCommand cmd = activeKeyMap.getCommand(key);
        if (cmd == null && key != null && getKeyData) {
            return InputCommand.fromKey(key.getKeyCode(), key.getKeyChar());
        }
        return cmd;
    }

    private static KeyEvent nextKey() {
        if (!inputEnabled)
            return null;

        KeyEvent key = keyListener.next();
        if (key != null) {
            DisplayManager.instance().setDirty();
        }
        return key;
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

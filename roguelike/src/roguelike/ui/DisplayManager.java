package roguelike.ui;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;

import javax.swing.JComponent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import roguelike.screens.TitleScreen;
import roguelike.ui.asciipanel.AsciiPanel;
import roguelike.ui.windows.AsciiPanelTerminalView;
import roguelike.ui.windows.Terminal;
import roguelike.ui.windows.TerminalBase;
import roguelike.ui.windows.TerminalChangeNotification;
import roguelike.util.CharEx;

public class DisplayManager {
    private static final Logger LOG = LogManager.getLogger(DisplayManager.class);

    // private final String FONT_NAME = "joystix monospace.ttf";
    // private final String FONT_NAME = "Adore64.ttf";
    // private final String FONT_NAME = "Commodore Pixelized v1.2.ttf";
    private final String FONT_NAME = "Nouveau_IBM.ttf";

    private final String BACKUP_FONT_NAME = "Lucidia";

    private Font font;
    private JComponent displayPane;
    private TerminalBase mainDisplay;
    private int fontSize;
    private int gridWidth;
    private int gridHeight;
    private boolean dirty;

    private AsciiPanel asciiPanel;
    private AsciiPanelTerminalView terminalView;

    private static DisplayManager self;

    public DisplayManager(int fontSize) {
        this.fontSize = fontSize;

        self = this;
    }

    public static DisplayManager instance() {
        return self;
    }

    public Font screenFont() {
        return this.font;
    }

    public JComponent displayPane() {
        return this.displayPane;
    }

    public void refresh() {
        if (dirty) {
            asciiPanel.repaint();
            dirty = false;
        }
    }

    public void setDirty() {
        dirty = true;
    }

    public TerminalBase getTerminal() {
        if (mainDisplay == null) {
            mainDisplay = new Terminal(gridWidth, gridHeight, new TerminalChangeNotification() {

                @Override
                public void onChanged(int x, int y, CharEx c) {
                    // dirty = true;
                }
            });
        }
        return mainDisplay;
    }

    public AsciiPanelTerminalView getTerminalView() {
        if (terminalView == null) {
            terminalView = new AsciiPanelTerminalView(getTerminal(), asciiPanel);
        }
        return terminalView;
    }

    public void init(int width, int height) {
        LOG.info("DisplayManager.init({}, {})", width, height);
        font = getFont(FONT_NAME);
        font = font.deriveFont((float) fontSize);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(font);

        this.gridWidth = width;
        this.gridHeight = height;

        asciiPanel = new AsciiPanel(width, height);
        displayPane = asciiPanel;
    }

    private Font getFont(String name) {
        Font font = null;
        String fName = "./assets/" + name;
        try {
            InputStream is = TitleScreen.class.getResourceAsStream("/resources/assets/" + name);
            font = Font.createFont(Font.TRUETYPE_FONT, is);

            System.out.println("Loaded " + fName);
            LOG.info("Loaded {}", fName);
        }
        catch (Exception ex) {
            ex.printStackTrace();

            LOG.error("{} not loaded; using {} font", fName, FONT_NAME);
            font = new Font(BACKUP_FONT_NAME, Font.PLAIN, 24);
        }
        return font;
    }
}

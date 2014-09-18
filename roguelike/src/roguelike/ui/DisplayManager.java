package roguelike.ui;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;

import javax.swing.JLayeredPane;

import roguelike.TitleScreen;
import roguelike.ui.windows.CharEx;
import roguelike.ui.windows.Terminal;
import roguelike.ui.windows.SwingPaneTerminalView;
import roguelike.ui.windows.TerminalBase;
import roguelike.ui.windows.TerminalChangeNotification;
import squidpony.squidgrid.gui.SwingPane;
import squidpony.squidgrid.gui.TextCellFactory;

public class DisplayManager {
	// private final String FONT_NAME = "joystix monospace.ttf";
	// private final String FONT_NAME = "Adore64.ttf";
	// private final String FONT_NAME = "Commodore Pixelized v1.2.ttf";
	private final String FONT_NAME = "Nouveau_IBM.ttf";

	private final String BACKUP_FONT_NAME = "Lucidia";
	private static final String CHARS_USED = "☃☺.,Xy#@.~M";

	private Font font;
	private JLayeredPane displayPane;
	private SwingPane foreground;
	private SwingPane background;
	private TerminalBase mainDisplay;
	private int fontSize;
	private int cellWidth;
	private int cellHeight;
	private boolean dirty;

	private SwingPaneTerminalView terminalView;

	// TODO: this is a hack, for now
	private static DisplayManager self;

	public DisplayManager(int fontSize, int cellWidth, int cellHeight) {
		this.fontSize = fontSize;
		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;

		self = this;
	}

	public static DisplayManager instance() {
		return self;
	}

	public Font screenFont() {
		return this.font;
	}

	public JLayeredPane displayPane() {
		return this.displayPane;
	}

	public void refresh() {
		if (dirty) {
			background.refresh();
			foreground.refresh();
			dirty = false;
		}
	}

	public void setDirty() {
		dirty = true;
	}

	public TerminalBase getTerminal() {
		if (mainDisplay == null) {
			mainDisplay = new Terminal(foreground.gridWidth(), foreground.gridHeight(),
					new TerminalChangeNotification() {

						@Override
						public void onChanged(int x, int y, CharEx c) {
							// dirty = true;
						}
					});
		}
		return mainDisplay;
	}

	public SwingPaneTerminalView getTerminalView() {
		if (terminalView == null) {
			terminalView = new SwingPaneTerminalView(getTerminal(), foreground, background);
		}
		return terminalView;
	}

	public void init(int width, int height) {
		font = getFont(FONT_NAME);
		font = font.deriveFont((float) fontSize);

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		ge.registerFont(font);

		TextCellFactory textFactory = new TextCellFactory(font, cellWidth, cellHeight, true, 0, CHARS_USED);
		foreground = new SwingPane(width, height, textFactory, null);
		foreground.refresh();

		background = new SwingPane(width, height, textFactory, null);

		displayPane = new JLayeredPane();
		displayPane.setLayer(foreground, JLayeredPane.PALETTE_LAYER);
		displayPane.setLayer(background, JLayeredPane.DEFAULT_LAYER);

		displayPane.add(foreground);
		displayPane.add(background);

		displayPane.setSize(foreground.getPreferredSize());
		displayPane.setPreferredSize(foreground.getPreferredSize());
		displayPane.setMinimumSize(foreground.getPreferredSize());

		System.out.println("Added SwingPanes:" + width + "," + height);
	}

	private Font getFont(String name) {
		Font font = null;
		String fName = "./assets/" + name;
		try {
			InputStream is = TitleScreen.class.getResourceAsStream("/resources/assets/" + name);
			font = Font.createFont(Font.TRUETYPE_FONT, is);

			System.out.println("Loaded " + fName);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println(fName + " not loaded.  Using " + FONT_NAME + " font.");
			font = new Font(BACKUP_FONT_NAME, Font.PLAIN, 24);
		}
		return font;
	}
}

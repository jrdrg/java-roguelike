package roguelike;

import javax.swing.JPanel;

public abstract class Screen {

	private JPanel panel;

	public Screen() {
		this.panel = new JPanel();
	}

	public abstract void process();
}

package roguelike.ui;

import java.util.ArrayList;

import roguelike.MessageDisplayProperties;
import squidpony.squidcolor.SColor;
import squidpony.squidgrid.gui.SwingPane;

public class MessageDisplay {
	private SwingPane pane;
	private ArrayList<MessageDisplayProperties> messages;
	private int numLines;

	public MessageDisplay(SwingPane messagePane, int numLines) {
		this.pane = messagePane;
		this.numLines = numLines;
		this.messages = new ArrayList<MessageDisplayProperties>();
	}

	public void clear() {
		messages.clear();
	}

	public void display(String message) {
		display(new MessageDisplayProperties(message));
	}

	public void display(String message, SColor color) {
		display(new MessageDisplayProperties(message, color));
	}

	public void display(MessageDisplayProperties message) {
		messages.add(message);

		if (messages.size() > numLines)
			messages.remove(0);
	}

	public void draw() {
		for (int y = 0; y < numLines; y++) {
			for (int x = 0; x < pane.gridWidth(); x++) {
				pane.clear(x, y);
			}
		}
		for (int x = 0; x < messages.size(); x++) {
			pane.put(0, x, messages.get(x).getText(), messages.get(x).getColor());
		}
		pane.refresh();
	}

}

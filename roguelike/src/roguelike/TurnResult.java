package roguelike;

import java.util.ArrayList;
import java.util.List;

import roguelike.ui.windows.Dialog;
import squidpony.squidcolor.SColor;

public class TurnResult {
	boolean running;
	boolean playerActed;
	ArrayList<MessageDisplayProperties> messages;
	ArrayList<TurnEvent> events;
	Dialog activeWindow;
	boolean needsInput;

	public TurnResult(boolean running) {
		this.running = running;
		this.messages = new ArrayList<MessageDisplayProperties>();
		this.events = new ArrayList<TurnEvent>();
	}

	public boolean playerActedThisTurn() {
		return this.playerActed;
	}

	public void playerActed() {
		this.playerActed = true;
	}

	public boolean isInputRequired() {
		return this.needsInput;
	}

	public TurnResult setNeedsInput(boolean needsInput) {
		this.needsInput = needsInput;
		return this;
	}

	public TurnResult addMessage(String message) {
		if (message != null) {
			messages.add(new MessageDisplayProperties(message));
		}
		return this;
	}

	public TurnResult addMessage(String message, SColor color) {
		if (message != null) {
			messages.add(new MessageDisplayProperties(message, color));
		}
		return this;
	}

	public TurnResult addEvent(TurnEvent event) {
		if (event != null) {
			events.add(event);
		}
		return this;
	}

	public Dialog getActiveWindow() {
		return activeWindow;
	}

	public TurnResult setWindow(Dialog window) {
		this.activeWindow = window;
		return this;
	}

	public boolean isRunning() {
		return this.running;
	}

	public List<MessageDisplayProperties> getMessages() {
		return messages;
	}

	public List<TurnEvent> getEvents() {
		return events;
	}
}

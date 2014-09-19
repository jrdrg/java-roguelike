package roguelike;

import java.util.ArrayList;
import java.util.List;

import squidpony.squidcolor.SColor;

public class TurnResult {
	boolean running;
	boolean playerActed;
	ArrayList<MessageDisplayProperties> messages;
	ArrayList<TurnEvent> events;
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

	public Dialog<?> getActiveWindow() {
		return Game.current().activeWindow;
	}

	public Cursor getCursor() {
		return Game.current().activeCursor;
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

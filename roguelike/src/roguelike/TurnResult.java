package roguelike;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class TurnResult {
	boolean running;
	boolean playerActed;
	ArrayList<MessageDisplayProperties> messages;
	ArrayList<TurnEvent> events;
	boolean needsInput;
	Point currentLook;

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

	public List<TurnEvent> getEvents() {
		return events;
	}

	public Point getCurrentLook() {
		return currentLook;
	}

	public void setCurrentLook(Point point) {
		this.currentLook = point;
	}
}

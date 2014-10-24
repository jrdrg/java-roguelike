package roguelike;

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import roguelike.screens.Screen;
import squidpony.squidutility.Pair;

public class TurnResult implements Serializable {
	private static final long serialVersionUID = 1L;

	boolean running;
	boolean playerActed;
	ArrayList<MessageDisplayProperties> messages;
	transient ArrayList<TurnEvent> events;
	transient Pair<Point, Boolean> currentLook = new Pair<Point, Boolean>(null, false);
	transient Screen subScreen;

	private TurnResult(boolean running) {
		this.running = running;
		this.messages = new ArrayList<MessageDisplayProperties>();
		this.events = new ArrayList<TurnEvent>();
	}

	private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
		in.defaultReadObject();

		this.currentLook = new Pair<Point, Boolean>(null, false);
		this.events = new ArrayList<TurnEvent>();
	}

	public static TurnResult reset(TurnResult result, boolean running) {
		if (result == null)
			result = new TurnResult(running);

		result.running = running;
		result.playerActed = false;
		result.messages.clear();
		result.events.clear();
		result.currentLook.setFirst(null);
		result.currentLook.setSecond(true);
		result.subScreen = null;

		return result;
	}

	public boolean playerActedThisTurn() {
		return this.playerActed;
	}

	public void playerActed() {
		this.playerActed = true;
	}

	public Screen getNextScreen() {
		return subScreen;
	}

	public TurnResult setNextScreen(Screen screen) {
		this.subScreen = screen;
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

	public Pair<Point, Boolean> getCurrentLook() {
		return currentLook;
	}

	public void setCurrentLook(Point point, boolean drawActor) {
		this.currentLook.setFirst(point);
		this.currentLook.setSecond(drawActor);
	}
}

package roguelike.actors;

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import roguelike.Game;
import roguelike.actions.Action;
import roguelike.actions.combat.CombatHandler;
import roguelike.actors.behaviors.Behavior;
import roguelike.actors.conditions.Condition;
import roguelike.items.Equipment;
import roguelike.items.Inventory;
import roguelike.maps.MapArea;
import roguelike.maps.Tile;
import roguelike.util.ActorUtils;
import roguelike.util.Coordinate;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

public abstract class Actor implements Serializable {
    private static final Logger LOG = LogManager.getLogger(Actor.class);
    
	private static final long serialVersionUID = 1L;

	protected transient Game game = Game.current();

	protected UUID actorId = UUID.randomUUID();

	protected char symbol;
	protected int visionRadius;
	protected boolean attackedThisRound;

	protected final Energy energy;
	protected final Statistics statistics;
	protected final CombatHandler combat;
	protected final Health health;
	protected final Equipment equipment;
	protected final Inventory inventory;
	protected ArrayList<Condition> conditions;

	protected Behavior behavior;
	protected Stack<AttackAttempt> attacked;
	protected Stack<AttackAttempt> attackedBy;
	protected transient SColor color;

	public final Coordinate position;

	protected Actor(char symbol, SColor color) {
		if (color == null)
			throw new IllegalArgumentException("color cannot be null: " + symbol);

		this.symbol = symbol;
		this.color = color;
		this.position = new Coordinate();

		this.energy = new Energy();
		this.statistics = new Statistics();
		this.combat = new CombatHandler(this);
		this.health = new Health(20);
		this.inventory = new Inventory();
		this.equipment = new Equipment();

		attacked = new Stack<AttackAttempt>();
		attackedBy = new Stack<AttackAttempt>();

		conditions = new ArrayList<Condition>();

		visionRadius = 15;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();

		out.writeInt(color.getRGB());
		LOG.debug("writing actor: {}", actorId);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();

		game = Game.current();
		color = SColorFactory.asSColor(in.readInt());

		LOG.debug("reading actor: {}", actorId);
		LOG.debug("game = {}", game.toString());

		// attacked = new Stack<AttackAttempt>();
		// attackedBy = new Stack<AttackAttempt>();
	}

	public char symbol() {
		return this.symbol;
	}

	public SColor color() {
		return this.color;
	}

	public List<Condition> conditions() {
		return conditions;
	}

	public void addCondition(Condition condition) {
		conditions.add(condition);
		condition.onConditionAdded(this);
	}

	public Behavior behavior() {
		return this.behavior;
	}

	public Energy energy() {
		return this.energy;
	}

	public Statistics statistics() {
		return this.statistics;
	}

	public Health health() {
		return this.health;
	}

	public Inventory inventory() {
		return this.inventory;
	}

	public Equipment equipment() {
		return this.equipment;
	}

	public boolean isAlive() {
		return health().getCurrent() > 0;
	}

	public int effectiveSpeed(MapArea map) {
		int tileSpeedModifier = map.getSpeedModifier(getPosition());
		int speed = statistics.speed.getTotalValue();
		speed = Math.max(1, speed + tileSpeedModifier); // always at least 1 speed even if 0 or negative

		return speed;
	}

	public boolean wasAttackedThisRound() {
		return attackedThisRound;
	}

	public CombatHandler combatHandler() {
		return this.combat;
	}

	public Coordinate getPosition() {
		return position;
	}

	public void setPosition(int x, int y) {
		position.setPosition(x, y);
	}

	public void offsetPosition(int xAmount, int yAmount) {
		position.offsetPosition(xAmount, yAmount);
	}

	public String getDescription() {
		return getName();
	}

	public int getVisionRadius() {
		return visionRadius;
	}

	public void setVisionRadius(int radius) {
		visionRadius = Math.max(1, radius);
	}

	public boolean isAdjacentTo(Actor other) {
		Point actorPos = position;
		Point otherPos = other.position;

		return Math.floor(actorPos.distance(otherPos)) <= 1;
	}

	public boolean canSee(Actor other, MapArea mapArea) {
		if (!other.isAlive())
			return false;

		return ActorUtils.canSee(this, other, mapArea);
	}

	public AttackAttempt getLastAttacked() {
		return attacked.size() > 0 ? attacked.pop() : null;
	}

	public AttackAttempt getLastAttackedBy() {
		return attackedBy.size() > 0 ? attackedBy.pop() : null;
	}

	public String getMessageName() {
		return "the " + getName();
	}

	public String getVerbSuffix() {
		return "s";
	}

	public String doAction(String action, Object... params) {
		try {
			return getMessageName() + " " + makeCorrectVerb(String.format(action, params));
		} catch (Exception e) {
			return "ERROR: " + action;
		}
	}

	public final void applyConditions() {
		ArrayList<Condition> toRemove = new ArrayList<Condition>();
		for (Condition condition : conditions) {
			if (condition.process(this))
				toRemove.add(condition);
		}
		conditions.removeAll(toRemove);
	}

	public final void finishTurn() {
		while (attacked.size() > 1)
			((Stack<AttackAttempt>) attacked).remove(0);
		while (attackedBy.size() > 1)
			((Stack<AttackAttempt>) attackedBy).remove(0);

		attackedThisRound = false;

		LOG.debug("Actor.finishTurn(): {}",getName());

		applyConditions();

		onTurnFinished();
	}

	public final void dead() {
		game = Game.current();

		onKilled();

		MapArea currentArea = game.getCurrentMapArea();
		if (Player.isPlayer(this)) {
			this.finishTurn();
			game.reset();
		}
		currentArea.removeActor(this);

		/* display bloodstain */
		currentArea.getTileAt(this.getPosition()).setBackground(SColorFactory.dimmer(SColor.DARK_RED));

		game.displayMessage("Target is dead");
	}

	public final void onAttacked(Actor attacker) {
		attackedBy.add(new AttackAttempt(attacker));
		attackedThisRound = true;

		if (behavior != null)
			behavior.onAttacked(attacker);

		onAttackedInternal(attacker);
	}

	public final void onDamaged(int amount) {
		if (health.damage(amount)) {
			dead();
		}
	}

	public abstract String getName();

	public abstract Action getNextAction();

	protected void onKilled() {
	}

	/**
	 * This is called after all other checks to move have been made, to allow the actor a final chance to prevent the
	 * move (for example, moving over certain tiles or opening doors could be disabled based on the actor type)
	 * 
	 * @param mapArea
	 * @param tile
	 * @return
	 */
	public boolean onMoveAttempting(MapArea mapArea, Tile tile) {
		// TODO: check for things like moving over certain tiles, opening doors
		return true;
	}

	protected void onTurnFinished() {
	}

	protected abstract void onAttackedInternal(Actor attacker);

	protected abstract String makeCorrectVerb(String message);
}

package roguelike.actors;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.UUID;
import java.util.stream.Collectors;

import roguelike.Game;
import roguelike.actions.Action;
import roguelike.actions.combat.CombatHandler;
import roguelike.items.Equipment;
import roguelike.items.Inventory;
import roguelike.maps.MapArea;
import roguelike.maps.Tile;
import roguelike.util.ActorUtils;
import roguelike.util.Coordinate;
import roguelike.util.Log;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

public abstract class Actor implements Serializable {
	private static final long serialVersionUID = 6622760709734146790L;

	protected UUID actorId = UUID.randomUUID();

	protected Coordinate position;
	protected char symbol;
	protected SColor color;

	protected Stack<AttackAttempt> attacked;
	protected Stack<AttackAttempt> attackedBy;
	protected boolean attackedThisRound;

	protected Energy energy;
	protected Statistics statistics;
	protected CombatHandler combat;
	protected Health health;
	protected Inventory inventory;
	protected Equipment equipment;

	protected ArrayList<Condition> conditions;

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

	public CombatHandler getCombatHandler() {
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
		return 15;
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

	public final void finishTurn() {
		while (attacked.size() > 1)
			((Stack<AttackAttempt>) attacked).remove(0);
		while (attackedBy.size() > 1)
			((Stack<AttackAttempt>) attackedBy).remove(0);

		attackedThisRound = false;

		Log.verboseDebug("Actor.finishTurn(): " + getName());

		onTurnFinished();
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

	public final void onAttacked(Actor attacker) {
		attackedBy.add(new AttackAttempt(attacker));
		attackedThisRound = true;
		onAttackedInternal(attacker);
	}

	public final void dead() {
		onKilled();

		MapArea currentArea = Game.current().getCurrentMapArea();
		if (Player.isPlayer(this)) {
			this.finishTurn();
			Game.current().reset();
		}
		currentArea.removeActor(this);

		currentArea.getTileAt(this.getPosition()).setBackground(SColorFactory.dimmer(SColor.DARK_RED));

		Game.current().displayMessage("Target is dead");
	}

	public void onKilled() {
	}

	public abstract String getName();

	public String getMessageName() {
		return "the " + getName();
	}

	public String getVerbSuffix() {
		return "s";
	}

	public abstract Action getNextAction();

	public void onSerialize(SerializationData data) {
		int[] color = new int[3];
		color[0] = this.color.getRed();
		color[1] = this.color.getGreen();
		color[2] = this.color.getBlue();

		data.setData("color", color);

		boolean isPlayer = Player.isPlayer(this);
		data.setData("isPlayer", isPlayer);

		data.setData("actorId", this.actorId);

		data.setData("symbol", this.symbol);
		data.setData("position", this.position);
		data.setData("currentEnergy", this.energy);
		data.setData("conditions", this.conditions.stream().map(c -> c.identifier.toString()).collect(Collectors.toList()));

		data.setData("inventory", this.inventory);
		data.setData("equipment", this.equipment);
		data.setData("health", this.health);
		data.setData("statistics", this.statistics);

//		data.setData("attacked", this.attacked);
//		data.setData("attackedBy", this.attackedBy);
//		data.setData("attackedThisRound", this.attackedThisRound);

		// protected CombatHandler combat;

		// TODO: need to serialize condition durations too
		// protected ArrayList<Condition> conditions;
	}

	@SuppressWarnings("unchecked")
	public void onDeserialize(SerializationData data) {
		this.actorId = (UUID) data.getData("actorId");

		int[] color = (int[]) data.getData("color");
		this.color = SColorFactory.asSColor(color[0], color[1], color[2]);
		this.symbol = 'X';// symbol;
		// actor.position = position; // TODO: load position when we load maps
		this.energy = (Energy) data.getData("currentEnergy");
		this.inventory = (Inventory) data.getData("inventory");
		this.equipment = (Equipment) data.getData("equipment");
		this.health = (Health) data.getData("health");
		this.statistics = (Statistics) data.getData("statistics");
		// this.attacked = (Stack<AttackAttempt>) data.getData("attacked");
		// this.attackedBy = (Stack<AttackAttempt>) data.getData("attackedBy");
		// this.attackedThisRound = (boolean) data.getData("attackedThisRound");
	}

	protected abstract void onAttackedInternal(Actor attacker);

	protected void onTurnFinished() {
	}
}

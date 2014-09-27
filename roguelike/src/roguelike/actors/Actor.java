package roguelike.actors;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import roguelike.actions.Action;
import roguelike.actions.combat.CombatHandler;
import roguelike.items.Equipment;
import roguelike.items.Inventory;
import roguelike.maps.MapArea;
import roguelike.maps.Tile;
import roguelike.util.Coordinate;
import roguelike.util.Log;
import squidpony.squidcolor.SColor;
import squidpony.squidgrid.los.BresenhamLOS;
import squidpony.squidgrid.los.LOSSolver;
import squidpony.squidgrid.util.BasicRadiusStrategy;

public abstract class Actor {

	protected Coordinate position;
	protected char symbol;
	protected SColor color;

	protected Stack<AttackAttempt> attacked;
	protected Stack<AttackAttempt> attackedBy;
	protected boolean attackedThisRound;

	private Energy energy;
	private Statistics statistics;
	private CombatHandler combat;
	private Health health;
	private Inventory inventory;
	private Equipment equipment;

	private ArrayList<Condition> conditions;

	protected LOSSolver losSolver;

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

		losSolver = new BresenhamLOS();
	}

	public List<Condition> conditions() {
		return conditions;
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

	public Energy getEnergy() {
		return this.energy;
	}

	public Statistics statistics() {
		return this.statistics;
	}

	public CombatHandler getCombatHandler() {
		return this.combat;
	}

	public Health getHealth() {
		return this.health;
	}

	public Inventory getInventory() {
		return this.inventory;
	}

	public Equipment getEquipment() {
		return this.equipment;
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

	public char getSymbol() {
		return this.symbol;
	}

	public SColor getColor() {
		return this.color;
	}

	public Coordinate chooseTarget() {
		// TODO: fix this
		return new Coordinate(0, 0);
	}

	public boolean isAlive() {
		return getHealth().getCurrent() > 0;
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

		int startx = position.x, starty = position.y, targetx = other.position.x, targety = other.position.y;
		float force = 1;
		float decay = 1 / this.getVisionRadius();
		boolean visible = losSolver.isReachable(mapArea.getLightValues(), startx, starty, targetx, targety, force, decay, BasicRadiusStrategy.CIRCLE);

		Log.verboseDebug(this.getName() + " canSee " + other.getName() + "=" + visible);

		return visible;
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

	protected void onTurnFinished() {
	}

	public void onKilled() {
	}

	public abstract String getName();

	public abstract Action getNextAction();

	public final void onAttacked(Actor attacker) {
		attackedBy.add(new AttackAttempt(attacker));
		attackedThisRound = true;
		onAttackedInternal(attacker);
	}

	protected abstract void onAttackedInternal(Actor attacker);
}

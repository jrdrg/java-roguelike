package roguelike.items;

import roguelike.Game;
import roguelike.actions.combat.Attack;
import roguelike.actions.combat.MeleeAttack;
import roguelike.actors.Actor;
import roguelike.util.Coordinate;
import squidpony.squidgrid.util.BasicRadiusStrategy;

public class MeleeWeapon extends Weapon {

	private static final long serialVersionUID = 682348343481995648L;

	protected MeleeWeapon() {
		super(false);
	}

	@Override
	public Attack getAttack() {

		double randomFactor = Game.current().random().nextDouble() * baseDamage;
		int totalDamage = (int) (baseDamage + randomFactor / 2);

		return new MeleeAttack(attackDescription, totalDamage, this);
	}

	@Override
	public ItemType type() {
		return ItemType.WEAPON;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public boolean canUse(Actor user, Actor target) {
		Coordinate userPos = user.getPosition();
		Coordinate targetPos = target.getPosition();

		float distance = targetPos.distance(userPos, BasicRadiusStrategy.CIRCLE);
		return distance <= getReachInTiles();
	}
}

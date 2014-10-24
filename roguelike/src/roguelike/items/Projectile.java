package roguelike.items;

import roguelike.Game;
import roguelike.actions.combat.Attack;
import roguelike.actions.combat.RangedAttack;
import roguelike.actors.Actor;
import roguelike.items.Equipment.ItemSlot;
import roguelike.util.Coordinate;
import squidpony.squidgrid.util.BasicRadiusStrategy;
import squidpony.squidutility.Pair;

public class Projectile extends Weapon {
	private static final long serialVersionUID = -5440292549086531435L;

	protected Projectile() {
		super(true);
	}

	@Override
	public boolean canUse(Actor user, Actor target) {
		Coordinate userPos = user.getPosition();
		Coordinate targetPos = target.getPosition();

		float distance = targetPos.distance(userPos, BasicRadiusStrategy.CIRCLE);
		return distance <= 1;
	}

	@Override
	public ItemType type() {
		return ItemType.PROJECTILE;
	}

	@Override
	public boolean canEquip(ItemSlot slot) {
		return super.canEquip(slot) || slot == ItemSlot.PROJECTILE;
	}

	@Override
	public Projectile asProjectile() {
		return this;
	}

	@Override
	public Attack getAttack() {

		double randomFactor = Game.current().random().nextDouble() * baseDamage;
		int totalDamage = (int) (baseDamage + randomFactor / 2);

		return new RangedAttack(attackDescription, totalDamage, this);
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void onEquipped(Actor actor) {
	}

	@Override
	public void onRemoved(Actor actor) {
	}

	@Override
	public Pair<Item, Boolean> onUsed() {
		return new Pair<Item, Boolean>(this, true);
	}
}

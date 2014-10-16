package roguelike.items;

import roguelike.actions.combat.Attack;
import roguelike.actions.combat.NoAmmunitionAttack;
import roguelike.actions.combat.WeaponCategory;
import roguelike.actors.Actor;
import roguelike.items.Equipment.ItemSlot;

public class RangedWeapon extends Weapon {

	private static final long serialVersionUID = -340930226160562519L;

	protected int maxRange;
	protected boolean requiresProjectiles;
	protected WeaponCategory projectileType;
	protected int remainingAmmunition;

	protected Actor owner;

	protected RangedWeapon() {
		super();
	}

	@Override
	public Attack getAttack() {

		if (requiresProjectiles) {
			Projectile projectile = getProjectile();
			if (projectile != null) {
				Attack attack = projectile.getAttack();
				return attack;
			}
		}

		return new NoAmmunitionAttack(0, this);
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String name() {
		return name;
	}

	public WeaponType ammunitionType() {
		return WeaponType.ARROW;
	}

	@Override
	public void onEquipped(Actor actor) {
		owner = actor;
	}

	@Override
	public void onRemoved(Actor actor) {
		owner = null;
		actor.doAction("removed the %s", this.name);
	}

	public Projectile getProjectile() {
		if (owner == null)
			return null;

		Item projectile = ItemSlot.PROJECTILE.removeItem(owner);
		if (projectile instanceof Projectile) {
			// TODO: update this to decrement the count instead of setting to null
			owner.inventory().remove(projectile);
			return (Projectile) projectile;
		}
		return null;
	}
}

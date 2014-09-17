package roguelike.items;

import roguelike.Game;
import roguelike.actions.combat.Attack;
import roguelike.actions.combat.MeleeAttack;
import roguelike.data.WeaponData;

public class MeleeWeapon extends Weapon {

	private static final long serialVersionUID = 682348343481995648L;

	protected int baseDamage;
	protected String name;
	protected String description;
	protected String attackDescription;

	public MeleeWeapon(WeaponData data) {
		super(data);
		this.name = data.name;
		this.description = data.description;
		this.baseDamage = data.baseDamage;

		this.droppable = data.droppable;
		this.symbol = data.symbol();
		this.color = data.color();

		if (data.attackDescription == null || data.attackDescription.length() == 0) {
			this.attackDescription = "%s swings " + getName() + " at %s";
		} else {
			this.attackDescription = data.attackDescription;
		}
	}

	@Override
	public Attack getAttack() {

		double randomFactor = Game.current().random().nextDouble() * baseDamage;
		int totalDamage = (int) (baseDamage + randomFactor / 2);

		return new MeleeAttack(attackDescription, totalDamage, this);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}
}

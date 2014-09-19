package roguelike.actions.combat;

import roguelike.items.Weapon;

public enum MissileRange {

	// TODO: implement ranges (point blank, near, medium, far) based on number of squares distance - show in different
	// colors when displaying line from player to target - so a line to a very far target would be displayed in 4
	// different colors while a line to a near target would be displayed in 2 and point blank would be 1
	// TODO: also implement different ranges for different ranged weapon types (thrown,
	/*
	 * Ranges for thrown weapons (all values are the max for that range, i.e. point blank is anything < 2 squares away)
	 * 
	 * point blank = 2 squares
	 * 
	 * near = 5
	 * 
	 * medium =
	 */

	POINT_BLANK,
	NEAR,
	MEDIUM,
	FAR;
	
	MissileRange(){
		
	}

	public MissileRange getRange(Weapon weapon, int distance) {
		switch (weapon.weaponType()) {

		default:
			break;

		}
		return FAR;
	}
}

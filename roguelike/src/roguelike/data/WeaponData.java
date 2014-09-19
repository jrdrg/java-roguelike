package roguelike.data;

import java.util.List;

import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;

import roguelike.util.FileUtils;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

@CsvDataType
public class WeaponData {

	public static final WeaponData DEFAULT = new WeaponData().setValues("default", 0, 0, false, "default", "%s doesn't have a weapon to hit %s");

	@CsvField(pos = 1, required = true)
	public String name;

	@CsvField(pos = 2, required = true)
	public String description;

	@CsvField(pos = 3, required = true)
	public String symbolStr;

	public char symbol() {
		return symbolStr.charAt(0);
	}

	@CsvField(pos = 4, required = true)
	public String colorStr;

	public SColor color() {
		return SColorFactory.colorForName(colorStr);
	}

	@CsvField(pos = 5, required = true)
	public String type;

	@CsvField(pos = 6, required = true)
	public int baseDamage;

	@CsvField(pos = 7, required = true)
	public int reach;

	@CsvField(pos = 8)
	public String attackDescription;

	@CsvField(pos = 9)
	public boolean droppable;

	@CsvField(pos = 10)
	public int slashTargetNumber; // TN when slashing

	@CsvField(pos = 11)
	public int slashDamageRating;

	public int[] slash() {
		return getTargetNumberAndDamage(slashTargetNumber, slashDamageRating);
	}

	@CsvField(pos = 12)
	public int thrustTargetNumber; // TN when thrusting

	@CsvField(pos = 13)
	public int thrustDamageRating;

	public int[] thrust() {
		return getTargetNumberAndDamage(thrustTargetNumber, thrustDamageRating);
	}

	@CsvField(pos = 14)
	public int bluntTargetNumber; // TN when dealing blunt damage

	@CsvField(pos = 15)
	public int bluntDamageRating;

	public int[] blunt() {
		return getTargetNumberAndDamage(bluntTargetNumber, bluntDamageRating);
	}

	/**
	 * Determines how many squares constitute each range (point blank, near, medium, far)
	 */
	@CsvField(pos = 16)
	public String rangedType;

	@CsvField(pos = 17)
	public int pointBlankTN;

	@CsvField(pos = 18)
	public int nearTN;

	@CsvField(pos = 19)
	public int mediumTN;

	@CsvField(pos = 20)
	public int farTN;

	@CsvField(pos = 21)
	public int rangedDamageRating;

	@CsvField(pos = 22)
	public boolean requiresAmmunition;

	@CsvField(pos = 23)
	public String ammunitionType;

	@CsvField(pos = 24)
	public int quality;

	@CsvField(pos = 25)
	public int defenseTargetNumber;

	/**
	 * The condition that can be caused by this weapon with net successes equal or greater than netSuccesses
	 */
	@CsvField(pos = 26)
	public String condition;

	/**
	 * The net successes necessary to cause the condition
	 */
	@CsvField(pos = 27)
	public int attackSuccesses;

	/**
	 * The attribute to check against to see
	 */
	@CsvField(pos = 28)
	public String attribute;

	/**
	 * The number of successes needed against the attribute to avoid the condition
	 */
	@CsvField(pos = 29)
	public int attrSuccesses;

	/**
	 * Used when deserializing from CSV file
	 */
	private WeaponData() {
	}

	public static List<WeaponData> create() {
		return FileUtils.recordsFromCsv("/resources/config/weapons.csv", WeaponData.class);
	}

	private WeaponData setValues(String name, int baseDamage, int reach, boolean droppable, String type, String attackDescription) {
		this.name = name;
		this.baseDamage = baseDamage;
		this.reach = reach;
		this.droppable = droppable;
		this.type = type;
		this.attackDescription = attackDescription;
		return this;
	}

	private int[] getTargetNumberAndDamage(int targetNumber, int damageRating) {
		int[] data = new int[2];
		data[0] = targetNumber;
		data[1] = damageRating;
		return data;
	}
}

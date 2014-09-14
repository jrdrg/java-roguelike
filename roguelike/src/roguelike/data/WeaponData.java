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
	public int slashDamageRating; // slashing modifier to dice pool

	@CsvField(pos = 12)
	public int thrustTargetNumber; // TN when thrusting

	@CsvField(pos = 13)
	public int thrustDamageRating; // thrusting modifier to dice pool

	@CsvField(pos = 14)
	public int bluntTargetNumber; // TN when dealing blunt damage

	@CsvField(pos = 15)
	public int bluntDamageRating; // blunt modifier to dice pool

	@CsvField(pos = 16)
	public int rangedTargetNumber;

	@CsvField(pos = 17)
	public int rangedDamageRating;

	@CsvField(pos = 18)
	public boolean requiresAmmunition;

	@CsvField(pos = 19)
	public String ammunitionType;

	@CsvField(pos = 20)
	public int quality;

	@CsvField(pos = 21)
	public int defenseTargetNumber;

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
}

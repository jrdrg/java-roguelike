package roguelike.data;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.jsefa.Deserializer;
import org.jsefa.common.lowlevel.filter.HeaderAndFooterFilter;
import org.jsefa.csv.CsvIOFactory;
import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;
import org.jsefa.csv.config.CsvConfiguration;

import roguelike.MainScreen;
import roguelike.util.FileUtils;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

@CsvDataType
public class WeaponData {

	public static final WeaponData DEFAULT = new WeaponData("default", 0, 0, false, "default", "%s doesn't have a weapon to hit %s");

	@CsvField(pos = 1)
	public String name;

	@CsvField(pos = 2)
	public String description;

	@CsvField(pos = 3)
	public String symbolStr;

	public char symbol() {
		return symbolStr.charAt(0);
	}

	@CsvField(pos = 4)
	public String colorStr;

	public SColor color() {
		return SColorFactory.colorForName(colorStr);
	}

	@CsvField(pos = 5)
	public String type;

	@CsvField(pos = 6)
	public int baseDamage;

	@CsvField(pos = 7)
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

	/**
	 * Used when deserializing from CSV file
	 */
	private WeaponData() {
	}

	private WeaponData(String name, int baseDamage, int reach, boolean droppable, String type, String attackDescription) {
		this.name = name;
		this.baseDamage = baseDamage;
		this.reach = reach;
		this.droppable = droppable;
		this.type = type;
		this.attackDescription = attackDescription;
	}

	public static List<WeaponData> create() {
		String csv;
		try {
			csv = FileUtils.readFile(MainScreen.class.getResourceAsStream("/resources/config/weapons.csv"));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		ArrayList<WeaponData> weapons = new ArrayList<WeaponData>();
		CsvConfiguration csvConfiguration = new CsvConfiguration();
		csvConfiguration.setFieldDelimiter(',');
		csvConfiguration.setLineFilter(new HeaderAndFooterFilter(1, false, false));

		Deserializer deserializer = CsvIOFactory.createFactory(csvConfiguration, WeaponData.class).createDeserializer();

		deserializer.open(new StringReader(csv));
		while (deserializer.hasNext()) {
			WeaponData row = deserializer.next();

			weapons.add(row);
			System.out.println("Read weapon " + row.name + " dmg=" + row.baseDamage);
		}
		deserializer.close(true);

		return weapons;
	}
}

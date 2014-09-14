package roguelike.data;

import java.util.List;

import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;

import roguelike.util.FileUtils;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

@CsvDataType
public class EnemyData {

	@CsvField(pos = 1, required = true)
	public String name;

	@CsvField(pos = 2, required = true)
	public String symbolStr;

	public char symbol() {
		return symbolStr.charAt(0);
	}

	@CsvField(pos = 3, required = true)
	public String colorStr;

	public SColor color() {
		return SColorFactory.colorForName(colorStr);
	}

	@CsvField(pos = 4, required = true)
	public int speed;

	@CsvField(pos = 5, required = true)
	public String behavior;

	@CsvField(pos = 6, required = true)
	public String weapon;

	@CsvField(pos = 7, required = true)
	public String type;

	@CsvField(pos = 8, required = true)
	public int health;

	@CsvField(pos = 9)
	public String dropTypes;

	@CsvField(pos = 10, required = true)
	public int difficulty;

	@CsvField(pos = 11, required = true)
	public int toughness;

	@CsvField(pos = 12, required = true)
	public int conditioning;

	@CsvField(pos = 13, required = true)
	public int perception;

	@CsvField(pos = 14, required = true)
	public int quickness;

	@CsvField(pos = 15, required = true)
	public int willpower;

	@CsvField(pos = 16, required = true)
	public int presence;

	@CsvField(pos = 17)
	public int reflexBonus;

	@CsvField(pos = 18)
	public int aimingBonus;

	private EnemyData() {
	}

	public static List<EnemyData> create() {
		return FileUtils.recordsFromCsv("/resources/config/enemies.csv", EnemyData.class);
	}
}

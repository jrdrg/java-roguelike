package roguelike.data;

import java.util.List;

import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;

import roguelike.util.FileUtils;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

@CsvDataType
public class MonsterData {

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

	private MonsterData() {
	}

	public static List<MonsterData> create() {
		return FileUtils.recordsFromCsv("/resources/config/monsters.csv", MonsterData.class);
	}
}

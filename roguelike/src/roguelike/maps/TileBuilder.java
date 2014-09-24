package roguelike.maps;

import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;
import squidpony.squidutility.ProbabilityTable;

public class TileBuilder {

	// wall: ▦ #
	// tree: ♣ & T
	// water: ⏞≈≓≘∴∠∭▄⏕

	private ProbabilityTable<Character> trees;
	private ProbabilityTable<Character> ground;
	private ProbabilityTable<Character> water;

	public TileBuilder() {
		trees = new ProbabilityTable<Character>();
		ground = new ProbabilityTable<Character>();
		water = new ProbabilityTable<Character>();

		trees.add('&', 15);
		trees.add('♣', 7);

		// water.add('~', 20);
		water.add('≈', 5);

		ground.add('.', 30);
		// ground.add(';', 2);
		// ground.add(',', 6);
		ground.add('`', 1);
	}

	public Tile buildTile(char tile) {
		Tile t = new Tile();

		switch (tile) {
		case '#': // wall
			t.setValues(' ', false, SColor.LIGHT_GRAY, true).setLighting(1f).setBackground(SColor.DARK_GRAY);
			break;

		case '+': // door
			return new Door().setValues(tile, false, SColor.BIRCH_BROWN, true);

		case 'T':
			t.setValues(trees.random(), true, SColor.KELLY_GREEN).setLighting(0.5f).setSpeedModifier(-15);// .setBackground(SColor.GREEN);
			break;

		case '=': // floor of building
			t.setValues(',', true, SColor.EARTHEN_YELLOW);// .setBackground(SColor.DARK_BROWN);
			break;

		case '~': // water
			t.setValues(water.random(), false, SColorFactory.asSColor(50, 150, 255)).setLighting(0f).setBackground(SColor.DARK_BLUE);
			break;

		case 'M': // mountain
			t.setValues('M', false, SColor.WHITE_MOUSE, true);
			break;

		case '*': // hills
			t.setValues('.', true, SColor.BENI_DYE).setLighting(0);
			break;

		case '-': // dungeon floor
			t.setValues('.', true, SColor.BOILED_RED_BEAN_BROWN);
			break;

		case '.': // ground
		default:
			t.setValues(ground.random(), true, SColorFactory.asSColor(50, 200, 100));// .setBackground(SColor.DARK_GREEN);
			break;
		}

		return t;
	}
}

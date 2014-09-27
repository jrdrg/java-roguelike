package roguelike.maps;

import roguelike.util.CharacterGlyph;
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
	private ProbabilityTable<SColor> waterColor;

	public TileBuilder() {
		trees = new ProbabilityTable<Character>();
		ground = new ProbabilityTable<Character>();
		water = new ProbabilityTable<Character>();

		waterColor = new ProbabilityTable<SColor>();
		waterColor.add(SColor.DARK_BLUE, 10);
		waterColor.add(SColor.BLUE, 3);
		waterColor.add(SColorFactory.desaturate(SColor.DARK_BLUE, 0.2), 6);

		trees.add('&', 15);
		// trees.add('♣', 7);

		water.add(CharacterGlyph.WATER.symbol(), 20);
		// water.add('≈', 5);

		ground.add(CharacterGlyph.GROUND1.symbol(), 30);
		ground.add(CharacterGlyph.GROUND2.symbol(), 1);
		// ground.add(';', 2);
		// ground.add(',', 6);
	}

	public Tile buildTile(char tile) {
		Tile t = new Tile();

		switch (tile) {
		case '#': // wall
			t.setValues(CharacterGlyph.WALL.symbol(), false, SColor.DARK_GRAY, true).setLighting(1f);// .setBackground(SColor.DARK_GRAY);
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
			// t.setValues(water.random(), false, SColorFactory.asSColor(50, 150,
			// 255)).setLighting(0f).setBackground(waterColor.random());
			t.setValues(water.random(), false, SColorFactory.blend(waterColor.random(), SColorFactory.asSColor(50, 150, 255), .5))
					.setLighting(0f)
					.setBackground(waterColor.random());
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

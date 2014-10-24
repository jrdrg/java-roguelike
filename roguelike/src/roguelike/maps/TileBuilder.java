package roguelike.maps;

import roguelike.util.Log;
import roguelike.util.Symbol;
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
	private ProbabilityTable<SColor> groundColor;

	public TileBuilder() {
		trees = new ProbabilityTable<Character>();
		ground = new ProbabilityTable<Character>();
		water = new ProbabilityTable<Character>();

		waterColor = new ProbabilityTable<SColor>();
		waterColor.add(SColor.DARK_BLUE, 10);
		waterColor.add(SColor.BLUE, 3);
		waterColor.add(SColorFactory.desaturate(SColor.DARK_BLUE, 0.2), 6);

		groundColor = new ProbabilityTable<SColor>();
		groundColor.add(SColorFactory.dimmer(SColor.DARK_GRAY), 10);
		groundColor.add(SColorFactory.desaturate(SColor.BLACK, 0.4), 4);
		groundColor.add(SColorFactory.desaturate(SColor.BLACK_CHESTNUT_OAK, 0.8), 2);

		trees.add(Symbol.TREE2.symbol(), 15);
		trees.add(Symbol.TREE1.symbol(), 7);

		water.add(Symbol.WATER.symbol(), 20);
		// water.add('≈', 5);

		ground.add(Symbol.GROUND1.symbol(), 30);
		ground.add(Symbol.GROUND2.symbol(), 1);
		// ground.add(';', 2);
		// ground.add(',', 6);
	}

	public Tile buildTile(Symbol character) {
		Tile t = new Tile();

		switch (character) {
		case WALL:
			t.setValues(character.symbol(), false, SColor.PINE_GREEN, true)
					.setLighting(1f);
			// .setBackground(SColor.DARK_BLUE_LAPIS_LAZULI);
			break;

		case DOOR:
			return new Door().setValues(character.symbol(), false, SColor.BIRCH_BROWN, true);

		case TREE:
			t.setValues(trees.random(), true, SColor.KELLY_GREEN)
					.setLighting(0.5f)
					.setSpeedModifier(-10);// .setBackground(SColor.GREEN);
			break;

		case BUILDING_FLOOR:
			t.setValues(character.symbol(), true, SColor.EARTHEN_YELLOW);// .setBackground(SColor.DARK_BROWN);
			break;

		case WATER:
			t.setValues(water.random(), false, SColorFactory.blend(waterColor.random(), SColorFactory.asSColor(50, 150, 255), .5))
					.setLighting(0f)
					.setBackground(waterColor.random());
			break;

		case SHALLOW_WATER:
			t.setValues(water.random(), true, SColorFactory.blend(waterColor.random(), SColorFactory.asSColor(30, 100, 255), .5))
					.setLighting(0f)
					.setSpeedModifier(-15)
					.setBackground(SColorFactory.desaturate(waterColor.random(), 0.1));
			break;

		case MOUNTAIN:
			t.setValues(character.symbol(), false, SColor.WHITE_MOUSE, true);
			break;

		case HILLS:
			t.setValues(character.symbol(), true, SColor.BENI_DYE).setLighting(0);
			break;

		case DUNGEON_FLOOR:
			t.setValues(character.symbol(), true, SColor.GRAY);// .setBackground(groundColor.random());
			break;

		case GROUND:
			t.setValues(ground.random(), true, SColorFactory.asSColor(50, 200, 100));// .setBackground(SColor.DARK_GREEN);
			break;

		case STAIRS_DOWN:
			return new Stairs(new DungeonMapBuilder(), true).setValues(character.symbol(), true, SColor.WHITE);

		case STAIRS_UP:
			return new Stairs(new DungeonMapBuilder(), false).setValues(character.symbol(), true, SColor.WHITE);

		case BOX_BOTTOM_LEFT_SINGLE:
			// TODO: change the character and make this a torch or something
			t.setValues(character.symbol(), false, SColor.ORANGE, true);
			break;

		default:
			t = buildTile(character.symbol());
		}
		return t;
	}

	Tile buildTile(char tile) {
		Tile t = new Tile();

		switch (tile) {
		case '#': // wall
			t.setValues(Symbol.WALL.symbol(), false, SColor.DARK_GRAY, true).setLighting(1f);// .setBackground(SColor.DARK_GRAY);
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
			Log.warning("Could not find a tile for character " + tile + ": " + (int) tile);
			t.setValues(ground.random(), true, SColorFactory.asSColor(50, 200, 100));// .setBackground(SColor.DARK_GREEN);
			break;
		}

		return t;
	}
}

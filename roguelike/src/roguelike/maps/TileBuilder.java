package roguelike.maps;

import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

public class TileBuilder {

	public Tile buildTile(char tile) {
		Tile t = new Tile();

		switch (tile) {
		case '#': // wall
			// t.setValues('▦', false, SColor.LIGHT_GRAY,
			// true).setLighting(1f);//.setBackground(SColor.DARK_GRAY);

			// t.setValues('#', false, SColor.LIGHT_GRAY,
			// true).setLighting(1f);// .setBackground(SColor.DARK_GRAY);
			t.setValues(' ', false, SColor.LIGHT_GRAY, true).setLighting(1f).setBackground(SColor.DARK_GRAY);
			break;

		case '+': // door
			return new Door().setValues(tile, false, SColor.BIRCH_BROWN, true);

		case 'T':
			t.setValues('♣', true, SColor.KELLY_GREEN).setLighting(0.5f);// .setBackground(SColor.GREEN);
			break;

		case '=': // floor of building
			t.setValues(',', true, SColor.EARTHEN_YELLOW);// .setBackground(SColor.DARK_BROWN);
			break;

		case '~': // water
			// t.setValues('~', false, SColor.AZUL).setLighting(0f);//
			// .setBackground(SColor.BLUE);
			// ⏞≈≓≘∴∠∭▄⏕
			t.setValues('~', false, SColorFactory.asSColor(50, 150, 255)).setLighting(0f).setBackground(SColor.DARK_BLUE);
			// t.setValues(' ', false, SColorFactory.asSColor(50, 170,
			// 255)).setLighting(0f).setBackground(SColor.AZUL);
			break;

		case '.': // ground
		default:
			// t.setValues('.', true, SColor.GRAPE_MOUSE);
			// t.setValues('.', true,
			// SColor.FERN_GREEN).setBackground(SColor.EMERALD);
			t.setValues('.', true, SColorFactory.asSColor(50, 200, 100));// .setBackground(SColor.DARK_GREEN);
			break;
		}

		return t;
	}
}

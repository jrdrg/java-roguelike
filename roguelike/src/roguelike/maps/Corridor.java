package roguelike.maps;

import java.awt.Rectangle;

public class Corridor extends Room {

	public Corridor(Rectangle area) {
		super(area);
	}

	@Override
	public void fillRoom(Tile[][] map, TileBuilder tb, char tile) {
		// TODO Auto-generated method stub
		super.fillRoom(map, tb, tile);
	}
}

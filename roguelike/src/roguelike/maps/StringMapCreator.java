package roguelike.maps;

import roguelike.util.CurrentItemTracker;

public class StringMapCreator {

	private String mapTiles;
	private TileBuilder tileBuilder;
	private CurrentItemTracker<Character> tiles;

	public StringMapCreator(String mapTiles) {
		this.mapTiles = mapTiles;
		this.tileBuilder = new TileBuilder();
		this.tiles = new CurrentItemTracker<Character>();
		for (int x = 0; x < this.mapTiles.length(); x++)
			tiles.add(this.mapTiles.charAt(x));
	}

	public Tile nextTile() {
		tiles.advance();
		return tileBuilder.buildTile(tiles.getCurrent());
	}
}

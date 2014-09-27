package roguelike.ui.asciipanel;

public interface TileTransformer {
	public void transformTile(int x, int y, AsciiCharacterData data);
}
package roguelike.maps;

public class Dungeon extends MapArea {

	private static final long serialVersionUID = 627520295057387283L;

	private int difficulty;
	private boolean hasSpecialFloors;

	public Dungeon(int width, int height, MapBuilderBase mapBuilder, int difficulty) {
		super(width, height, mapBuilder);
		this.difficulty = difficulty;
	}

}

package roguelike.maps;

public class Dungeon extends MapArea {

	private static final long serialVersionUID = 627520295057387283L;

	private boolean hasSpecialFloors;
	private String name;
	private int currentFloor;
	private int totalFloors;

	public Dungeon(int width, int height, MapBuilderBase mapBuilder, int difficulty, int totalFloors) {
		super(width, height, mapBuilder);
		this.difficulty = difficulty;

		this.currentFloor = 1;
		this.totalFloors = totalFloors;
	}

}

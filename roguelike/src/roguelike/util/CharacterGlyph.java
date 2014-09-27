package roguelike.util;

public enum CharacterGlyph {
	TREE1(5), // ♣
	TREE2('&'),
	WATER(247), // ≈
	// WATER('='),
	GROUND1('.'),
	GROUND2('`'),

	WALL(176), // ░

	BOX_LEFT_SINGLE(179), // │
	BOX_RIGHT_SINGLE(179),
	BOX_TOP_SINGLE(196), // ─
	BOX_DOWN_SINGLE(196),
	BOX_TOP_LEFT_SINGLE(218), // ┌
	BOX_TOP_RIGHT_SINGLE(191), // ┐
	BOX_BOTTOM_LEFT_SINGLE(192), // └
	BOX_BOTTOM_RIGHT_SINGLE(217), // ┘

	BOX_LEFT_DOUBLE(186), // ║
	BOX_TOP_DOUBLE(205), // ═

	;
	private char symbol;

	public char symbol() {
		return this.symbol;
	}

	CharacterGlyph(char symbol) {
		this.symbol = symbol;
	}

	CharacterGlyph(int charIndex) {
		this.symbol = (char) charIndex;
	}
}

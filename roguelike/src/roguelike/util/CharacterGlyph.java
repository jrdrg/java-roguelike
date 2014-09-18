package roguelike.util;

public enum CharacterGlyph {
	TREE1('♣'),
	TREE2('&'),
	WATER('≈'),
	GROUND1('.'),
	GROUND2('`'),

	BOX_LEFT('|'),
	BOX_RIGHT('|'),
	BOX_UP('-'),
	BOX_DOWN('-'),
	BOX_TOP_LEFT('+'),
	BOX_TOP_RIGHT('+'),
	BOX_BOTTOM_LEFT('+'),
	BOX_BOTTOM_RIGHT('+')

	;
	private char symbol;

	public char symbol() {
		return this.symbol;
	}

	CharacterGlyph(char symbol) {
		this.symbol = symbol;
	}
}

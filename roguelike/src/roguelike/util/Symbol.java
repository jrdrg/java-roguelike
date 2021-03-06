package roguelike.util;

public enum Symbol {
	TREE('&'),
	DOOR('+'),
	BUILDING_FLOOR(','),
	MOUNTAIN('^'),
	DUNGEON_FLOOR('.'),
	HILLS('.'),
	GROUND('.'),
	STAIRS_UP('<'),
	STAIRS_DOWN('>'),

	TREE1(5), // ♣
	TREE2('&'),
	WATER(247), // ≈
	SHALLOW_WATER(247), // ≈
	// WATER('='),
	GROUND1('.'),
	GROUND2('`'),

	WALL(176), // ░
	// WALL('#'),

	TORCH1(';'),
	TORCH2(':'),
	TORCH3('|'),
	TORCH4('`'),

	BOX_LEFT_SINGLE(179), // │
	BOX_RIGHT_SINGLE(179),
	BOX_TOP_SINGLE(196), // ─
	BOX_DOWN_SINGLE(196),
	BOX_TOP_LEFT_SINGLE(218), // ┌
	BOX_TOP_RIGHT_SINGLE(191), // ┐
	BOX_BOTTOM_LEFT_SINGLE(192), // └
	BOX_BOTTOM_RIGHT_SINGLE(217), // ┘

	BOX_LEFT_DOUBLE(186), // ║
	BOX_RIGHT_DOUBLE(186),
	BOX_TOP_DOUBLE(205), // ═
	BOX_DOWN_DOUBLE(205),
	BOX_TOP_LEFT_DOUBLE(201), // ╔
	BOX_TOP_RIGHT_DOUBLE(187), // ╗
	BOX_BOTTOM_LEFT_DOUBLE(200), // ╚
	BOX_BOTTOM_RIGHT_DOUBLE(188), // ╝

	;
	private char symbol;

	public char symbol() {
		return this.symbol;
	}

	Symbol(char symbol) {
		this.symbol = symbol;
	}

	Symbol(int charIndex) {
		this.symbol = (char) charIndex;
	}
}

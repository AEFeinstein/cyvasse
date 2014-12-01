package com.gelakinetic.cyvasse.gameHelpers;

public class Coord {

	public int	x, y;

	public Coord(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Coord() {
		x = y = 0;
	}

	public Coord(Coord position, Coord coord) {
		this.x = position.x + coord.x;
		this.y = position.y + coord.y;
	}

	@Override
	public boolean equals(Object o) {
		try {
			Coord c = (Coord) o;
			return (c.x == this.x && c.y == this.y);
		}
		catch (ClassCastException e) {
			return false;
		}
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	public int distBetween(Coord c) {
		return Math.abs(x - c.x) + Math.abs(y - c.y);
	}
}
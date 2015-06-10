package com.netsensia.twentyfortyeight;

public class SolverMove {
	public int direction;
	
	public SolverMove(int direction) {
		this.direction = direction;
	}
	
	public String toString() {
		String dirStr = "";
		switch (this.direction) {
		case Board.UP: dirStr = "Up"; break;
		case Board.DOWN: dirStr = "Down"; break;
		case Board.LEFT: dirStr = "Left"; break;
		case Board.RIGHT: dirStr = "Right"; break;
		}
		
		return "{" + dirStr + "}";
	}
}

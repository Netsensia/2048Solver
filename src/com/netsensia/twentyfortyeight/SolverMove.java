package com.netsensia.twentyfortyeight;

public class SolverMove {
	private int direction;
	
	private double score;

	public SolverMove(int direction) {
		this.setDirection(direction);
	}
	
	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}
	
	public String toString() {
		String dirStr = "";
		switch (this.getDirection()) {
		case Board.UP: dirStr = "Up"; break;
		case Board.DOWN: dirStr = "Down"; break;
		case Board.LEFT: dirStr = "Left"; break;
		case Board.RIGHT: dirStr = "Right"; break;
		}
		
		return "{" + dirStr + "," + this.getScore() + "}";
	}
}

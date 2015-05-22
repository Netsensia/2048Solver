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
}

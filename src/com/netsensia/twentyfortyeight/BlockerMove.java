package com.netsensia.twentyfortyeight;

public class BlockerMove {
	
	private int x;
	
	private int y;
	
	private int piece;
	
	private double score;
	
	public BlockerMove(int x, int y, int piece) {
		this.setX(x);
		this.setY(y);
		this.setPiece(piece);
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public int getPiece() {
		return piece;
	}
	
	public void setPiece(int piece) {
		this.piece = piece;
	}
	
	public double getScore() {
		return score;
	}
	
	public void setScore(double score) {
		this.score = score;
	}
	
	public String toString() {
		return "{" + getX() + "," + getY() + "=" + getPiece() + ":" + getScore() + "}";
	}
}

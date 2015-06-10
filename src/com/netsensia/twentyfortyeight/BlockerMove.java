package com.netsensia.twentyfortyeight;

public class BlockerMove {
	
	public int x;
	
	public int y;
	
	public int piece;
	
	public double score;
	
	public BlockerMove(int x, int y, int piece) {
		this.x = x;
		this.y = y;
		this.piece = piece;
	}
	
	public String toString() {
		return "{" + x + "," + y + "=" + piece + ":" + score + "}";
	}
}

package com.netsensia.twentyfortyeight;

public class TwentyFortyEight {
	
	public static void main(String args[]) {
		Board board = new Board();
		
		board.place(0,0,2);
		board.place(0,2,2);
		
		System.out.println("Start");
		System.out.println(board);
		
		board.slide(Board.DOWN);
		
		
	}
}

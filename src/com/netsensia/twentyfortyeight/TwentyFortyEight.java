package com.netsensia.twentyfortyeight;

public class TwentyFortyEight {
	
	public static Board getTestBoard() {
		Board board = new Board();
		
		board.place(0,0,2);
		board.place(0,2,2);
		
		board.place(1,0,4);
		board.place(1,1,2);
		board.place(1,2,2);
		board.place(1,3,8);
		
		board.place(2,0,4);
		board.place(2,1,8);
		board.place(2,2,2);
		board.place(2,3,8);
		
		board.place(3,0,8);
		board.place(3,1,8);
		board.place(3,2,8);
		board.place(3,3,4);
		
		return board;
	}
	
	public static void main(String args[]) {
		
		Board board = getTestBoard();
		System.out.println("Start");
		System.out.println(board);
		
		board.slide(Board.DOWN);
		System.out.println("Slid down");
		System.out.println(board);
		
		board = getTestBoard();
		board.slide(Board.LEFT);
		System.out.println("Slid left");
		System.out.println(board);
		
		board = getTestBoard();
		board.slide(Board.RIGHT);
		System.out.println("Slid right");
		System.out.println(board);
		
		board = getTestBoard();
		board.slide(Board.UP);
		System.out.println("Slid up");
		System.out.println(board);
		
	}
}

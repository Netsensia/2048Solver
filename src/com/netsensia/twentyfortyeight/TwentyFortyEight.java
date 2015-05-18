package com.netsensia.twentyfortyeight;

public class TwentyFortyEight {
	
	public static Board getTestBoard() {
		Board board = new Board();
		
		int[] testBoard = {
				2,4,4,8,
				0,2,8,8,
				2,2,2,8,
				0,8,8,4,
		};
		
		board.setBoard(testBoard);
		
		return board;
	}
	
	public static void test() {
		
		Board board = getTestBoard();
		System.out.println("Start");
		System.out.println(board);
		
		board.slide(Board.DOWN);
		System.out.println("Slid down");
		System.out.println(board);
		
		board.slide(Board.LEFT);
		System.out.println("Slid left");
		System.out.println(board);
		
		board.slide(Board.RIGHT);
		System.out.println("Slid right");
		System.out.println(board);
		
		board.slide(Board.UP);
		System.out.println("Slid up");
		System.out.println(board);
		
		System.out.println("Valid moves:");
		if (board.isValidMove(Board.UP)) System.out.println("Up");
		if (board.isValidMove(Board.DOWN)) System.out.println("Down");
		if (board.isValidMove(Board.LEFT)) System.out.println("Left");
		if (board.isValidMove(Board.RIGHT)) System.out.println("Right");
		
		System.exit(0);
	}
	
	public static void main(String args[]) {
		
		test();
		
		Board board = new Board();
		board.setRandomStartPosition();
		
		while (!board.isGameOver()) {
			
		}
		
	}
}

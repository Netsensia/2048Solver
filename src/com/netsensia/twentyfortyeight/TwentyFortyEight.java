package com.netsensia.twentyfortyeight;

import java.util.Random;

public class TwentyFortyEight {

	public static void main(String args[]) {
		
		Board board = new Board();
		board.setRandomStartPosition();
		
		Random r = new Random();
		
		System.out.println("Starting position:");
		System.out.println(board);
		int moveCount = 0;
		
		while (!board.isGameOver()) {
			boolean legalMove = false;
			do {
				int direction = r.nextInt() % 4;
				String dirText = "";
				
				switch (direction) {
				case 0: legalMove = board.isValidMove(Board.UP); dirText = "Up"; break;
				case 1: legalMove = board.isValidMove(Board.DOWN); dirText = "Down"; break;
				case 2: legalMove = board.isValidMove(Board.LEFT); dirText = "Left"; break;
				case 3: legalMove = board.isValidMove(Board.RIGHT);dirText = "Right";  break;
				}
				
				if (legalMove) {
					System.out.println("Move " + ++moveCount);
					System.out.println("Solver's move: " + dirText);
					board.makeMove(direction, true);
					System.out.println(board);
					System.out.println("Blocker's move:");
					board.placeRandomPiece();
					System.out.println(board);
				}
				
			} while (!legalMove);
		}
		
	}
	
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
		
		board.makeMove(Board.DOWN, true);
		System.out.println("Slid down");
		System.out.println(board);
		
		board.makeMove(Board.LEFT, true);
		System.out.println("Slid left");
		System.out.println(board);
		
		board.makeMove(Board.RIGHT, true);
		System.out.println("Slid right");
		System.out.println(board);
		
		board.makeMove(Board.UP, true);
		System.out.println("Slid up");
		System.out.println(board);
		
		System.out.println("Valid moves:");
		if (board.isValidMove(Board.UP)) System.out.println("Up");
		if (board.isValidMove(Board.DOWN)) System.out.println("Down");
		if (board.isValidMove(Board.LEFT)) System.out.println("Left");
		if (board.isValidMove(Board.RIGHT)) System.out.println("Right");
		
		System.exit(0);
	}
	
}

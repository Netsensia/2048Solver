package com.netsensia.twentyfortyeight;

import java.util.Random;
import java.util.Arrays;

public class Board {
	
	public static final int ROWS = 4;
	public static final int COLS = 4;
	
	public static final int TOTAL_SQUARES = ROWS * COLS;
	
	public static final int UP = 0;
	public static final int DOWN = 1;
	public static final int RIGHT = 2;
	public static final int LEFT = 3;
	
	public static final int MAX_TILE_POWER = 16; // 64k
	
	public static final int SOLVER = 0;
	public static final int BLOCKER = 1;
	
	private int score = 0;
	private int movesMade = 0;
	
	private static int lookupRotateClockwise[] = new int[ROWS*COLS];
	private static int lookupRotateAntiClockwise[] = new int[ROWS*COLS];
	private static int lookupRotate180[] = new int[ROWS*COLS];
	private static boolean staticItemsInitialised = false;

	public int getMovesMade() {
		return movesMade;
	}

	public void setMovesMade(int movesMade) {
		this.movesMade = movesMade;
	}

	private int board[] = new int[ROWS*COLS];
	
	private static Random r;
	
	public Board() {
		if (!staticItemsInitialised) {
			initStaticItems();
		}
	}
	
	public Board(int[] board, int score) {
		System.arraycopy( board, 0, this.board, 0, board.length );
		this.score = score;
	}
	
	private void initStaticItems() {
		for (int x=0; x<ROWS; x++) {
			for (int y=0; y<COLS; y++) {
				// New Y is current x, new x is the inverse of Y: COLS-y-1
				lookupRotateClockwise[y*COLS+x] = x*COLS+(COLS-y-1);
				lookupRotateAntiClockwise[y*COLS+x] = (ROWS-x-1)*COLS+y;
				
				// Now for the 180
				int newY = x;
				int newX = COLS-y-1;
				
				// And rotate it again
				int newNewY = newX;
				int newNewX = COLS-newY-1;
				
				lookupRotate180[y*COLS+x] = newNewY * COLS + newNewX;
				
			}
		}
		
		 r = new Random();
		 
		staticItemsInitialised = true;
	}
	
	public void setRandomStartPosition() {
		int numSquares = ROWS * COLS;
		
		int square1 = r.nextInt(numSquares);
		int square2;
		do {
			square2 = r.nextInt(numSquares);
		} while (square1 == square2);
		
		board[square1] = r.nextInt(2) == 0 ? 2 : 4;
		board[square2] = r.nextInt(2) == 0 ? 2 : 4;
	}
	
	public int getHighestTileValue() {
		int highest = 0;
		for (int i=0; i<ROWS*COLS; i++) {
			if (board[i] > highest) {
				highest = board[i];
			}
		}
		return highest;
	}
	
	public boolean placeRandomPiece() {
		
		if (isFull()) {
			return false;
		}
		
		int square;
		do {
			square = r.nextInt(ROWS*COLS);
			if (board[square] == 0) {
				board[square] = r.nextInt(2) == 0 ? 2 : 4;
				break;
			}
		} while (true);
		
		return true;
	}
	
	public boolean isFull()	{
		
		for (int i=0; i<ROWS*COLS; i++) {
			if (board[i] == 0) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean isGameOver() {
		
		boolean isGameOver = true;
		
		Board newBoard;
		newBoard = new Board(this.board, this.score);
	
		for (int i=Board.UP; i<=Board.LEFT; i++) {
			newBoard.makeMove(i, false);
			if (!Arrays.equals(newBoard.getBoard(), board)) {
				isGameOver = false;
				break;
			}
		}

		return isGameOver;
	}
	
	public boolean isValidMove(int direction) {
		
		Board newBoard = new Board(this.board, this.score);

		newBoard.makeMove(direction, false);
		
		if (Arrays.equals(board, newBoard.getBoard())) {
			return false;
		}
		
		return true;
	}
	
	public void place(int x, int y, int number) {
		this.board[y*ROWS+x] = number;
	}
	
	public int getSquare(int x, int y) {
		return board[y*COLS+x];
	}
	
	public void rotateClockwise() {
		int[] newBoard = new int[ROWS*COLS];
		
		for (int i=0; i<newBoard.length; i++) {
			newBoard[lookupRotateClockwise[i]] = board[i];
		}
		
		this.board = newBoard;
	}
	
	public void rotateAntiClockwise() {
		int[] newBoard = new int[ROWS*COLS];
		
		for (int i=0; i<newBoard.length; i++) {
			newBoard[lookupRotateAntiClockwise[i]] = board[i];
		}
		
		this.board = newBoard;
	}
	
	public void rotate180() {
		int[] newBoard = new int[ROWS*COLS];
		
		for (int i=0; i<newBoard.length; i++) {
			newBoard[lookupRotate180[i]] = board[i];
		}
		
		this.board = newBoard;
	}
	
	public int[] compactColumn(final int[] column) {
		
		int newColumn[] = new int[column.length];
		
		int pieces = 0;
		
		for (int i=0; i<column.length; i++) {
			int piece = column[i];
			if (piece > 0) {
				newColumn[pieces++] = piece;
			}
		}
		
		return newColumn;
	}
	
	public int[] slideColumn(int[] column, final boolean calcScore) {
		
		column = compactColumn(column);
		
		boolean needsRecompacting = false;
		
		for (int i=0; i<column.length-1; i++) {
			if (column[i] == column[i+1]) {
				column[i] *= 2;
				column[i+1] = 0;
				needsRecompacting = true;
				if (calcScore) {
					score += column[i];
				}
			}
		}
		
		if (needsRecompacting) {
			column = compactColumn(column);
		}
		return column;
	}
	
	private void slideUp(boolean calcScore) {

		int column[] = new int[ROWS];
		
		for (int x=0; x<COLS; x++) {
			
			for (int y=0; y<ROWS; y++) {
				column[y] = board[y*COLS+x];
			}
			
			column = slideColumn(column, calcScore);
			
			for (int y=0; y<ROWS; y++) {
				board[y*COLS+x] = column[y];
			}
		}
	}
	
	public void makeMove(int direction, boolean calcScore) {
		
		switch (direction) {
			case UP:
				slideUp(calcScore);
				break;
			case DOWN:
				rotate180();
				slideUp(calcScore);
				rotate180();
				break;
			case LEFT: 
				rotateClockwise();
				slideUp(calcScore);
				rotateAntiClockwise();
				break;
			case RIGHT:
				rotateAntiClockwise();
				slideUp(calcScore);
				rotateClockwise();
				break;
		}
		
		movesMade ++;
		
	}
	
	public int[] getBoard() {
		return board;
	}

	public void setBoard(int[] board) {
		this.board = board;
	}
	
	public int getScore() {
		return score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public String toString() {
		String s = "";
		
		for (int i=0; i<COLS; i++) {
			s += "-------";
		}
		
		s += System.getProperty("line.separator");
		
		for (int y=0; y<ROWS; y++) {
			
			for (int x=0; x<COLS; x++) {
				int num = board[y*COLS+x];
				if (num > 0) {
					s += String.format("|%6s", board[y*ROWS+x]);
				} else {
					s += "|      ";
				}
			}
			
			s += "|" + System.getProperty("line.separator");
			
			for (int i=0; i<COLS; i++) {
				s += "-------";
			}
			s += System.getProperty("line.separator");
		}
		
		s += "Score: " + score + ", Game over: " + isGameOver();
		s += System.getProperty("line.separator");
		
		return s;
	}

}

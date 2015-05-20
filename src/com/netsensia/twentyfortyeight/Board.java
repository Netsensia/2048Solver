package com.netsensia.twentyfortyeight;

import java.util.Random;
import java.util.Arrays;

public class Board implements Cloneable {
	
	public static final int ROWS = 4;
	public static final int COLS = 4;
	
	public static final int TOTAL_SQUARES = ROWS * COLS;
	
	public static final int UP = 0;
	public static final int DOWN = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 3;
	
	public static final int MAX_TILE_POWER = 16; // 64k
	
	public static final int SOLVER = 0;
	public static final int BLOCKER = 1;
	
	private int score = 0;
	
	private int board[] = new int[ROWS*COLS];
	
	Random r = new Random();
	
	public Board() {
		for (int i=0; i<ROWS*COLS; i++) {
			board[i] = 0;
		}
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
		
		if (countBlankSpaces() == 0) {
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
	
	public int countBlankSpaces() {
		int blankSpaces = 0;
		for (int i=0; i<ROWS*COLS; i++) {
			if (board[i] == 0) {
				blankSpaces++;
			}
		}
		return blankSpaces;
	}
	
	public boolean isGameOver() {
		
		boolean isGameOver = true;
		
		Board newBoard;
		try {
			newBoard = (Board)this.clone();
		
			for (int i=Board.UP; i<=Board.RIGHT; i++) {
				newBoard.makeMove(i, false);
				if (!Arrays.equals(newBoard.getBoard(), board)) {
					isGameOver = false;
					break;
				}
			}
		
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isGameOver;
	}
	
	public boolean isValidMove(int direction) {
		
		int[] backupBoard = new int[ROWS*COLS];
		
		System.arraycopy( board, 0, backupBoard, 0, board.length );

		this.makeMove(direction, false);
		
		if (Arrays.equals(board, backupBoard)) {
			return false;
		}
		
		System.arraycopy( backupBoard, 0, board, 0, backupBoard.length );
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
		
		for (int x=0; x<ROWS; x++) {
			for (int y=0; y<COLS; y++) {
				// New Y is current x, new x is the inverse of Y: COLS-y-1
				newBoard[x*COLS+(COLS-y-1)] = board[y*COLS+x];
			}
		}
		
		this.board = newBoard;
	}
	
	public void rotateClockwise(int times) {
		for (int i=0; i<times; i++) {
			rotateClockwise();
		}
	}
	
	public int[] compactColumn(int[] column) {
		
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
	
	public int[] slideColumn(int[] column, boolean calcScore) {
		
		column = compactColumn(column);
		
		for (int i=0; i<column.length-1; i++) {
			if (column[i] == column[i+1]) {
				column[i] *= 2;
				column[i+1] = 0;
				if (calcScore) {
					score += column[i];
				}
			}
		}
		
		return compactColumn(column);
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
				rotateClockwise(2);
				slideUp(calcScore);
				rotateClockwise(2);
				break;
			case LEFT: 
				rotateClockwise(1);
				slideUp(calcScore);
				rotateClockwise(3);
				break;
			case RIGHT:
				rotateClockwise(3);
				slideUp(calcScore);
				rotateClockwise(1);
				break;
		}
		
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
	
	protected Object clone() throws CloneNotSupportedException {
		Board clone = new Board();
		int[] cloneBoard = new int[ROWS*COLS];
		
		System.arraycopy( board, 0, cloneBoard, 0, board.length );
		
		clone.setBoard(cloneBoard);
		clone.setScore(score);
		
        return clone;
    }
}

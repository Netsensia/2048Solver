package com.netsensia.twentyfortyeight;

import java.util.Random;
import java.util.Arrays;

public class Board implements Cloneable {
	
	public static final int ROWS = 4;
	public static final int COLS = 4;
	
	public static final int UP = 0;
	public static final int DOWN = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 3;
	
	private int score = 0;
	
	private int board[] = new int[ROWS*COLS];
	
	public Board() {
		for (int i=0; i<ROWS*COLS; i++) {
			board[i] = 0;
		}
	}
	
	public void setRandomStartPosition() {
		int numSquares = ROWS * COLS;
		Random r = new Random();
		
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
		
		boolean isBoardFull = true;
		
		for (int i=0; i<ROWS*COLS; i++) {
			if (board[i] > 0) {
				isBoardFull = false;
				break;
			}
		}
		
		if (isBoardFull) {
			return false;
		}
		
		Random r = new Random();
		int numSquares = ROWS * COLS;
		int square;
		do {
			square = r.nextInt(numSquares);
			if (board[square] == 0) {
				board[square] = r.nextInt(2) == 0 ? 2 : 4;
				break;
			}
		} while (true);
		
		return true;
	}
	
	public int getBlankSpaces() {
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
		
		int[] backupBoard = new int[ROWS*COLS];
		
		System.arraycopy( board, 0, backupBoard, 0, board.length );
		
		for (int i=Board.UP; i<=Board.RIGHT; i++) {
			this.makeMove(i, false);
			if (!Arrays.equals(board, backupBoard)) {
				isGameOver = false;
				break;
			}
		}
		
		System.arraycopy( backupBoard, 0, board, 0, backupBoard.length );
		
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
	
	private void rotateClockwise() {
		int[] newBoard = new int[ROWS*COLS];
		
		for (int x=0; x<ROWS; x++) {
			for (int y=0; y<COLS; y++) {
				int newY = x;
				int newX = COLS - y - 1;
				
				newBoard[newY*COLS+newX] = board[y*COLS+x];
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
		
		int len = column.length;
		int newColumn[] = new int[len];
		
		for (int i=0; i<len; i++) {
			newColumn[i] = 0;
		}
		
		int pieces = 0;
		
		for (int i=0; i<len; i++) {
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
		
		column = compactColumn(column);
		
		return column;
	}
	
	private void slide(boolean calcScore) {

		int column[] = new int[ROWS];
		int newColumn[] = new int[ROWS];
		
		for (int x=0; x<COLS; x++) {
			
			for (int y=0; y<ROWS; y++) {
				column[y] = board[y*COLS+x];
			}
			
			newColumn = slideColumn(column, calcScore);
			
			for (int y=0; y<ROWS; y++) {
				this.place(x, y, newColumn[y]);
			}
		}
	}
	
	public void makeMove(int direction, boolean calcScore) {
		
		switch (direction) {
			case UP:
				slide(calcScore);
				break;
			case DOWN:
				rotateClockwise(2);
				slide(calcScore);
				rotateClockwise(2);
				break;
			case LEFT: 
				rotateClockwise(1);
				slide(calcScore);
				rotateClockwise(3);
				break;
			case RIGHT:
				rotateClockwise(3);
				slide(calcScore);
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

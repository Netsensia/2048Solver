package com.netsensia.twentyfortyeight;

public class Board {
	
	public static final int ROWS = 4;
	public static final int COLS = 4;
	
	public static final int UP = 0;
	public static final int DOWN = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 3;
	
	private int board[] = new int[ROWS*COLS];
	
	public Board() {
		for (int i=0; i<ROWS*COLS; i++) {
			board[i] = 0;
		}
	}
	
	public void place(int x, int y, int number) {
		this.board[y*ROWS+x] = number;
	}
	
	public void rotateClockwise() {
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
	
	public int[] slideColumn(int[] column) {
		
		column = compactColumn(column);
		
		for (int i=0; i<column.length-1; i++) {
			if (column[i] == column[i+1]) {
				column[i] *= 2;
				column[i+1] = 0;
			}
		}
		
		column = compactColumn(column);
		
		return column;
	}
	
	public void slide() {

		for (int x=0; x<COLS; x++) {
			int column[] = new int[ROWS];
			for (int y=0; y<ROWS; y++) {
				column[y] = board[y*COLS+x];
			}
			
			int[] newColumn = slideColumn(column);
			
			for (int y=0; y<ROWS; y++) {
				this.place(x, y, newColumn[y]);
			}
		}
			
	}
	
	public void slide(int direction) {
		
		switch (direction) {
			case UP:
				slide();
				break;
			case DOWN:
				rotateClockwise(2);
				slide();
				rotateClockwise(2);
				break;
			case LEFT: 
				rotateClockwise(1);
				slide();
				rotateClockwise(3);
				break;
			case RIGHT:
				rotateClockwise(3);
				slide();
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
		
		return s;
	}
}

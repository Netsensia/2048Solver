package com.netsensia.twentyfortyeight.test;

import static org.junit.Assert.*;
import com.netsensia.twentyfortyeight.Board;

import org.junit.Test;

public class BoardTest {

	private Board getOneToSixteenBoard() {
		
		Board board = new Board();
    	
    	for (int x=0; x<Board.COLS; x++) {
    		for (int y=0; y<Board.ROWS; y++) {
    			board.place(x, y, y*Board.ROWS + x + 1);
    		}
    	}
    	
    	return board;
	}
	
    @Test
    public void testBoardRotate() {
    	
    	Board board = getOneToSixteenBoard();
    	
    	board.rotateClockwise();
    	
    	int[] expected1 = {
    	    	13,9,5,1,
    	    	14,10,6,2,
    	    	15,11,7,3,
    	    	16,12,8,4
    	    	};
    	
    	assertArrayEquals(
    		expected1,
	    	board.getBoard()
    	);
    	
    	int[] expected2 = {
    	    	16,15,14,13,
    	    	12,11,10,9,
    	    	8,7,6,5,
    	    	4,3,2,1
    	    	};
    	
    	int[] expected3 = {
    	    	4,8,12,16,
    	    	3,7,11,15,
    	    	2,6,10,14,
    	    	1,5,9,13
    	    	};
    	
    	board.rotateClockwise();
    	
    	assertArrayEquals(
    		expected2,
	    	board.getBoard()
    	);
    	
    	board = getOneToSixteenBoard();
    	
    	board.rotate180();
    	
    	assertArrayEquals(
    		expected2,
	    	board.getBoard()
    	);
    	
    	board = getOneToSixteenBoard();
    	
    	board.rotateAntiClockwise();
    	
    	assertArrayEquals(
    		expected3,
	    	board.getBoard()
    	);
    	
    	board = getOneToSixteenBoard();
    	
    	int[] expected4 = board.getBoard();
    	
    	board.rotate180();
    	board.rotate180();
    	
    	assertArrayEquals(
    		expected4,
	    	board.getBoard()
    	);
    	
    	board = getOneToSixteenBoard();
    	
    	board.rotateAntiClockwise();
    	
    	assertArrayEquals(
    		expected3,
	    	board.getBoard()
    	);	
    }
    
    @Test
    public void testCompactColumn() {
    	int[] col = {2,0,2,0,0,5,6,0,9};
    	int[] expected = {2,2,5,6,9,0,0,0,0};
    	
    	Board board = new Board();
    	int[] result = board.compactColumn(col);
    	assertArrayEquals(expected, result);
    }
    
    @Test
    public void testSlideColumn() {
    	int[] col = {2,0,2,5,6,6,0,4,0,2,4,0,0,4};
    	int[] expected = {4,5,12,4,2,8,0,0,0,0,0,0,0,0};
    	
    	Board board = new Board();
    	int[] result = board.slideColumn(col, false);
    	assertArrayEquals(expected, result);
    	
    }
    
    @Test
    public void testPlaceRandomPieceReturnsFalseWhenBoardIsFull() {
    	
    	int[] notFull = {
    	    	0,2,0,2,
    	    	2,2,4,4,
    	    	2,8,2,0,
    	    	0,8,4,4,
    	    	};
    	
    	int[] full = {
    	    	2,2,2,2,
    	    	2,2,4,4,
    	    	2,8,2,2,
    	    	2,8,4,4,
    	    	};
    	
    	Board board = new Board();
    	board.setBoard(notFull);
    	
    	assertTrue(board.placeRandomPiece());
    	
    	board.setBoard(full);
    	
    	assertFalse(board.placeRandomPiece());
    	
    }
    
    @Test
    public void testGetHighestTileValue() {
    	int[] position = {
    	    	0,2,0,2,
    	    	2,2,4,4,
    	    	2,8,2,0,
    	    	0,8,4,4,
    	    	};
    	
    	Board board = new Board();
    	board.setBoard(position);
    	
    	assertEquals(8, board.getHighestTileValue());
    }
    
    @Test
    public void testBoardSlide() {
    	
    	int[] start = {
    	    	0,2,0,2,
    	    	2,2,4,4,
    	    	2,8,2,0,
    	    	0,8,4,4,
    	    	};
    	
    	int[] slideDown = {
    			0,0,0,0,
    			0,0,4,0,
    			0,4,2,2,
    			4,16,4,8
    	};
    	
    	int[] slideUp = {
    			4,4,4,2,
    			0,16,2,8,
    			0,0,4,0,
    			0,0,0,0,
    	};
    	
    	int[] slideLeft = {
    			4,0,0,0,
    			4,8,0,0,
    			2,8,2,0,
    			8,8,0,0,
    	};
    	
    	int[] slideRight = {
    			0,0,0,4,
    			0,0,4,8,
    			0,2,8,2,
    			0,0,8,8,
    	};
    	
    	Board board = new Board();
    	board.setBoard(start);
    	
    	board.makeMove(Board.LEFT, false);
    	
    	assertArrayEquals(
    		slideLeft,
	    	board.getBoard()
    	);
    	
    	board.setBoard(start);
    	
    	board.makeMove(Board.RIGHT, false);
    	
    	assertArrayEquals(
    		slideRight,
	    	board.getBoard()
    	);
    	
    	board.setBoard(start);
    	
    	board.makeMove(Board.UP, false);
    	
    	assertArrayEquals(
    		slideUp,
	    	board.getBoard()
    	);
    	
    	board.setBoard(start);
    	
    	board.makeMove(Board.DOWN, false);
    	
    	assertArrayEquals(
    		slideDown,
	    	board.getBoard()
    	);
    	
    }
    
}

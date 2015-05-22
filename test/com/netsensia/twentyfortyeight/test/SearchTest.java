package com.netsensia.twentyfortyeight.test;

import static org.junit.Assert.*;

import java.util.Random;
import com.netsensia.twentyfortyeight.*;

import org.junit.Test;

public class SearchTest {

	@Test
	public void testGenerateHash() {
		Random r = new Random();
		int numHashPositions = (int)Math.pow(2, Search.HASH_TABLE_POWER);
		
		for (int i=0; i<1000; i++) {
			Board board = new Board();
			for (int x=0; x<Board.COLS; x++) {
				for (int y=0; y<Board.ROWS; y++) {
					int power = r.nextInt(Board.MAX_TILE_POWER);
					int faceValue = (int)Math.pow(2, power);
					board.place(x, y, faceValue);
				}
			}
			
			Search search = new Search();
			int hashkey = search.generateHashKey(board);
			search.generateHashLockValue(board);

		    assertTrue(hashkey >= 0 && hashkey < numHashPositions);
		}
	}
	
	@Test
	public void testEvaluate() {
		int[] position = {
    	    	2,0,0,0,
    	    	2,0,0,0,
    	    	4,0,0,0,
    	    	2,4,0,0,
    	    	};
		
		Board board = new Board();
		board.setScore(10);
		board.setBoard(position);
		
		Search search = new Search();
		search.setEvaluateBlankSpaces(false);
		
		assertEquals(10, search.evaluate(board));
	}
	
	@Test
	public void testNegamax() throws Exception {
		int[] position = {
    	    	2,0,0,0,
    	    	2,0,0,0,
    	    	0,0,0,0,
    	    	0,4,0,0,
    	    	};
		
		Board board = new Board();
		board.setScore(10);
		board.setBoard(position);
		
		Search search = new Search();
		search.setEvaluateBlankSpaces(false);
		
		StringBuilder moves = new StringBuilder();
		
		assertEquals(14, search.negamax(board, 1, Integer.MIN_VALUE, Integer.MAX_VALUE, 1, moves));
		search.setDepth(1);
		assertEquals(Board.UP, search.getMoveFromSearch(board));
		
		assertEquals(14, search.negamax(board, 2, Integer.MIN_VALUE, Integer.MAX_VALUE, 1, moves));
		search.setDepth(2);
		assertEquals(Board.UP, search.getMoveFromSearch(board));
		
		assertEquals(22, search.negamax(board, 3, Integer.MIN_VALUE, Integer.MAX_VALUE, 1, moves));
		search.setDepth(3);
		assertEquals(Board.UP, search.getMoveFromSearch(board));

		assertEquals(22, search.negamax(board, 4, Integer.MIN_VALUE, Integer.MAX_VALUE, 1, moves));
		search.setDepth(4);
		assertEquals(Board.UP, search.getMoveFromSearch(board));
		
		board.place(3, 3, 4);
		board.place(2, 0, 8);
		/*
		 *      2,0,8,0,
    	 *   	2,0,0,0,
    	 *  	0,0,0,0,
    	 *  	0,4,0,4,
		 */
		assertEquals(18, search.negamax(board, 1, Integer.MIN_VALUE, Integer.MAX_VALUE, 1, moves));
		search.setDepth(1);
		assertEquals(Board.LEFT, search.getMoveFromSearch(board));
		
		assertEquals(18, search.negamax(board, 2, Integer.MIN_VALUE, Integer.MAX_VALUE, 1, moves));
		search.setDepth(2);
		assertEquals(Board.LEFT, search.getMoveFromSearch(board));

		assertEquals(38, search.negamax(board, 8, Integer.MIN_VALUE, Integer.MAX_VALUE, 1, moves));
		search.setDepth(8);
		assertEquals(Board.UP, search.getMoveFromSearch(board));
	}
}

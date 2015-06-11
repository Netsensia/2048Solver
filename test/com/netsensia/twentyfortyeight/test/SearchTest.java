package com.netsensia.twentyfortyeight.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Random;

import com.netsensia.twentyfortyeight.*;

import org.junit.Test;

public class SearchTest {

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
		
		assertEquals(51, search.evaluate(board));
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
		
		search.negamax(board, 1, Integer.MIN_VALUE, Integer.MAX_VALUE, 1, moves);
		search.setDepth(1);
		assertEquals(Board.RIGHT, search.getMoveFromSearch(board).getDirection());
		
	}
	
	@Test
	public void testGetOrderedBlockerMoves() throws Exception {
		int[] position = {
    	    	 2, 0, 4, 4,
    	    	 2, 8, 8,16,
    	    	 4, 0, 8, 2,
    	    	 0, 4,32,64,
    	    	};
		
		Board board = new Board();
		board.setBoard(position);
		
		Search search = new Search();
		
		ArrayList<BlockerMove> moves = search.getOrderedBlockerMoves(board);

	}
	
}

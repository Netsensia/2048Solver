package com.netsensia.twentyfortyeight.test;

import static org.junit.Assert.*;

import java.util.Random;

import junit.framework.Assert;

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
    
}

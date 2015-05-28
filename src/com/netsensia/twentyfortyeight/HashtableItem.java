package com.netsensia.twentyfortyeight;

public class HashtableItem {
	
	public static final int UPPER_BOUND = 1;
	public static final int LOWER_BOUND = 2;
	public static final int EXACT_SCORE = 3;
	
	// A lock to confirm that this is the correct item 
	// to avoid clashes of positions with the same hash key.
	public long lock;
	
	// The depth to which this position was searched
	public int height;
	
	// The score given by the search
	public int score;
	
	// Upper bound, lower bound, exact score
	public int type;
	
	Board board;
	
}

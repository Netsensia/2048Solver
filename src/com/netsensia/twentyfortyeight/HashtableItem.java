package com.netsensia.twentyfortyeight;

public class HashtableItem {
	
	// A lock to confirm that this is the correct item 
	// to avoid clashes of positions with the same hash key.
	public long lock;
	
	// The depth to which this position was searched
	public int height;
	
	// The score given by the search
	public int score;
	
}

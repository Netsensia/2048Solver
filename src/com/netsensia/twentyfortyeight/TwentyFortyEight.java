package com.netsensia.twentyfortyeight;

public class TwentyFortyEight {

	public static final int DEPTH = 6;
	
	// Set this to the number of CPU cores
	// Any higher than that and no additional benefit is likely
	public static final int NUM_THREADS = 4;
	
	public static final int RUNS = 1000;
    public static final int POWER_MAX = 32;
	
	public static void main(String args[]) {

		Board.initStaticItems();
		
		ResultsLogger resultsLogger = new ResultsLogger(RUNS);
		
		int numCores = Runtime.getRuntime().availableProcessors();;
		System.out.println("Number of available cores: " + numCores);
		
		for (int i=0; i<NUM_THREADS; i++) {
			new Thread(new GameRunner(resultsLogger, DEPTH)).start();
		}

	}
}

package com.netsensia.twentyfortyeight;

public class TwentyFortyEight {

	public static final int DEPTH = 5;
	
	// A good value for this would be the number of CPU cores
	public static final int NUM_THREADS = 12;
	
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

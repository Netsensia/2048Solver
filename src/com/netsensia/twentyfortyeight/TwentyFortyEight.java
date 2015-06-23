package com.netsensia.twentyfortyeight;

public class TwentyFortyEight {

	public static final int DEPTH = 8;
	
	public static final int RUNS = 1000;
    public static final int POWER_MAX = 32;
	
	public static void main(String args[]) {

		// We don't want to leave it to the Board class
		// to initialise the static items on first use
		// as it will be used in multiple threads.
		Board.initStaticItems();
		
		ResultsLogger resultsLogger = new ResultsLogger(RUNS);
		
		int numCores = Runtime.getRuntime().availableProcessors();;
		System.out.println("Number of available cores: " + numCores);
		
		int numThreads = numCores;
		
		for (int i=0; i<numThreads; i++) {
			new Thread(new GameRunner(resultsLogger, DEPTH)).start();
		}

	}
}

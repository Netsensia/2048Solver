package com.netsensia.twentyfortyeight;

public class TwentyFortyEight {

	public static final int DEPTH = 7;
	
	public static final int RUNS = 1000;
    public static final int POWER_MAX = 32;
	
	public static void main(String args[]) {

		// We don't want to leave it to the Board class
		// to create the static items on first use
		// as it would not be thread safe.
		Board.initStaticItems();
		
		ResultsLogger resultsLogger = new ResultsLogger(RUNS, DEPTH);
		
		int numCores = Runtime.getRuntime().availableProcessors();
		System.out.println("Number of available cores: " + numCores);
		
		int numThreads = numCores;
		
		for (int threadNum=0; threadNum<numThreads; threadNum++) {
			new Thread(new GameRunner(resultsLogger, DEPTH)).start();
		}

	}
}

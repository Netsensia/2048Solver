package com.netsensia.twentyfortyeight;

public class TwentyFortyEight {

	public static final int DEPTH = 1;
	
	public static final int RUNS = 100;
    public static final int POWER_MAX = 32;
	
	public static void main(String args[]) {

		// We don't want to leave it to the Board class
		// to create the static items on first use
		// as it would not be thread safe.
		Board.initStaticItems();
		
		int numCores = Runtime.getRuntime().availableProcessors();
		System.out.println("Number of available cores: " + numCores);
		
		int numThreads = numCores;
		int mod = RUNS % numThreads;
		int adjustedRuns = RUNS - mod;

		int gamesPerThread = (int)(RUNS / numThreads);
		
		for (int depth=1; depth<=6; depth++) {
			ResultsLogger resultsLogger = new ResultsLogger(adjustedRuns, depth, numThreads);
			resultsLogger.setPrintSummaryAfterNGames(adjustedRuns);
			resultsLogger.setIsCsvOnly(true);
			
			Thread[] threadGroup = new Thread[numThreads];
			
			for (int threadNum=0; threadNum<numThreads; threadNum++) {
				GameRunner gameRunner = new GameRunner(resultsLogger, depth, gamesPerThread);
				threadGroup[threadNum] = new Thread(gameRunner);
				threadGroup[threadNum].start();
			}
			
			// wait for these threads to finish before starting next depth
			for (int threadNum=0; threadNum<numThreads; threadNum++) {
				try {
					threadGroup[threadNum].join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
}

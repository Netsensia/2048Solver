package com.netsensia.twentyfortyeight;

public class TwentyFortyEight {

	public static final int MIN_DEPTH = 2;
	public static final int MAX_DEPTH = 2;
	
	public static final int RUNS = 12;
    public static final int POWER_MAX = 32;
	
    public static final int[] DEPTH_RUNS = {
    	0,
    	100000,   // 1
    	25000,    // 2
    	25000,     // 3
    	25000,      // 4
    	5000,       // 5
    	1000,       // 6
    	750,      // 7
    	250,      // 8
    	175,      // 9
    	125,       // 10
    	100,       // 11
    };
    
	public static void main(String args[]) {

		// We don't want to leave it to the Board class
		// to create the static items on first use
		// as it would not be thread safe.
		Board.initStaticItems();
		
		long start = System.currentTimeMillis();
		
		int numCores = Runtime.getRuntime().availableProcessors();
		System.out.println("Number of available cores: " + numCores);
		
		int numThreads = numCores;
		
		StringBuilder sb = new StringBuilder();
		StringBuilder headers = new StringBuilder();
		
		int closeWeightIndex = 2;
		
		for (int q=0; q<=100; q++) {
			
			for (int qq=closeWeightIndex+1; qq<Search.EVALUATION_CLOSE_WEIGHTS.length; qq++) {
				Search.EVALUATION_CLOSE_WEIGHTS[qq] = 1;
			}
		
			Search.EVALUATION_CLOSE_WEIGHTS[closeWeightIndex] = 1 + (double)q / 100.0;
					
			System.out.println("Search.EVALUATION_CLOSE_WEIGHTS[" + closeWeightIndex + "] = " + Search.EVALUATION_CLOSE_WEIGHTS[closeWeightIndex]);
			
			for (int depth=MIN_DEPTH; depth<=MAX_DEPTH; depth++) {
			
				int runs = DEPTH_RUNS[depth];
	
				int gamesPerThread = (int)(runs / numThreads);
				
				if (gamesPerThread == 0) {
					gamesPerThread = 1;
				}
				
				int adjustedRuns = gamesPerThread * numThreads;
	
				ResultsLogger resultsLogger = new ResultsLogger(adjustedRuns, depth);
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
						e.printStackTrace();
					}
				}
				
				headers.append(Search.EVALUATION_CLOSE_WEIGHTS[closeWeightIndex] + ",");
				sb.append(resultsLogger.getLastAverageScore() + ",");
			}
		}
		
		long millis = System.currentTimeMillis() - start;
		int minutes = (int)millis / 60000;
		int seconds = (int)millis % 60000 / 1000;
		
		System.out.println("Time taken: " + minutes + " minutes, " + seconds + " seconds");
		
		
		System.out.println("Average Scores");
		System.out.println(headers);
		System.out.println(sb);
		
	}
}

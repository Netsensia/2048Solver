package com.netsensia.twentyfortyeight;

import java.text.NumberFormat;

public class ResultsLogger {
	
	public static final String PROCESSOR = "2.6 GHz Intel Core i7";
	public static final String VERSION = "2.3";

	public static final int POWER_MAX = 32;
	
	public static final int POWER_MIN = 3;
    
	private int depth;
	private long totalScore = 0;
	private int highScore = 0;
	private int highestTileValue = 0;
	private int wins[] = new int[POWER_MAX];
	private long totalGameTime = 0;
	private long totalMoves = 0;
	private int gameNumber = 0;
	private int runs;
	private long startTime;
	
    public ResultsLogger(int runs, int depth) {
    	this.runs = runs;
    	this.depth = depth;
    	this.startTime = System.currentTimeMillis();
    }
	
	public synchronized void log(Board board) {

		gameNumber ++;
		System.out.println("Game: " + gameNumber);
			
		int score = board.getScore();
		if (score > highScore) {
			highScore = score;
		}
		
		totalScore += score;
		int t = board.getHighestTileValue();
		if (t > highestTileValue) {
			highestTileValue = t;
		}
				
		/**
		 *  wins[3] = number of games with an 8 tile
		 *  wins[11] = number of games with a 2048 tile
		 */
		for (int j=POWER_MAX-1; j>=POWER_MIN; j--) {
			int tileValue = (int)Math.pow(2, j);
			if (t >= tileValue) {
				wins[j] ++;
			}
		}
		
		long gameTime = System.currentTimeMillis() - board.getGameStartMillis();
		long realTime = System.currentTimeMillis() - this.startTime;
		
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(4);
		totalGameTime += gameTime;
		totalMoves += board.getMovesMade();
		double averageMoveTime = (double)gameTime / board.getMovesMade();
		
		double averageTime = (double)realTime / gameNumber;
		int gamesLeft = runs - gameNumber;
		double timeLeft = gamesLeft * averageTime;
		
		int averageScore = (int)(totalScore / gameNumber);
		
		printResults(board, gameTime, realTime, nf, averageMoveTime, timeLeft, averageScore);
		
		if (gameNumber == runs) {
			System.exit(0);
		}
	}

	private void printResults(Board board, long gameTime, long realTime,
			NumberFormat nf, double averageMoveTime, double timeLeft,
		    int averageScore) {
		
		StringBuilder human = new StringBuilder();
		StringBuilder csv = new StringBuilder();
		for (int j=14; j>=POWER_MIN; j--) {
			int tileValue = (int)Math.pow(2, j);
			int winCount = wins[j];
			human.append(tileValue + "s: ");
			
			double winPercent = ((double)winCount / gameNumber) * 100.0;
			
			human.append(nf.format(winPercent) + "%");
			
			if (winPercent > 0) {
				csv.append(nf.format(winPercent) + "%,");
			} else {
				csv.append(",");
			}
			
			if (j != POWER_MIN) {
				human.append(" | ");
			}
		}
		
		csv.append(PROCESSOR + "," + Runtime.getRuntime().availableProcessors() + "," + VERSION);
		
		System.out.println("Number of moves: " + board.getMovesMade() + ", Score: " + board.getScore());
		System.out.println("Game time: " + gameTime + ", Average move time: " + nf.format(averageMoveTime));
		System.out.println("-----------------------------------------------------------------------------------------------------------");
		System.out.println("Total real time: " + nf.format(realTime) + ", Total game time: " + nf.format(totalGameTime)  + ", Total game moves: " + nf.format(totalMoves));
		System.out.println("Real average move time: " + nf.format((double)realTime / totalMoves));
		System.out.println("Overall average move time: " + nf.format((double)totalGameTime / totalMoves));
		System.out.println("Average score = " + averageScore + ", Highest score: " + highScore + ", Highest tile value: " + highestTileValue);
		
		System.out.println("-----------------------------------------------------------------------------------------------------------");
		System.out.println(human);
		System.out.println("-----------------------------------------------------------------------------------------------------------");
		System.out.println("CSV");
		System.out.println("---");
		System.out.print(depth + "," + gameNumber + "," + realTime + "," + totalGameTime + "," + totalMoves + "," + averageScore + "," + highScore);
		System.out.print(",,,,,,");
		System.out.println(csv);
		System.out.println("-----------------------------------------------------------------------------------------------------------");
		
		System.out.println("Estimated time left: " + nf.format(timeLeft / 60000) + " minutes");
		System.out.println("===========================================================================================================");
	}
	
}

package com.netsensia.twentyfortyeight;

import java.text.NumberFormat;

public class ResultsLogger {

	public static final int POWER_MAX = 32;
    
	private int totalScore = 0;
	private int highScore = 0;
	private int highestTileValue = 0;
	private int wins[] = new int[POWER_MAX];
	private long totalTime = 0;
	private long totalMoves = 0;
	private int gameNumber = 0;
	private int runs;
	private long startTime;
	
    public ResultsLogger(int runs) {
    	this.runs = runs;
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
		for (int j=POWER_MAX-1; j>=3; j--) {
			int tileValue = (int)Math.pow(2, j);
			if (t >= tileValue) {
				wins[j] ++;
			}
		}
		
		long gameTime = System.currentTimeMillis() - board.getGameStartMillis();
		long realTime = System.currentTimeMillis() - this.startTime;
		
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		totalTime += gameTime;
		totalMoves += board.getMovesMade();
		double averageMoveTime = (double)gameTime / board.getMovesMade();
		double totalAverageMoveTime = (double)totalTime / totalMoves;
		
		double averageTime = (double)totalTime / gameNumber;
		int gamesLeft = runs - gameNumber;
		double timeLeft = gamesLeft * averageTime;
		
		System.out.println("Time: " + gameTime + ", Number of moves: " + board.getMovesMade() + ", Score: " + board.getScore());
		System.out.println("Average move time: " + nf.format(averageMoveTime));
		System.out.println("-----------------------------------------------------------------------------------------------------------");
		System.out.println("Total real time: " + realTime);
		System.out.println("Total game Time: " + totalTime  + ", Average game time: " + nf.format(averageTime) + ", Average move time: " + nf.format(totalAverageMoveTime));
		System.out.println("Average score = " + (totalScore / gameNumber) + ", Highest score: " + highScore + ", Highest tile value: " + highestTileValue);
	
		nf.setMaximumFractionDigits(4);
		StringBuilder sb = new StringBuilder();
		for (int j=14; j>=8; j--) {
			int tileValue = (int)Math.pow(2, j);
			int winCount = wins[j];
			sb.append(tileValue + "s: ");
			
			double winPercent = ((double)winCount / gameNumber) * 100.0;
			
			sb.append(nf.format(winPercent) + "%");
			
			if (j != 8) {
				sb.append(" | ");
			}
			
		}
		System.out.println("-----------------------------------------------------------------------------------------------------------");
		System.out.println(sb);
		System.out.println("-----------------------------------------------------------------------------------------------------------");
		
		System.out.println("Estimated time left: " + nf.format(timeLeft / 60000) + " minutes");
		System.out.println("===========================================================================================================");
		
		if (gameNumber == runs) {
			System.exit(0);
		}
		
	}
	
}

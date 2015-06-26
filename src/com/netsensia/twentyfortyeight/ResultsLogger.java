package com.netsensia.twentyfortyeight;

import java.text.NumberFormat;

public class ResultsLogger {
	
	public static final String PROCESSOR = System.getenv("PROCESSOR");
	public static final String VERSION = "2.3.13";

	public static String newLine = System.getProperty("line.separator");
	
	public static final int POWER_MAX = 32;
	
	public static final int POWER_MIN = 3;
    
	private boolean isCsvOnly = false;
	private int printSummaryAfterNGames = 1;
	private String lastResultsString;
	private int gamesLogged = 0;
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
		
		totalGameTime += gameTime;
		totalMoves += board.getMovesMade();
		double averageMoveTime = (double)gameTime / board.getMovesMade();
		
		double averageTime = (double)realTime / gameNumber;
		int gamesLeft = runs - gameNumber;
		double timeLeft = gamesLeft * averageTime;
		
		int averageScore = (int)(totalScore / gameNumber);
		
		lastResultsString = getResultsString(gameNumber, board, gameTime, realTime, averageMoveTime, timeLeft, averageScore);
		
		gamesLogged = gameNumber;
		
		if (gamesLogged % printSummaryAfterNGames == 0) {
			System.out.println(lastResultsString);
		}

	}

	public synchronized String getResultsString(int gameNumber, Board board, long gameTime, long realTime,
			double averageMoveTime, double timeLeft, int averageScore) {
		
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(4);
		
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
		
		StringBuilder results = new StringBuilder();
		
		if (!isCsvOnly) {
			results.append("Game: " + gameNumber);
			results.append(newLine);
			results.append("Number of moves: " + board.getMovesMade() + ", Score: " + board.getScore());
			results.append(newLine);
			results.append("Game time: " + gameTime + ", Average move time: " + nf.format(averageMoveTime));
			results.append(newLine);
			results.append("-----------------------------------------------------------------------------------------------------------");
			results.append(newLine);
			results.append("Total real time: " + nf.format(realTime) + ", Total game time: " + nf.format(totalGameTime)  + ", Total game moves: " + nf.format(totalMoves));
			results.append(newLine);
			results.append("Real average move time: " + nf.format((double)realTime / totalMoves));
			results.append(newLine);
			results.append("Overall average move time: " + nf.format((double)totalGameTime / totalMoves));
			results.append(newLine);
			results.append("Average score = " + averageScore + ", Highest score: " + highScore + ", Highest tile value: " + highestTileValue);
			results.append(newLine);
			
			results.append("-----------------------------------------------------------------------------------------------------------");
			results.append(newLine);
			results.append(human);
			results.append(newLine);
			results.append("-----------------------------------------------------------------------------------------------------------");
			results.append(newLine);
			results.append("CSV");
			results.append(newLine);
			results.append("---");
			results.append(newLine);
		}
		
		results.append(depth + "," + gameNumber + "," + realTime + "," + totalGameTime + "," + totalMoves + "," + averageScore + "," + highScore);
		results.append(",,,,,,");
		results.append(csv);
		
		if (!isCsvOnly) {
			results.append("-----------------------------------------------------------------------------------------------------------");
			results.append(newLine);
			
			results.append("Estimated time left: " + nf.format(timeLeft / 60000) + " minutes");
			results.append(newLine);
			results.append("===========================================================================================================");
			results.append(newLine);
		}
		
		return results.toString();
		
	}

	public String getLastResultsString() {
		return lastResultsString;
	}

	public void setLastResultsString(String lastResultsString) {
		this.lastResultsString = lastResultsString;
	}

	public int getPrintSummaryAfterNGames() {
		return printSummaryAfterNGames;
	}

	public void setPrintSummaryAfterNGames(int printSummaryAfterNGames) {
		this.printSummaryAfterNGames = printSummaryAfterNGames;
	}

	public boolean getIsCsvOnly() {
		return isCsvOnly;
	}

	public void setIsCsvOnly(boolean isCsvOnly) {
		this.isCsvOnly = isCsvOnly;
	}
	
}

package com.netsensia.twentyfortyeight;

import java.text.NumberFormat;

public class TwentyFortyEight {

	public static final int RUNS = 1000;
	public static final int DEPTH = 7;
	
	public static void main(String args[]) {
		
		Board board = null;
		int totalScore = 0;
		int highScore = 0;
		int highestTileValue = 0;
		int wins = 0;
		int halfWins = 0;
		int doubleWins = 0;
		int quadWins = 0;
		int doubleQuadWins = 0;
		
		long totalTime = 0;
		long totalMoves = 0;
		
		for (int i=1; i<=RUNS; i++) {
			
			System.out.println("Game: " + i);
			
			long thisTimeStart = 0;
			long thisTime = 0;
			
			try {
				thisTimeStart = System.currentTimeMillis();
				board = playGame();
				thisTime = System.currentTimeMillis() - thisTimeStart;
				if (DEPTH > 7) {
					System.out.println(board);
				}
				int score = board.getScore();
				if (score > highScore) {
					highScore = score;
				}
				
				totalScore += score;
				int t = board.getHighestTileValue();
				if (t > highestTileValue) {
					highestTileValue = t;
				}
				
				if (t >= 16384) doubleQuadWins ++;
				if (t >= 8192) quadWins ++;
				if (t >= 4096) doubleWins ++;
				if (t >= 2048) wins ++;
				if (t >= 1024) halfWins ++;
				
			} catch (Exception e) {
				System.out.println(e);
				System.exit(1);
			}
			
			NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(2);
			totalTime += thisTime;
			totalMoves += board.getMovesMade();
			double averageMoveTime = (double)thisTime / board.getMovesMade();
			double totalAverageMoveTime = (double)totalTime / totalMoves;
			
			double winPercent = ((double)wins / i) * 100.0;
			double halfWinPercent = ((double)halfWins / i) * 100.0;
			double doubleWinPercent = ((double)doubleWins / i) * 100.0;
			double quadWinPercent = ((double)quadWins / i) * 100.0;
			double doubleQuadWinPercent = ((double)doubleQuadWins / i) * 100.0;
			double averageTime = totalTime / i;
			int gamesLeft = RUNS - i;
			double timeLeft = gamesLeft * averageTime;
			
			System.out.println("Time: " + thisTime + ", Number of moves: " + board.getMovesMade() + ", Score: " + board.getScore());
			System.out.println("Average move time: " + nf.format(averageMoveTime));
			System.out.println("-----------------------------------------------------------------------------------------------------------");
			System.out.println("Total Time: " + totalTime  + ", Average time: " + (int)averageTime + ", Average move time: " + nf.format(totalAverageMoveTime));
			System.out.println("Average score = " + (totalScore / i) + ", Highest score: " + highScore + ", Highest tile value: " + highestTileValue);
			System.out.println("16384s: " + doubleQuadWins + "(" + nf.format(doubleQuadWinPercent) + "%), 8192s: " + quadWins + "(" + nf.format(quadWinPercent) + "%), 4096s: " + doubleWins + "(" + nf.format(doubleWinPercent) + "%), 2048s: " + wins + "(" + nf.format(winPercent) + "%), 1024s: " + halfWins + "(" + nf.format(halfWinPercent) + "%)");
			System.out.println("-----------------------------------------------------------------------------------------------------------");
			System.out.println("Estimated time left: " + (int)(timeLeft / 60000) + " minutes");
			System.out.println("===========================================================================================================");
		}
		
	}
	
	public static Board playGame() throws Exception {
		Board board = new Board();
		board.setRandomStartPosition();
		
		if (DEPTH > 7) {
			System.out.println("Starting position:");
			System.out.println(board);
		}

		Search search = new Search();
		
		while (!board.isGameOver()) {
			
			search.setMode(Search.SEARCH);
			
			search.setDepth(DEPTH);
			
			SolverMove solverMove = search.getBestMove(board);
			
			if (board.isValidMove(solverMove.getDirection())) {
				board.makeMove(solverMove.getDirection(), true);				
				board.placeRandomPiece();
				
			} else {
				System.out.println("Illegal move: " + solverMove.getDirection());
				System.out.println(board);
				System.exit(1);
			}
			
		}

		return board;
	}
}

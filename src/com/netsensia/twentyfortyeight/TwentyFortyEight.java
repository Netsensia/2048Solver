package com.netsensia.twentyfortyeight;

import java.text.NumberFormat;

public class TwentyFortyEight {

	public static final int RUNS = 100000;
	public static final int DEPTH = 3;
    public static final int POWER_MAX = 32;
	
	public static void main(String args[]) {
		
		Board board = null;
		int totalScore = 0;
		int highScore = 0;
		int highestTileValue = 0;
		int wins[] = new int[POWER_MAX];
		
		long totalTime = 0;
		long totalMoves = 0;
		
		for (int i=1; i<=RUNS; i++) {
			
			System.out.println("Game: " + i);
			
			long thisTimeStart = 0;
			long thisTime = 0;
			
			try {
				thisTimeStart = System.currentTimeMillis();
				
				board = playGame();
				// Final board position available via board.toString()

				thisTime = System.currentTimeMillis() - thisTimeStart;

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

			} catch (Exception e) {
				System.out.println(e);
				System.exit(1);
			}
			
			NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(4);
			totalTime += thisTime;
			totalMoves += board.getMovesMade();
			double averageMoveTime = (double)thisTime / board.getMovesMade();
			double totalAverageMoveTime = (double)totalTime / totalMoves;
			
			double averageTime = (double)totalTime / i;
			int gamesLeft = RUNS - i;
			double timeLeft = gamesLeft * averageTime;
			
			System.out.println("Time: " + thisTime + ", Number of moves: " + board.getMovesMade() + ", Score: " + board.getScore());
			System.out.println("Average move time: " + nf.format(averageMoveTime));
			System.out.println("-----------------------------------------------------------------------------------------------------------");
			System.out.println("Total Time: " + totalTime  + ", Average time: " + nf.format(averageTime) + ", Average move time: " + nf.format(totalAverageMoveTime));
			System.out.println("Average score = " + (totalScore / i) + ", Highest score: " + highScore + ", Highest tile value: " + highestTileValue);
			
			StringBuilder sb = new StringBuilder();
			for (int j=14; j>=8; j--) {
				int tileValue = (int)Math.pow(2, j);
				int winCount = wins[j];
				sb.append(tileValue + "s: ");
				
				double winPercent = ((double)winCount / i) * 100.0;
				
				sb.append(nf.format(winPercent) + "%");
				
				if (j != 8) {
					sb.append(" | ");
				}
				
			}
			System.out.println("-----------------------------------------------------------------------------------------------------------");
			System.out.println(sb);
			System.out.println("-----------------------------------------------------------------------------------------------------------");

			
			System.out.println("Estimated time left: " + (int)(timeLeft / 60000) + " minutes");
			System.out.println("===========================================================================================================");
		}
		
	}
	
	public static Board playGame() throws Exception {
		Board board = new Board();
		board.setRandomStartPosition();

		Search search = new Search();
		
		while (!board.isGameOver()) {
			
			search.setDepth(DEPTH);
			
			int solverMove = search.getMoveFromSearch(board);
			
			if (board.isValidMoveFast(solverMove)) {
				board.makeMove(solverMove, true);				
				board.placeRandomPiece();
				
			} else {
				System.out.println("Illegal move: " + solverMove);
				System.out.println(board);
				System.exit(1);
			}
			
		}

		return board;
	}
}

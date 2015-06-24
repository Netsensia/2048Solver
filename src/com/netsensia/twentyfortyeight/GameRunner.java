package com.netsensia.twentyfortyeight;

public class GameRunner implements Runnable {

	private ResultsLogger resultsLogger;
	
	private int depth;
	
    public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public GameRunner(ResultsLogger resultsLogger, int depth) {
    	this.resultsLogger = resultsLogger;
    	this.depth = depth;
    }
	
	public void run() {
		while (true) {
			resultsLogger.log(playGame());
		}
	}
	
	public Board playGame() {
		Board board = new Board();
		board.setRandomStartPosition();
		
		Search search = new Search();
		
		board.setGameStartMillis(System.currentTimeMillis());
		
		while (!board.isGameOver()) {
			
			search.setDepth(depth);
			
			try {
				int solverMove = search.getMoveFromSearch(board);
				if (board.isValidMoveFast(solverMove)) {
					board.makeMove(solverMove, true);
					board.placeRandomPiece();
				} else {
					System.out.println("Illegal move: " + solverMove);
					System.out.println(board);
					System.exit(1);
				}
			} catch (Exception e) {
				System.out.println(e);
				System.exit(0);
			}
			
		}

		return board;
	}
}

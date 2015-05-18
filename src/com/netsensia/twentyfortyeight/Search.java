package com.netsensia.twentyfortyeight;

public class Search {
	
	int depth = 1;
	
	public int getDepth() {
		return depth;
	}

	public int score(Board board, int move) {
		int bestScore = 0;
		
		try {
			Board newBoard = (Board) board.clone();
			
			newBoard.makeMove(move, true);
			int score = newBoard.getScore();
			if (score > bestScore) {
				bestScore = score;
			}
			
		} catch (CloneNotSupportedException e) {
			
		}
		
		return bestScore;
	}
	
	public int getBestMove(Board board, int depth, int low, int high) {
		int bestMove = 0;
		int bestScore = 0;
		
		for (int i=Board.UP; i<Board.RIGHT; i++) {
			if (board.isValidMove(i)) {
				int score = score(board, i);
				if (score > bestScore) {
					bestScore = score;
					bestMove = i;
				}
			}
		}
		
		return bestMove;
	}
	
}

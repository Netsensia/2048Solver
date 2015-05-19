package com.netsensia.twentyfortyeight;

import java.util.ArrayList;
import java.util.Random;

public class Search {
	
	public static final int RANDOM = 0;
	public static final int SCORE = 1;
	public static final int SEARCH = 2;
	
	int depth = 1;

	int mode = RANDOM;
	
	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getDepth() {
		return depth;
	}
	
	public void setDepth(int depth) {
		this.depth = depth;
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
	
	public ArrayList<Integer> getLegalMoves(Board board) {
		ArrayList<Integer> legalMoves = new ArrayList<Integer>();
		
		for (int i=Board.UP; i<=Board.RIGHT; i++) {
			if (board.isValidMove(i)) {
				legalMoves.add(i);
			}
		}
		
		return legalMoves;
	}
	
	public int getRandomMove(Board board) {
		ArrayList<Integer> legalMoves = getLegalMoves(board);
		
		Random r = new Random();
		return legalMoves.get(r.nextInt(legalMoves.size()));
	}
	
	public int getMoveBasedOnImmediateScore(Board board) {
		int bestMove = -1;
		int bestScore = -1;
		
		ArrayList<Integer> legalMoves = getLegalMoves(board);
		
		for (Integer move : legalMoves) {
			int score = score(board, move);
			if (score > bestScore) {
				bestScore = score;
				bestMove = move;
			}
		}
		
		return bestMove;
		
	}
	
	public int getSearchScore(Board board) throws Exception {
		return getSearchScore(board, depth);
	}
	
	public int getSearchScore(Board board, int depth) throws Exception {
		
		int bestScore = -1;
		
		ArrayList<Integer> legalMoves = getLegalMoves(board);
		
		if (depth == 0 || legalMoves.size() == 0) {
			return board.getScore();
		}
		
		for (Integer move : legalMoves) {
			try {
				Board newBoard = (Board)board.clone();
				newBoard.makeMove(move, true);
				
				int spaces = newBoard.getBlankSpaces();
				
				if (spaces > 0) {
					
					int totalScore = 0;
					
					for (int i=0; i<spaces; i++) {
						Board anotherNewBoard = (Board)newBoard.clone();
						
						anotherNewBoard.placeRandomPiece();
						
						totalScore += getSearchScore(newBoard, depth-1);
					}
					
					int averageScore = totalScore / spaces;
					
					if (averageScore > bestScore) {
						bestScore = averageScore;
					}
				}
				
			} catch (CloneNotSupportedException e) {
				
			}
			
		}
		
		return bestScore;
	}
	
	public int getMoveFromSearch(Board board) throws Exception {
		int bestScore = -1;
		int bestMove = -1;
		
		ArrayList<Integer> legalMoves = getLegalMoves(board);
		
		if (legalMoves.size() == 0) {
			throw new Exception("No legal moves for position\n " + board);
		}
		
		for (Integer move : legalMoves) {
			try {
				Board newBoard = (Board)board.clone();
			
				newBoard.makeMove(move, true);
				
				int score = getSearchScore(newBoard);
				
				if (score > bestScore) {
					bestScore = score;
					bestMove = move;
				}
			} catch (CloneNotSupportedException e) {
				
			}
		}
		
		if (bestMove == -1) {
			throw new Exception("No move resulted in a postive score for position\n " + board + "\nLegal moves: " + legalMoves);
		}
		
		return bestMove;
	}
	
	public int getBestMove(Board board) throws Exception {
		
		switch (getMode()) {
			case Search.RANDOM: return getRandomMove(board);
			case Search.SCORE: return getMoveBasedOnImmediateScore(board);
			case Search.SEARCH: return getMoveFromSearch(board);
			default:
				return getRandomMove(board);
		}
	}
	
}

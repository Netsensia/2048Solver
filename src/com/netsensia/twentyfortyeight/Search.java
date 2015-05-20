package com.netsensia.twentyfortyeight;

import java.util.ArrayList;
import java.util.Random;
import java.util.Hashtable;

public class Search {
	
	public static final int RANDOM_MOVES_TO_PLAY = 3;
	
	public static final int RANDOM = 0;
	public static final int SCORE = 1;
	public static final int SEARCH = 2;
	public static final int HASH_TABLE_POWER = 16; // Math.pow(2,HASH_TABLE_POWER) positions
	
	public static final boolean USE_HASH_TABLE = true;
	
	public int[][] pieceSquareZorbrist = new int[Board.TOTAL_SQUARES][Board.MAX_TILE_POWER];
	public int[][] pieceSquareZorbristLock = new int[Board.TOTAL_SQUARES][Board.MAX_TILE_POWER];
	
	public Hashtable<Integer, HashtableItem> hashtable = new Hashtable<Integer, HashtableItem>();
	
	public int hashClashes = 0;
	public int hashHits = 0;
	
	Random r = new Random();
	
	int depth = 1;

	int mode = RANDOM;
	
	public Search() {
		int numPositions = (int)Math.pow(2, HASH_TABLE_POWER);
		
		for (int i=0; i<Board.TOTAL_SQUARES; i++) {
			for (int j=0; j<Board.MAX_TILE_POWER; j++) {
				pieceSquareZorbrist[i][j] = r.nextInt(numPositions);
				pieceSquareZorbristLock[i][j] = r.nextInt(Integer.MAX_VALUE);
			}
		}
		
	}
	
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
	
	public int generateHashKey(Board board) {
		int hashKey = 0;
		
		for (int x=0; x<Board.COLS; x++) {
			for (int y=0; y<Board.ROWS; y++) {
				int piece = board.getSquare(x, y);
				if (piece != 0) {
					int power = (int)(Math.log(piece) / Math.log(2));
					hashKey ^= pieceSquareZorbrist[y*Board.COLS+x][power];
				}
			}
		}
		
		return hashKey;
	}
	
	public int generateHashLockValue(Board board) {
		int hashKey = 0;
		
		for (int x=0; x<Board.COLS; x++) {
			for (int y=0; y<Board.ROWS; y++) {
				int piece = board.getSquare(x, y);
				if (piece != 0) {
					int power = (int)(Math.log(piece) / Math.log(2));
					hashKey ^= pieceSquareZorbristLock[y*Board.COLS+x][power];
				}
			}
		}
		
		return hashKey;
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
		
		int hashkey;
		
		if (USE_HASH_TABLE) {
			hashkey = generateHashKey(board);
	
			HashtableItem find = hashtable.get(hashkey);
			
			if (find != null) {
				if (find.lock == generateHashLockValue(board) && find.height >= depth) {
					hashHits ++;
					return find.score;
				} else {
					hashClashes ++;
				}
			}
		}
		
		int bestScore = -1;
		
		ArrayList<Integer> legalMoves = getLegalMoves(board);
		
		if (depth == 0 || legalMoves.size() == 0) {
			return evaluate(board, legalMoves);
		}
		
		for (Integer move : legalMoves) {
			try {
				Board newBoard = (Board)board.clone();
				
				newBoard.makeMove(move, true);
				
				Board anotherBoard;
				
				int totalScore = 0;
				int totalTries = 0;
				int availableMoves = newBoard.countBlankSpaces() * 2;
				for (int i=0; i<RANDOM_MOVES_TO_PLAY && i<availableMoves; i++) {
					 anotherBoard = (Board)newBoard.clone();
					 anotherBoard.placeRandomPiece();
					 totalScore += getSearchScore(anotherBoard, depth-1);
					 totalTries ++;
				}
				int averageScore = totalScore / totalTries;
				
				if (averageScore > bestScore) {
					bestScore = averageScore;
				}
				
			} catch (CloneNotSupportedException e) {
				
			}
			
		}
		
		if (USE_HASH_TABLE) {
			HashtableItem find = hashtable.get(hashkey);
			
			if (find == null || find.height >= depth) {
				if (find == null) {
					find = new HashtableItem();
				}
				find.height = depth;
				find.score = bestScore;
				find.lock = generateHashLockValue(board);
				hashtable.put(hashkey, find);
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
	
	public int evaluate(Board board, ArrayList<Integer>legalMoves) {

		int bestScore = 0;
		
		if (legalMoves.size() == 0) {
			return board.getScore();
		}
		
		for (Integer move : legalMoves) {
			try {
				Board newBoard = (Board)board.clone();
			
				newBoard.makeMove(move, true);
				
				int score = newBoard.getScore();
				
				if (score > bestScore) {
					bestScore = score;
				}
			} catch (CloneNotSupportedException e) {
				
			}
		}
		
		return bestScore;
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

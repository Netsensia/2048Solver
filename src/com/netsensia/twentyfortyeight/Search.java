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
	
	public int getBestMove(Board board) throws Exception {
		
		switch (getMode()) {
			case Search.RANDOM: return getRandomMove(board);
			case Search.SCORE: return getMoveBasedOnImmediateScore(board);
			case Search.SEARCH: return getMoveFromSearch(board);
			default:
				return getRandomMove(board);
		}
	}
	
	/**
	 * Absolute difference between value "me" and the value of square x,y
	 * or zero if square x,y is zero.
	 * 
	 * @param board
	 * @param me
	 * @param x
	 * @param y
	 * @return
	 */
	public int neighbourScore(Board board, int me, int x, int y) {
		
		if (x > -1 && y > -1 && x < Board.COLS && y < Board.ROWS) {
			int them = board.getSquare(x, y);
			if (them > 0) {
				return Math.abs(them - me);
			}
		}

		return 0;
	}
	
	public int neighbourAverageScore(Board board, int x, int y) {
		int total = 0;
		int count = 0;
		int score;
		
		int me = board.getSquare(x, y);
		if (me > 0) {
			for (int nx=-1; nx<=1; nx++) {
				for (int ny=-1; ny<=1; ny++) {
					if (nx == 0 && ny == 0) {
						continue;
					}
					score = neighbourScore(board, me, x+nx, y+ny);
					if (score > 0) {
						total += score;
						count ++;
					}
				}
			}
			
		}
		
		if (count > 0) {
			return total / count;
		}
		
		return 0;
	}
	
	public int trappedPenalty(Board board) {
		int trappedPenalty = 0;
		
		for (int x=0; x<Board.ROWS; x++) {
			for (int y=0; y<Board.COLS; y++) {
				trappedPenalty += neighbourAverageScore(board, x, y);
			}
		}
		
		return trappedPenalty;
	}
	
	public int evaluate(Board board) {
		
		int score = board.getScore();
		
		if (score > 0) {
			score += Math.log(score) * board.countBlankSpaces();
		}

		return score;
	   		
	}
	
	public int negamax(Board board, final int depth, int low, int high, int mover) throws Exception {
		
		ArrayList<Integer> legalMoves = getLegalMoves(board);
		
		if (depth == 0) {
			return mover * evaluate(board);
		}

		int bestScore = Integer.MIN_VALUE;
		
		if (mover == 1) {
			
			if (legalMoves.size() == 0) {
				return mover * evaluate(board);
			}
			
			for (Integer move : legalMoves) {
				Board newBoard;
				try {
					newBoard = (Board)board.clone();
					newBoard.makeMove(move, true);
					
					int score = -negamax(newBoard, depth-1, -high, -low, -1);
					bestScore = Math.max(bestScore, score);
					low = Math.max(low, score);
					
					if (low >= high) {
						return bestScore;
					}
					
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
		} else {
			
			if (board.countBlankSpaces() == 0) {
				return mover * evaluate(board);
			}
			
			int count = 0;
			for (int x=0; x<Board.COLS; x++) {
				for (int y=0; y<Board.ROWS; y++) {
					if (board.getSquare(x, y) == 0) {
						Board newBoard;
						try {
							for (int piece=2; piece<=4; piece+=2) {
								newBoard = (Board)board.clone();
								newBoard.place(x, y, piece);
								
								int score = -negamax(newBoard, depth-1, -high, -low, 1);
								bestScore = Math.max(bestScore, score);
								low = Math.max(low, score);
								
								if (low >= high) {
									return bestScore;
								}
								
								count ++;
								if (count > 3) {
									return bestScore;
								}
							}
							
						} catch (CloneNotSupportedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		
		if (bestScore == Integer.MIN_VALUE) {
			throw new Exception("Best score is not set for " + (mover == 1 ? "Solver" : "Blocker") + "\n" + board);
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
				
				int score = negamax(newBoard, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
				
				if (score > bestScore) {
					bestScore = score;
					bestMove = move;
				}
			} catch (CloneNotSupportedException e) {
				
			}
		}
		
		if (bestMove == -1) {
			throw new Exception("No move resulted in a postive score for position\n " + board + "\nLegal moves: " + legalMoves + " Best score " + bestScore);
		}
		
		return bestMove;
	}
	
}

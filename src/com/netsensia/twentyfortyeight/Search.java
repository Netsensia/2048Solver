package com.netsensia.twentyfortyeight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Hashtable;

import static java.util.Comparator.comparing;

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
	
	private boolean evaluateBlankSpaces = true;
	
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
	
	public boolean isEvaluateBlankSpaces() {
		return evaluateBlankSpaces;
	}

	public void setEvaluateBlankSpaces(boolean evaluateBlankSpaces) {
		this.evaluateBlankSpaces = evaluateBlankSpaces;
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
	
	public ArrayList<SolverMove> getLegalMoves(Board board) {
		ArrayList<SolverMove> legalMoves = new ArrayList<SolverMove>();
		
		Board newBoard;
		
		for (int i=Board.UP; i<=Board.RIGHT; i++) {
			if (board.isValidMove(i)) {
				
				try {
					newBoard = (Board)board.clone();
					newBoard.makeMove(i, false);
					
					SolverMove solverMove = new SolverMove(i);
					solverMove.setScore(evaluate(newBoard));
					legalMoves.add(solverMove);
					
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				
			}
		}

		Comparator<SolverMove> moveComp = (SolverMove m1, SolverMove m2) -> (int)(m1.getScore() > m2.getScore() ? -1 : 1);
		Collections.sort(legalMoves, moveComp);

		return legalMoves;
	}
	
	public SolverMove getRandomMove(Board board) {
		ArrayList<SolverMove> legalMoves = getLegalMoves(board);
		
		return legalMoves.get(r.nextInt(legalMoves.size()));
	}
	
	public SolverMove getMoveBasedOnImmediateScore(Board board) {
		SolverMove bestMove = new SolverMove(-1);
		int bestScore = -1;
		
		ArrayList<SolverMove> legalMoves = getLegalMoves(board);
		
		for (SolverMove move : legalMoves) {
			int score = score(board, move.getDirection());
			if (score > bestScore) {
				bestScore = score;
				bestMove = move;
			}
		}
		
		return bestMove;
		
	}
	
	public SolverMove getBestMove(Board board) throws Exception {
		
		switch (getMode()) {
			case Search.RANDOM: return getRandomMove(board);
			case Search.SCORE: return getMoveBasedOnImmediateScore(board);
			case Search.SEARCH: return getMoveFromSearch(board);
			default:
				return getRandomMove(board);
		}
	}
	
	public int evaluate(Board board) {
		int score = board.getScore();
		double weight = 1.0;

		for (int x=0; x<Board.COLS; x++) {
			
			for (int y=0; y<Board.ROWS; y++) {
				
				weight += 0.1;
				double thisWeight = weight;
				
				int piece = board.getSquare(x, y);

				if (y > 0 && board.getSquare(x, y-1) > piece) {
					thisWeight *= 0.9;
				}
				
				if (x > 0 && board.getSquare(x-1, y) > piece) {
					thisWeight *= 0.9;
				}

				score += (int)(piece * thisWeight);

			}
		}
		
		return score; 
	   		
	}
	
	public int negamax(Board board, final int depth, int low, int high, int mover, StringBuilder moveString) throws Exception {
		
		StringBuilder underPath = new StringBuilder();
		
		ArrayList<SolverMove> legalMoves = getLegalMoves(board);
		
		if (depth == 0) {
			return mover * evaluate(board);
		}

		int bestScore = Integer.MIN_VALUE;
		
		if (mover == 1) {
			
			if (legalMoves.size() == 0) {
				return mover * evaluate(board);
			}
			
			for (SolverMove move : legalMoves) {
				Board newBoard;
				try {
					newBoard = (Board)board.clone();
					newBoard.makeMove(move.getDirection(), true);
					
					int score = -negamax(newBoard, depth-1, -high, -low, -1, underPath);
					
					if (score > bestScore) {
						bestScore = score;
						moveString.replace(0,  moveString.length(), "");
						String englishMove;
						switch (move.getDirection()) {
							case Board.UP: englishMove = "Up"; break;
							case Board.DOWN: englishMove = "Down"; break;
							case Board.LEFT: englishMove = "Left"; break;
							case Board.RIGHT: englishMove = "Right"; break;
							default: englishMove = "ERROR!";
						}
						moveString.append("Slide " + englishMove + " for a score of " + score);
						moveString.append(System.getProperty("line.separator"));
						moveString.append(newBoard);
						moveString.append("=================================================");
						moveString.append(System.getProperty("line.separator"));
						moveString.append(underPath);

					}
					
					low = Math.max(low, score);
					
					if (low >= high) {
						return bestScore;
					}
					
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
		} else {
			
			int count = 0;
			for (int x=0; x<Board.COLS; x++) {
				for (int y=0; y<Board.ROWS; y++) {
					if (board.getSquare(x, y) == 0) {
						Board newBoard;
						try {
							for (int piece=2; piece<=4; piece+=2) {
								
								count ++;
								
								newBoard = (Board)board.clone();
								newBoard.place(x, y, piece);
								
								int score = -negamax(newBoard, depth-1, -high, -low, 1, underPath);
								
								if (score > bestScore) {
									bestScore = score;
									moveString.replace(0,  moveString.length(), "");
									moveString.append(System.getProperty("line.separator"));
									moveString.append("Place a " + piece + " at " + x + "," + y + " for a score of " + score);
									moveString.append(System.getProperty("line.separator"));
									moveString.append(newBoard);
									moveString.append("=================================================");
									moveString.append(System.getProperty("line.separator"));
									moveString.append(underPath);

								}
								
								low = Math.max(low, score);
								
								if (low >= high) {
									return bestScore;
								}

							}
							
						} catch (CloneNotSupportedException e) {
							e.printStackTrace();
						}
					}
				}
			}
			
			if (count == 0) {
				return mover * evaluate(board);
			}
		}
		
		if (bestScore == Integer.MIN_VALUE) {
			throw new Exception("Best score is not set for " + (mover == 1 ? "Solver" : "Blocker") + "\n" + board);
		}
		
		return bestScore;
		
	}
	
	public SolverMove getMoveFromSearch(Board board) throws Exception {

		ArrayList<SolverMove> legalMoves = getLegalMoves(board);
		
		if (legalMoves.size() == 0) {
			throw new Exception("No legal moves for position\n " + board);
		}
		
		StringBuilder moves = new StringBuilder();
		
		negamax(board, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, 1, moves);
		
		switch (moves.charAt(6)) {
		case 'U' : return new SolverMove(Board.UP);
		case 'D' : return new SolverMove(Board.DOWN);
		case 'L' : return new SolverMove(Board.LEFT);
		case 'R' : return new SolverMove(Board.RIGHT);
		default:
			throw new Exception("Unknown move in " + moves);
		}
		
	}
	
}

package com.netsensia.twentyfortyeight;

import java.util.Arrays;
import java.util.Random;

public class Search {
	
	public static final int SEARCH_RANDOM_MOVES_TO_PLAY = 7;
	
	public static final double EVALUATION_LOST_GAME_MULT = 0.2;
	public static final double EVALUATION_WEIGHT_ORDERED = 1.25;
	public static final double EVALUATION_TOUCHER_WEIGHT = 4.5;
	public static final double EVALUATION_EMPTY_SQUARE_WEIGHT = 0.0001;

	public static final double EVALUATION_CLOSE_WEIGHTS[] = {
		1.35, 
		1.15, 
		1.05, 
		1, 
		1, 
		1, 
		1, 
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
	
	Random r = new Random();
	
	int depth = 1;
	
	private final int MAX_TILE_VALUE = 32768;
	
	private int pushRightBonusLookup[] = new int[Board.COLS];
	private int log2Lookup[] = new int[MAX_TILE_VALUE+1];
	
	public Search() {
		for (int x=0; x<Board.COLS; x++) {
			pushRightBonusLookup[x] = (x+1) * (x+1);
		}
		
		for (int i=2; i<=MAX_TILE_VALUE; i++) {
			log2Lookup[i] = (int)(Math.log(i) / Math.log(2));
		}
		
		/************************** 
		 * log2Lookup[2048] = 11
		 * log2Lookup[4096] = 12
		 **************************/
	}
	
	public int getDepth() {
		return depth;
	}
	
	public void setDepth(int depth) {
		this.depth = depth;
	}

	public int score(Board board, int move) {
		int bestScore = 0;
		
		Board newBoard = new Board(board.getBoard(), board.getScore());
		
		newBoard.makeMove(move);
		int score = newBoard.getScore();
		if (score > bestScore) {
			bestScore = score;
		}
		
		return bestScore;
	}
	
	public int[] getOrderedBlockerMoves(Board board) {
		
		int moves[] = new int[board.countEmptySquares() * 2];

		int count = 0;
		int maxBonus = (Board.COLS * 2) - 2;
		int bonus, y;
		
		for (int x=0; x<Board.COLS; x++) {
			
			bonus = maxBonus - (int)(x + (Math.random() * x));
			
			for (y=0; y<Board.ROWS; y++) {
				if (board.getSquare(x, y) == 0) {
					
					moves[count++] = y * Board.COLS + x + 2000 + (bonus * 10000);
					moves[count++] = y * Board.COLS + x + 4000 + (bonus * 10000);
					
				}
			}
		}

		Arrays.sort(moves);
		
		return moves;
		
	}
	
	public final int evaluate(Board board) {
		
		int score = board.getScore() + pieceBonuses(board);
		
		int rowTouchers = getRowTouchers(board);
		int columnTouchers = getColumnTouchers(board);
		
		// Bonus for having tiles of the same value next to each other
		score += (Math.max(rowTouchers, columnTouchers)) * EVALUATION_TOUCHER_WEIGHT;
		
		if (rowTouchers + columnTouchers == 0 && board.isFull()) {
			// Game over
			score *= EVALUATION_LOST_GAME_MULT;
		}

		return score; 
	   		
	}

	private final int pieceBonuses(Board board) {

		int bonus = 0;
		
		for (int x=0; x<Board.COLS; x++) {
			
			for (int y=0; y<Board.ROWS; y++) {
				
				bonus += pieceBonus(board, x, y);
			}
		}
		
		return bonus;
	}

	private final int pieceBonus(Board board, int x, int y) {
		
		int piece = board.getSquare(x, y);
		
		if (piece == 0) {
			return 0;
		}
		
		// Get large numbers to the right-hand side
		double weight = pushRightBonusLookup[x];
		
		boolean ordered = true;
		
		// Are all pieces below this one of a higher value?
		for (int i=y+1; i<Board.ROWS; i++) {
			if (piece > board.getSquare(x, i)) {
				ordered = false;
				break;
			}
		}

		if (ordered) {
			weight *= EVALUATION_WEIGHT_ORDERED;
		}
		
		if (x < Board.COLS - 1) {
			int neighbourPiece = board.getSquare(x + 1, y);
	
			if (neighbourPiece != 0) {
				weight *= EVALUATION_CLOSE_WEIGHTS[Math.abs(log2Lookup[neighbourPiece] - log2Lookup[piece])];
			}
		}
		
		return (int)(piece * weight);
	}
	
	private final int getRowTouchers(Board board) {
		int lastPiece;
		int rowTouchers = 0;
		
		for (int y=0; y<Board.ROWS; y++) {
			
			lastPiece = board.getSquare(0,y);
			
			for (int x=1; x<Board.COLS; x++) {
				
				int piece = board.getSquare(x,y);
				
				if (piece > 0) {
					if (piece == lastPiece) {
						rowTouchers += piece;
						lastPiece = 0;
					} else {
						lastPiece = piece;
					}
				}
			}
		}
		return rowTouchers;
	}
	
	private final int getColumnTouchers(Board board) {
		int lastPiece;
		int columnTouchers = 0;
		
		for (int x=0; x<Board.COLS; x++) {
			
			lastPiece = board.getSquare(x,0);
			
			for (int y=1; y<Board.ROWS; y++) {
				
				int piece = board.getSquare(x,y);
				
				if (piece > 0) {
					if (piece == lastPiece) {
						columnTouchers += piece;
						lastPiece = 0;
					} else {
						lastPiece = piece;
					}
				}
			}
		}
		
		return columnTouchers;
	}
	
	public int negamax(Board board, final int depth, int low, int high, int mover, StringBuilder returnMove) throws Exception {
		
		if (depth == 0) {
			return mover * evaluate(board);
		}

		int bestScore = Integer.MIN_VALUE;
		
		if (mover == 1) {

			Board newBoard;
			
			int count = 0;

			for (int i=0; i<4; i++) {
				
				if (board.isValidMove(i)) {
					count ++;
					
					newBoard = new Board(board.getBoard(), board.getScore());
					newBoard.makeMove(i);
					
					int score = -negamax(newBoard, depth-1, -high, -low, -1, null);
					
					if (score > bestScore) {
						bestScore = score;
						if (returnMove != null) {
							returnMove.setLength(0);
							returnMove.append(i);
						}
					}
					
					if (score > low) {
						low = score;
					}
					
					if (low >= high) {
						return bestScore;
					}
				}
			}
			if (count == 0) {
				return mover * evaluate(board);
			}
		} else {
			
			int[] legalMoves = getOrderedBlockerMoves(board);
			
			if (legalMoves.length == 0) {
				return mover * evaluate(board);
			}

			int count = 0;
			int totalScore = 0;

			Board newBoard;
			
			for (int move : legalMoves) {
			
				count ++;
				newBoard = new Board(board.getBoard(), board.getScore());
				newBoard.place(move % 1000, move % 10000 / 1000);
				
				totalScore += -negamax(newBoard, depth-1, -high, -low, 1, null);
				
				if (count == SEARCH_RANDOM_MOVES_TO_PLAY) {
					break;
				}
			}
			
			return (int)(totalScore / count);
		}
		
		return bestScore;
		
	}
	
	public int[] getSolverMoves(Board board) {
		
		int[] moves = {-1,-1,-1,-1};
		int count = 0;
		
		for (int i=0; i<4; i++) {
			if (board.isValidMove(i)) {
				moves[count++] = i;
			}
		}
		
		return moves;
	}
	
	public int searchForMove(Board board) throws Exception {

		int[] legalMoves = getSolverMoves(board);
		
		if (legalMoves[0] == -1) {
			throw new Exception("No legal moves for position\n " + board);
		} else {
			if (legalMoves[1] == -1) {
				return legalMoves[0];
			}
		}
		
		StringBuilder move = new StringBuilder();
		
		negamax(board, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, 1, move);
		
		return Integer.parseInt(move.toString());
		
	}
	
}

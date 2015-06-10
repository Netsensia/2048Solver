package com.netsensia.twentyfortyeight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class Search {
	
	public static final int RANDOM_MOVES_TO_PLAY = 4;
	
	public static final int RANDOM = 0;
	public static final int SCORE = 1;
	public static final int SEARCH = 2;
	
	private boolean evaluateBlankSpaces = true;
	
	Random r = new Random();
	
	int depth = 1;

	int mode = RANDOM;
	
	private final int MAX_TILE_VALUE = 16384;
	
	private int squareLookup[] = new int[Board.COLS];
	private int log2Lookup[] = new int[MAX_TILE_VALUE+1];
	
	public Search() {
		for (int x=0; x<Board.COLS; x++) {
			squareLookup[x] = (x+1) * (x+1);
		}
		
		for (int i=2; i<=MAX_TILE_VALUE; i++) {
			log2Lookup[i] = (int)(Math.log(i) / Math.log(2));
		}
		
		/************************** 
		 * log2Lookup[2048] = 11
		 * log2Lookup[4096] = 12
		 **************************/
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

	public int score(Board board, int move) {
		int bestScore = 0;
		
		Board newBoard = new Board(board.getBoard(), board.getScore());
		
		newBoard.makeMove(move, true);
		int score = newBoard.getScore();
		if (score > bestScore) {
			bestScore = score;
		}
		
		return bestScore;
	}
	
	private void addBlockerMove(ArrayList<BlockerMove> blockerMoves, Board board, int x, int y, int piece) {
		
		BlockerMove blockerMove = new BlockerMove(x, y, piece);
		
		// Slightly prefer moves further to the right of the board 
		blockerMove.score = (x + (Math.random() * x));
		blockerMoves.add(blockerMove);	
	}
	
	public ArrayList<BlockerMove> getOrderedBlockerMoves(Board board) {
		
		ArrayList<BlockerMove> blockerMoves = new ArrayList<BlockerMove>();
		
		for (int x=0; x<Board.COLS; x++) {
			for (int y=0; y<Board.ROWS; y++) {
				if (board.getSquare(x, y) == 0) {
					addBlockerMove(blockerMoves, board, x, y, 2);
					addBlockerMove(blockerMoves, board, x, y, 4);
				}
			}
		}
		
		Comparator<BlockerMove> moveComp = (BlockerMove m1, BlockerMove m2) -> (int)(m1.score > m2.score ? -1 : 1);
		Collections.sort(blockerMoves, moveComp);

		return blockerMoves;
	}

	public int evaluate(Board board) {
		int score = board.getScore();
		double weight = 0.0;

		for (int x=0; x<Board.COLS; x++) {
			
			for (int y=0; y<Board.ROWS; y++) {
				
				int piece = board.getSquare(x, y);
				
				if (piece > 0) {
					
					// Get large numbers to the right-hand side
					weight = squareLookup[x];
					
					boolean ordered = true;
					
					// Are all pieces below this one of a higher value?
					for (int i=y+1; i<Board.ROWS; i++) {
						int neighbourPiece = board.getSquare(x, i);
						
						if (piece > neighbourPiece) {
							ordered = false;
							break;
						}
						
					}
					
					boolean closeValues = true;
					
					if (x < Board.COLS - 1) {
						int neighbourPiece = board.getSquare(x + 1, y);
				
						if (neighbourPiece > 0) {
	
							if (Math.abs(log2Lookup[piece] - log2Lookup[neighbourPiece]) > 1) {
								closeValues = false;
							}
						}
					}
					
					if (ordered) {
						weight *= 1.25;
					}
					
					if (closeValues) {
						weight *= 1.25;
					}
					
					score += (int)(piece * weight);
				}
			}
		}
		
		int lastPiece;
		int rowTouchers = 0;
		int columnTouchers = 0;
		
		for (int x=0; x<Board.COLS; x++) {
			lastPiece = 0;
			for (int y=0; y<Board.ROWS; y++) {
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
		
		for (int y=0; y<Board.ROWS; y++) {
			lastPiece = 0;
			for (int x=0; x<Board.COLS; x++) {
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
		
		// Bonus for having tiles of the same value next to each other
		score += (Math.max(rowTouchers,  columnTouchers));
		
		if (rowTouchers + columnTouchers == 0 && board.isFull()) {
			// Game over
			score *= 0.8;
		}
		
		return score; 
	   		
	}
	
	public int negamax(Board board, final int depth, int low, int high, int mover, StringBuffer returnMove) throws Exception {
		
		if (depth == 0) {
			return mover * evaluate(board);
		}

		int bestScore = Integer.MIN_VALUE;
		
		if (mover == 1) {

			Board newBoard;
			
			int count = 0;

			for (int i=Board.UP; i<=Board.LEFT; i++) {
				
				if (board.isValidMoveFast(i)) {
					count ++;
					
					newBoard = new Board(board.getBoard(), board.getScore());
					newBoard.makeMove(i, true);
					
					int score = -negamax(newBoard, depth-1, -high, -low, -1, null);
					
					if (score > bestScore) {
						bestScore = score;
						if (returnMove != null) {
							returnMove.setLength(0);
							returnMove.append(i);
						}
					}
					
					low = Math.max(low, score);
					
					if (low >= high) {
						return bestScore;
					}
				}
			}
			if (count == 0) {
				return mover * evaluate(board);
			}
		} else {
			
			ArrayList<BlockerMove> legalMoves = getOrderedBlockerMoves(board);
			
			if (legalMoves.size() == 0) {
				return mover * evaluate(board);
			}

			int count = 0;
			int totalScore = 0;

			Board newBoard;
			
			for (BlockerMove move : legalMoves) {
			
				count ++;
				newBoard = new Board(board.getBoard(), board.getScore());
				newBoard.place(move.x, move.y, move.piece);
				
				totalScore += -negamax(newBoard, depth-1, -high, -low, 1, null);
				
				if (count == (RANDOM_MOVES_TO_PLAY + 1)) {
					return (int)(totalScore / (RANDOM_MOVES_TO_PLAY + 1));
				}
			}
			
			return (int)(totalScore / count);
		}
		
		return bestScore;
		
	}
	
	public int[] getSolverMoves(Board board) {
		
		int[] moves = {-1,-1,-1,-1};
		int count = 0;
		
		for (int i=Board.UP; i<=Board.LEFT; i++) {
			if (board.isValidMove(i)) {
				moves[count++] = i;
			}
		}
		
		return moves;
	}
	
	public int getMoveFromSearch(Board board) throws Exception {

		int[] legalMoves = getSolverMoves(board);
		
		if (legalMoves[0] == -1) {
			throw new Exception("No legal moves for position\n " + board);
		} else
			if (legalMoves[1] == -1) {
				return legalMoves[0];
			}
		
		StringBuffer move = new StringBuffer();
		
		negamax(board, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, 1, move);
		
		return Integer.parseInt(move.toString());
		
	}
	
}

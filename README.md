# 2048 Solver

Java code that plays the infuriating and addictive game 2048 at a high level. 2s and 4s are placed randomly on the board, after which it is the player's turn to move. They are able to move left, down, up or right. 

The examples below will allow you to figure out exactly what happens, but, in a nutshell, all the tiles slide in the direction chosen and identical numbers merge to become a number twice as large.

Numbers at the far end of the direction of swipe are merged first, so, for example, a left swipe in a row of 2,2,2,0 will result in the row becoming 4,2,0,0 - once the 4 is made, the other 2 doesn't get in on the act.

Two constants ROWS and COLS define the size of the grid.

Currently just has code to set up and manipulate the board for making moves. I am in the process of implementing the game search to allow the program to play with itself.

	Board board = new Board();
			
	int[] testBoard = {
			2,4,4,8,
			0,2,8,8,
			2,2,2,8,
			0,8,8,4,
	};
	
	board.setBoard(testBoard);
	System.out.println(board);
    ----------------------------
    |     2|     4|     4|     8|
    ----------------------------
    |      |     2|     8|     8|
    ----------------------------
    |     2|     2|     2|     8|
    ----------------------------
    |      |     8|     8|     4|
    ----------------------------
		
	board.slide(Board.DOWN);
	System.out.println(board);
  	----------------------------
    |      |      |     4|      |
    ----------------------------
    |      |     4|     8|     8|
    ----------------------------
    |      |     4|     2|    16|
    ----------------------------
    |     4|     8|     8|     4|
    ----------------------------
		
	board.slide(Board.LEFT);
	System.out.println(board);
    ----------------------------
    |     4|      |      |      |
    ----------------------------
    |     4|    16|      |      |
    ----------------------------
    |     4|     2|    16|      |
    ----------------------------
    |     4|    16|     4|      |
    ----------------------------
  
	board.slide(Board.RIGHT);
	System.out.println(board);
    ----------------------------
    |      |      |      |     4|
    ----------------------------
    |      |      |     4|    16|
    ----------------------------
    |      |     4|     2|    16|
    ----------------------------
    |      |     4|    16|     4|
    ----------------------------	
  
	board.slide(Board.UP);
	System.out.println(board);
    ----------------------------
    |      |     8|     4|     4|
    ----------------------------
    |      |      |     2|    32|
    ----------------------------
    |      |      |    16|     4|
    ----------------------------
    |      |      |      |      |
    ----------------------------

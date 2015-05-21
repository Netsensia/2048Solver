# 2048 Solver

Java code that plays the infuriating and addictive game [2048](http://gabrielecirulli.github.io/2048/). 2s and 4s are placed randomly on the board, after which it is the player's turn to move. They are able to move left, down, up or right. 

The examples below will allow you to figure out exactly what happens, but, in a nutshell, all the tiles slide in the direction chosen and identical numbers merge to become a number twice as large.

Numbers at the far end of the direction of swipe are merged first, so, for example, a left swipe in a row of 2,2,2,0 will result in the row becoming 4,2,0,0 - once the 4 is made, the other 2 doesn't get in on the act.

The aim is to merge two 1024 tiles into a 2048 tile. Point scoring is simple, each time a tile is created from a merger, the value of the new tile is added to the score, so merging two 2s will give earn four points.

Two constants ROWS and COLS define the size of the grid.

There is code to set up and manipulate the board for making moves. I am in the process of implementing the game search to allow the program to play out and solve the game in as fast a time as possible using an alpha-beta search, such as that used in my [Rival Chess Engine](https://github.com/Netsensia/rival-chess-android-engine).

There are several methods of play. First, random moves, resulting in an average score after 50,000 runs of 889 and resulting in positions such as:

	----------------------------
	|     4|     2|     4|    16|
	----------------------------
	|    16|     8|     2|     4|
	----------------------------
	|     4|    64|    32|     8|
	----------------------------
	|     2|     8|     2|     4|
	----------------------------
	Score: 492, Game over: true

The next method is to select the best-scoring move each time. This results in an average score of 2,721. An example end position of this strategy:

	----------------------------
	|    16|     8|     4|    16|
	----------------------------
	|   256|    64|    32|     8|
	----------------------------
	|    64|    32|    16|     2|
	----------------------------
	|    32|    16|     8|     4|
	----------------------------
	Score: 2700, Game over: true

The first variant of a search method is create a move tree but without the random placement of new tiles. In other words, to allow the player to make multiple moves in a row. So, you may find that UP scores worse than DOWN when looking at the immediate score, but that UP can lead to a much better score than DOWN if the player is allowed to make multiple moves.

This leads to an average score of 5,635, and positions such as:

	----------------------------
	|     2|     4|     2|     4|
	----------------------------
	|   512|     8|   128|     2|
	----------------------------
	|     2|    16|    64|     8|
	----------------------------
	|     8|     4|     2|     4|
	----------------------------
	Score: 4772, Game over: true

and even some positions like this:

	----------------------------
	|     4|     2|     8|     4|
	----------------------------
	|    64|     8|    32|     2|
	----------------------------
	|     2|   128|  1024|     4|
	----------------------------
	|     8|     4|    16|     2|
	----------------------------
	Score: 9672, Game over: true

The last position is better than anything I have managed with my own wits, although to be fair, I've not got the luxury of being able to play 50,000 games in a few seconds.

It turns out that allowing two moves in a row is just as effective as allowing any number of moves higher than two. So three, four or five moves, for example, make no difference to the average score. This may allow us to add a sneaky extra ply to the search for minimal cost during a full search.

This is getting us somewhere! Now, we introduce the random placement of the piece into the search. 

The search routine considers each of the four possible moves (up, down, left, right) at each ply of the search tree as well as all possible responses by the tile-placing player.  Three  random placements are considered and the average of the scores available by searching the new position is taken to be the value of the initial move (up, down, left or right).

The following table shows the results for various searches. The number of runs is rather low at the moment because the search is brute force with no optimisations such as hash tables or pruning. Such things should speed up the search significantly. 

In order to speed things up without, hopefully, impacting the results too much, the search routine always uses a depth of one unless the board is more than half full, in which case it uses the depth specified in the table.

	--------------------------------------------------------------------------
	| Search | Total | Average  | Average | Highest | Highest | 2048s | 1024s |
	| Depth  | Runs  | Time(ms) | Score   | Score   | Tile    |       |       |
	--------------------------------------------------------------------------
	       1   1000        27      6,633    15,500     1024       0      216
	       2   1000       272      7,690    25,052     2048       4      315
	       3   1000      2638      8,007    24,128     2048       3      369
	--------------------------------------------------------------------------
	
At this point, it appears that we are not gaining much from a deeper search, but the number of 1024s is going up so we'll try one level deeper, but I need to find a way to speed it up if we're going to go much deeper.

I changed to a negamax algorithm to allow easy coding of some tree reduction using alpha-beta pruning.

## Class Interfaces

The key Board interface methods are:

* boolean isValidMove(Board.DOWN)
* void makeMove(Board.DOWN)
* boolean isGameOver()
* void placeRandomPiece()
* void setRandomStartPosition()

The key Search interface methods are: 

* void setDepth(int)
* void setMode(int)
* getBestMove(Board);

## Example code:

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


	
	
	
	

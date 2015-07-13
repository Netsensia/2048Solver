# 2048 Solver

Java code that plays the addictive game [2048](http://gabrielecirulli.github.io/2048/). The only way to get over the addiction to this game is to write a program that plays it for you.

With a move time of around 5 milliseconds, running single-threaded on a fast machine, the program will win the game (get a 2048 tile) about 98 percent of the time.

The highest score the program has achieved so far was 252,016 on a depth 7 search. 

The test results shown here are run using multiple parallel threads. This results in slower average move times per thread (thread duration / thread moves) but faster overall move times when considering all threads (program duration / moves across all threads). 

## Current program performance

![Program Results]
(https://www.dropbox.com/s/ltxyc61ye6y3h2e/Screenshot%202015-07-13%2019.19.26.png?dl=1)

## Using the classes

Running the program from the command line (by executing com.netsensia.twentyfortyeight.TwentyFortyEight) will run a test suite resulting in CSV output that maps to the
data shown in the spreadsheet extract above.

If you wish to use the classes directly in order to create an interactive game-player or just to view a game in progress, here are some useful interface methods.

	Board board = new Board();
	board.setRandomStartPosition();
		
	Search search = new Search();
	search.setDepth(3);
	
	int solverMove = search.searchForMove(board);
	
solverMove will be one of Board.UP, Board.RIGHT, Board.DOWN, Board.LEFT
	
	// To see if a move is valid
	board.isValidMove(solverMove); 
	
	// Make a move on the board and update the score
	board.makeMove(solverMove);
	
	// Place a 2 or a 4 on a random blank square
	board.placeRandomPiece(); 
	
	// Place an "8" tile at location 1,2 (the top left corner is 0,0, the tile at the top of the second column is 1,0, etc...)
	board.place(1,2,8); 
	
	// Get the tile at a location
	board.getSquare(1,2);
	
	// Show the board
	board.toString();
	
This will print something like:

	----------------------------
	|     2|      |    16|     4|
	----------------------------
	|      |     8|    64|     8|
	----------------------------
	|     2|    32|   128|     2|
	----------------------------
	|     4|    16|   512|  4096|
	----------------------------

		
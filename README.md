# 2048 Solver

Java code that plays the addictive game [2048](http://gabrielecirulli.github.io/2048/). 2s and 4s are placed randomly on the board and after each placement it is the player's turn to move. Available moves are left, down, up or right. 

Current stats:

	 ----------------------------------------------------------------------------------
                                                           | % games where tile seen   | 
	 ----------------------------------------------------------------------------------
	| Search | Total | Average | Average | Average | High  | 8192 | 4096 | 2048 | 1024 |
	| Depth  | Runs  | Move    | Game    | Score   | Score |      |      |      |      |
	                   Time    | Time    |         |       |      |      |      |      |
	 ----------------------------------------------------------------------------------
	     3     1000     0.28ms     281ms   27,728   115,248|  0.10| 13.40| 66.20| 94.90
	     5     1000     4.68ms   10987ms   45,314   126,428|  1.70| 45.90| 92.70| 99.30
	 ----------------------------------------------------------------------------------
	 Highest score of all time:         127,048
	 Highest tile seen across all runs: 8192
	 ----------------------------------------------------------------------------------
	     
The examples below will allow you to figure out exactly what happens, but, in a nutshell, all the tiles slide in the direction chosen and identical numbers merge to become a number twice as large.

Numbers at the far end of the direction of swipe are merged first, so, for example, a left swipe in a row of 2,2,2,0 will result in the row becoming 4,2,0,0 - once the 4 is made, the other 2 doesn't get in on the act. Merged cells cannot merge again on the same move.

The aim is to merge two 1024 tiles into a 2048 tile. Point scoring is simple, each time a tile is created from a merger, the value of the new tile is added to the score, so merging two 2s will give earn four points.

Two constants ROWS and COLS define the size of the grid.

This README was written while building the program so roughly documents the process of finding better ways to search for a solution. 

First, random moves, resulting in an average score after 50,000 runs of 889 and resulting in positions such as:

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

Select the best-scoring move each time. This results in an average score of 2,721. An example end position of this strategy:

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

The first variant of a search method is create a move tree but without the random placement of new tiles. In other words, to allow the player to make multiple moves in a row. So, you may find that UP scores worse than DOWN when looking at the immediate score, but that UP can lead to a much better score than DOWN if the player is allowed to make multiple moves without the blocker
having a say.

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

and this:

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

It turns out that allowing two moves in a row is just as effective as allowing any number of moves higher than two. So three, four or five moves, for example, make no difference to the average score. This may allow us to add a sneaky extra ply to the search for minimal cost during a full search.

This is getting us somewhere! Now, we introduce the random placement of the piece into the search. 

Initially the search routine considered each of the four possible moves (up, down, left, right) at each ply of the search tree as well as all possible responses by the tile-placing player.  Three  random placements are considered and the average of the scores available by searching the new position is taken to be the value of the initial move (up, down, left or right).

The following table shows the results for various searches using this method. The number of runs is rather low at the moment because the search is brute force with no optimisations such as hash tables or pruning. Such things should speed up the search significantly. Each ply in the search tree represents a move for the solver plus a move for the blocker.

In order to speed things up without, hopefully, impacting the results too much, the search routine always uses a depth of one unless the board is more than half full, in which case it uses the depth specified in the table.

	--------------------------------------------------------------------------
	| Search | Total | Average  | Average | Highest | Highest | 2048s | 1024s |
	| Depth  | Runs  | Time(ms) | Score   | Score   | Tile    |       |       |
	--------------------------------------------------------------------------
	       1   1000        27      6,633    15,500     1024       0      216
	       2   1000       272      7,690    25,052     2048       4      315
	       3   1000      2638      8,007    24,128     2048       3      369
	--------------------------------------------------------------------------
	
At this point, I could see the need to use to change to a negamax algorithm to allow easy coding of some tree reduction using alpha-beta pruning. Using this method, each ply in the following table represents either a  move by the solver or one move by the blocker. A search depth of 4, therefore is the equivalent of a depth of 2 in the previous table.

You can see that the alpha beta pruning has allowed us to search deeper in a faster time than was possible with the previous search routine.

	--------------------------------------------------------------------------
	| Search | Total | Average  | Average | Highest | Highest | 2048s | 1024s |
	| Depth  | Runs  | Time(ms) | Score   | Score   | Tile    |       |       |
	--------------------------------------------------------------------------
	     9     1000     10770      8614      25676     2048       8      426
	--------------------------------------------------------------------------

Up until this point, the evaluation function had just been using the board score.

I then changed to an evaluation function that simply adds up the values of all the tiles, but each tile is multiplied by its row, so, for example, a 256 on the bottom row will count as 1024. This causes the search to follow the strategy I use to solve the game, which is to get all the high numbers on one side of the square, avoiding making any swipes in the opposite direction if at all possible. It ignores the game score completely.

This made impressive improvements even at low depths. The number of games where a 2048 tile is achieved is a new record, and it's taking less than a second to play each game.

	--------------------------------------------------------------------------
	| Search | Total | Average  | Average | Highest | Highest | 2048s | 1024s |
	| Depth  | Runs  | Time(ms) | Score   | Score   | Tile    |       |       |
	--------------------------------------------------------------------------
	     5     1000      698       7,742     30,136     2048      17      308
	--------------------------------------------------------------------------
	
I tweaked this to give each square on the board its own weighting, always favouring squares in columns further to the right, and within each column favouring squares towards the bottom, e.g.:

     ----------------------------
    |     1|     5|     9|    13|
    ----------------------------
    |     2|     6|    10|    14|
    ----------------------------
    |     3|     7|    11|    15|
    ----------------------------
    |     4|     8|    12|    16|
    ----------------------------
    
The evaluation function then just multiples the square value by its weight.

	--------------------------------------------------------------------------
	| Search | Total | Average  | Average | Highest | Highest | 2048s | 1024s |
	| Depth  | Runs  | Time(ms) | Score   | Score   | Tile    |       |       |
	--------------------------------------------------------------------------
	     5     1000      769       8,074     34,528     2048     32      354
	--------------------------------------------------------------------------
	
A decent improvement. I now tried considering all Blocker's moves instead of just randomly selecting 25% of them. This made no noticeable difference.

I think tried tweaking the evaluation function to penalise numbers that are lower than numbers sitting on lower-weighted squares. This made a small difference.

A ridiculously large difference, however, occurred when I finally decided to order the moves in the search tree, assuming that the additional time required to estimate their effectiveness before searching them would be offset by the pruning that would occur in the tree as a result.

I don't know what happened during this change, ordering the moves should have made very little difference to anything other than the number of nodes searched in the tree, but the outcome was indeed hugely improved.  The program even managed its first one of these:

	----------------------------
	|     2|     8|    16|     4|
	----------------------------
	|    16|    32|    64|     8|
	----------------------------
	|     2|     8|   128|     2|
	----------------------------
	|     4|    16|   512|  4096|
	----------------------------
	Score: 47312, Game over: true
	
And at depth 5, it started winning the game more than 25% of the time. The increase in time taken is due to now considering all the possible blocker moves.

	----------------------------------------------------------------------------------
	| Search | Total | Average  | Average | Highest | Highest | 4096s | 2048s | 1024s |
	| Depth  | Runs  | Time(ms) | Score   | Score   | Tile    |       |       |       |
	----------------------------------------------------------------------------------
	     5     1000      1475      14,992    49,576     4096       3     248     770
	     7     1000     16448      15,532    49,112     4096       6     273     771
	----------------------------------------------------------------------------------
	
A few more tweaks, particularly to the evaluation function. In this test I am only considering four random moves for the blocker at each ply.

I've had to add a new column to this one.

	------------------------------------------------------------------------------------------
	| Search | Total | Average  | Average | Highest | Highest | 8192s | 4096s | 2048s | 1024s |
	| Depth  | Runs  | Time(ms) | Score   | Score   | Tile    |       |       |       |       |
	------------------------------------------------------------------------------------------
	     7     1000      3389     20,175    107,844    8192       1       35      467    864
	------------------------------------------------------------------------------------------
	
Some further tweaks to the evaluation function to consider tiles of the same value next to each other and to reduce the score if there are no moves left.

	------------------------------------------------------------------------------------------
	| Search | Total | Average  | Average | Highest | Highest | 8192s | 4096s | 2048s | 1024s |
	| Depth  | Runs  | Time(ms) | Score   | Score   | Tile    |       |       |       |       |
	------------------------------------------------------------------------------------------
	     5     1000       687     19,793     68,164    4096       0       34     437     847
	     7     1000      3338     21,948     72,164    4096       0       58     505     902
	------------------------------------------------------------------------------------------

Yet more changes, making steady progress:

	------------------------------------------------------------------------------------------
	| Search | Total | Average  | Average | Highest | Highest | 8192s | 4096s | 2048s | 1024s |
	| Depth  | Runs  | Time(ms) | Score   | Score   | Tile    |       |       |       |       |
	------------------------------------------------------------------------------------------
	     7     1000      3806     24,874    78,008     4096       0      104     589     892
	     
Then, I had the idea of, while still keeping the blocker moves random, adding a slight nudge for those further to the right of the board. At this point I also started to record the average move time as this is more meaningful than the average game time.

	------------------------------------------------------------------------------------------
	| Search | Total | Average  | Average | Highest | Highest | 8192s | 4096s | 2048s | 1024s |
	| Depth  | Runs  | Move ms  | Score   | Score   | Tile    |       |       |       |       |
	------------------------------------------------------------------------------------------
	     7     1000     4.27      30,299    127,048     8192      3      190     698     944
	     

## Example code to show how moves are made in the game of 2048

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

	

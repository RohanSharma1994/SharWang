package aiproj.sharwang;

import java.io.PrintStream;
import aiproj.squatter.*;

/**
 *  SharWang:
 *    A smart AI agent for Squatter. 
 *    It is an implementation of class Player.
 *
 *  Authors:
 *      Rohan Sharma (rsharma1)
 *      Shiyi Wang (sharwang)
 */

public class SharWang implements Player, Piece {
	/* The dimension of the board. */
	private int dim;

	/* The player ID. */
	private int player;

	/* The game board. */
	private Board board;  // Change to private before submit.

	/* The number of pieces on the board. */
	private int numPieces;

    /* The last move made by opponent. */
    private Move lastOpponentMove = new Move();

	/*
	 * Define weights for evaluation function:
	 *  Eval = w1 * captureScore + w2 * sideScore + w3 * potentialScore
	 */
    private double w1, w2, w3;

    /* An array of weights optimized by gradient decent algorithm for round 16 ~ 30. */
	private double[][] weights = new double[][] {
            {0.642656, -0.025575, 0.007332},  // round 16
            {0.739764, -0.035820, -0.004044}, // round 17
            {0.747621, -0.022536, 0.000874},  // round 18
            {0.810477, -0.015397, 0.001077},  // round 19
            {0.821982, -0.003835, -0.004692}, // round 20
            {0.830698, -0.010243, 0.001302},  // round 21
            {0.832525, 0.004337, 0.003385},   // round 22
            {0.842164, -0.001772, 0.001612},  // round 23
            {0.838333, 0.007135, 0.004044},   // round 24
            {0.846195, -0.002701, 0.001826},  // round 25
            {0.839661, 0.007033, 0.003967},   // round 26
            {0.846929, -0.002112, 0.002232},  // round 27
            {0.839524, 0.008952, 0.004949},   // round 28
            {0.848400, -0.002242, 0.001904},  // round 29
            {0.843682, 0.007818, 0.004519}};  // round 30

	/**
	 * Initialize a Player.
	 *
	 * @param n  the dimension of the board.
	 * @param p  the ID of the player
	 * @return   1 if valid, otherwise -1
	 */
	public int init(int n, int p) {
		if(n < 0 || (p != WHITE && p != BLACK))
			return INVALID;
		dim = n;  // Initialize dimension of the board.
		board = new Board(dim);  // Initialize the board.
		player = p;  // Initialize the player
		numPieces = 0;  // Initialize the number of pieces on the board.
        /* Initialize the last move made by opponent. */
        lastOpponentMove.P = EMPTY;
        lastOpponentMove.Row = -5;  // Set a arbitrary invalid move.
        lastOpponentMove.Col = -10;
		return 1;
	}

	/**
	 * Make a smart move by using alpha-beta pruning algorithm.
	 * 
	 * @return   a smart move generated by alpha-beta pruning algorithm
	 */
	public Move makeMove() {
        double[] ws;
        /* Set weights for the corresponding round. */
        if(numPieces < 16)
            ws = weights[0];
        else if(numPieces > 30)
            ws = weights[14];
        else
            ws = weights[numPieces-16];

        /* Strategy:
         *   Place first move on the center of the board, an make sure the opponent's
         *     token is not on the same diagonal line with our token.
         *   For the rest stages, use the weights of round 16 if number of pieces is
         *     less than 16. Use the weights of round 30 if the number of pieces is
         *     greater than 30. Otherwise, use the weights of corresponding stages.
         *
         */
        Move m;
        if(numPieces <= 1)
            m = firstMove(); // First Move
        else {
            w1 = ws[0];
            w2 = ws[1];
            w3 = ws[2];
            if((numPieces <= 10 && dim == 6) || (numPieces <= dim*dim/2 && dim == 7))
                m = smartMove(3);
            else
                m = smartMove(4);
        }
		board.makeMove(m);  // Update the board.
        numPieces++;
		return m;
	}

	/**
	 * Update the move of opponent.
	 *
	 * @param m  the move of opponent
	 * @return   -1 if the move is invalid, otherwise 0
	 */
	public int opponentMove(Move m) {
		/* Return 0 if the move is not made by opponent. */
		if(m.P == this.player)
			return 0;
		/* Return -1 if the move is invalid. */
		if(!board.makeMove(m))
			return -1;
		/* Otherwise. */
        lastOpponentMove = m;
        numPieces++;
		return 0;
	}

	/**
	 * Get the winner of the game.
	 *
	 * @return   0 if the game is uncomplated
	 *           1 if the winner is white
	 *           2 if the winner is black
	 *           3 if draw
	 */
	public int getWinner() {
		return board.getWinner();
	}

	/**
	 * Print the board to a PrintStream.
	 *
	 * @param output  PrintStream
	 */
	public void printBoard(PrintStream output) {
		output.format(board.toString());
	}

	/* The first layer of alpha-beta pruning algorithm. 
	   We define this function for getting the best move. */
	private Move smartMove(int limit) {
		/* Define alpha as -Infinity, beta as Infinity. */
		double alpha = -Double.MAX_VALUE, beta = Double.MAX_VALUE;
		/* Define a variable to save the scores of child boards. */
		double childScore;
        /* Define a variable to check whether we found a move not on the corner. */
        boolean found = false;
		/* Define the best move and temp move variables. */
		Move best_move = new Move(), m;
		/* Define the child board variable. */
		Board boardChild;
		/* For each child board. */
		for(int row = 0; row < dim; row++) {
			for(int col = 0; col < dim; col++) {
                if((row==0 && col==0) || (row==0 && col==dim-1) || (row==dim-1 && col==0) || (row==dim-1 && col==dim-1))
                    continue;
				if(board.getCell(row, col).getToken().equals(Board.TOKEN_EMPTY)) {
					/* Let boardChile be the child board. */
					boardChild = new Board(board);
					m = new Move();
					m.P = player;
					m.Row = row;
					m.Col = col;
					boardChild.makeMove(m);
					childScore = alphabeta(boardChild, limit-1, alpha, beta, changePlayer(player));
					/* Choose the maximum of alpha. */
					if(alpha < childScore) {
						alpha = childScore;
						/* Update the best move. */
						best_move.P = player;
						best_move.Row = row;
						best_move.Col = col;
                        found = true;
					}
				}
			}
		}
        if(!found)
            best_move = placeOnCorner();
		return best_move;
	}

	/* Impletation of alpha-beta pruning algorithm. */
	private double alphabeta(Board b, int depth, double alpha, double beta, int player) {
		/* Return the value of evaluation function if reach the terminal node. */
		if(depth == 0 || b.isComplete())
            return getEval(b, w1, w2, w3);
        /* Otherwise, continue search. */
		Move m;
		Board boardChild;
		boolean break2 = false;  // A variable for breaking from the nested for loop.
		/* For each child board. */
		if(player == this.player) {
			/* If the player is the maximizing player. */
			for(int row = 0; row < dim; row++) {
				for(int col = 0; col < dim; col++) {
						/* Let boardChile be the child board. */
						boardChild = new Board(b);
						m = new Move();
						m.P = player;
						m.Row = row;
						m.Col = col;
						boardChild.makeMove(m);
						/* Select maximum alpha. */
						alpha = Math.max(alpha, alphabeta(boardChild, depth-1, alpha, beta, changePlayer(player)));
						if(beta <= alpha) {
							/* beta cut-off. */
							break2 = true;
							break;
						}
				}
				if(break2)
					break;
			}
			return alpha;
		} else {
			/* Otherwise. */
			for(int row = 0; row < dim; row++) {
				for(int col = 0; col < dim; col++) {
						/* Let boardChile be the child board. */
						boardChild = new Board(b);
						m = new Move();
						m.P = player;
						m.Row = row;
						m.Col = col;
						boardChild.makeMove(m);
						/* Select minimum beta. */
						beta = Math.min(beta, alphabeta(boardChild, depth-1, alpha, beta, changePlayer(player)));
						if(beta <= alpha) {
							/* alpha cut-off. */
							break2 = true;
							break;
						}
				}
				if(break2)
					break;
			}
			return beta;
		}
	}

    /* Return a valid move on the corner of the board. */
    private Move placeOnCorner() {
        Move m = new Move();
        m.P = player;
        if (board.getCell(0, 0).getToken().equals(Board.TOKEN_EMPTY)) {
            m.Row = 0; // Left-Up
            m.Col = 0;
        } else if (board.getCell(0, dim - 1).getToken().equals(Board.TOKEN_EMPTY)) {
            m.Row = 0; // Right-Up
            m.Col = dim - 1;
        } else if (board.getCell(dim - 1, 0).getToken().equals(Board.TOKEN_EMPTY)) {
            m.Row = dim - 1;
            m.Col = 0; // Left-Down
        } else {
            m.Row = dim - 1; // Right-Down
            m.Col = dim - 1;
        }
        return m;
    }

    /* Return the first move. */
    private Move firstMove() {
        Move m = new Move();
        m.P = player;
        do {
            m.Row = 2 + (int)(Math.random()*(dim-4));
            m.Col = 2 + (int)(Math.random()*(dim-4));
        } while (numPieces == 1 && (m.Row+m.Col==lastOpponentMove.Row+lastOpponentMove.Col)
                || (m.Row-m.Col==lastOpponentMove.Row-lastOpponentMove.Col));
        return m;
    }

	/* Return the player ID of opponent. */
	private int changePlayer(int player) {
		if(player == WHITE)
			return BLACK;
		else
			return WHITE;
	}

	/*
	 * Get the score of our defined evaluation function.
	 * Then our evaluation function is:
	 *   Eval = w1 * capturedScore + w2 * sideScore + w3 * potentialScore
	 */
	private double getEval(Board b, double w1, double w2, double w3) {
        int sign;
        if (player == WHITE)
            sign = 1;
        else
            sign = -1;
        double eval = 0;
        eval += w1 * (b.getWhiteScore() - b.getBlackScore());
        eval += w2 * (b.getWhiteSideScore() - b.getBlackSideScore());
        eval += w3 * (b.getWhitePScore() - b.getBlackPScore());
        return sign * eval;
    }

}
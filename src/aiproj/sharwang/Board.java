package aiproj.sharwang;

import aiproj.squatter.Move;
import aiproj.squatter.Piece;
import java.util.ArrayList;

/**
 *  Board:
 *    The board of the game.
 *
 *  Authors:
 *      Rohan Sharma (rsharma1)
 *      Shiyi Wang (sharwang)
 */

public class Board implements Piece {
	/* The dimension of the board */
	private int dim;

	/* The capture scores of white and black. */
	private int whiteScore, blackScore;

	/* The numbers of tokens on the sides of board for white and black. */
	private int whiteSideScore, blackSideScore;

	/* We defined potential scores for white and black. */
    private int whitePScore, blackPScore;

	/* The Board. */
	private Cell[][] board;

	/** Player ID of None. */
	public static final int PLAYER_NONE = Piece.EMPTY;

	/** Player ID of White. */
	public static final int PLAYER_WHITE = Piece.WHITE;

	/** Player ID of Black. */
	public static final int PLAYER_BLACK = Piece.BLACK;

	/** Token of empty cell. */
	public static final String TOKEN_EMPTY = "+";

	/** Token of white piece. */
	public static final String TOKEN_WHITE = "W";

	/** Token of black piece. */
	public static final String TOKEN_BLACK = "B";

	/** Token of captured empty cell. */
	public static final String TOKEN_EMPTY_CAPTURED = "-";

	/** Token of captured white piece. */
	public static final String TOKEN_WHITE_CAPTURED = "w";

	/** Token of captured black piece. */
	public static final String TOKEN_BLACK_CAPTURED = "b";

	/** 
	 * Initialize a new Board object. 
	 *
	 * @param dim  dimension of the board
	 */
	public Board(int dim) {
		this.dim = dim;
		board = new Cell[dim][dim];
		initboard();
	}

	/**
	 * Copy a Board object.
	 *
	 * @param b  the board need to copy
	 */
	public Board(Board b) {
		this.dim = b.dim;
		this.whiteScore = b.whiteScore;
		this.blackScore = b.blackScore;
        this.whitePScore = b.whitePScore;
        this.blackPScore = b.blackPScore;
		this.whiteSideScore = b.whiteSideScore;
		this.blackSideScore = b.blackSideScore;
		this.board = new Cell[dim][dim];
		for(int row = 0; row < this.dim; row++) {
			for(int col = 0; col < this.dim; col++) {
				this.board[row][col] = new Cell(b.getCell(row, col));
			}
		}
	}

	/** 
	 * Get capture score of white. 
	 *
	 * @return the capture score of white
	 */
	public int getWhiteScore() {
		return whiteScore;
	}

	/** 
	 * Get capture of black. 
	 *
	 * @return the capture of black
	 */
	public int getBlackScore() {
		return blackScore;
	}

    /**
     * Get potential score of white.
     *
     * @return the potential score of white
     */
    public int getWhitePScore() {
        return whitePScore;
    }

    /**
     * Get potential of black.
     *
     * @return the potential of black
     */
    public int getBlackPScore() {
        return blackPScore;
    }

	/** 
	 * Get side score of white. 
	 *
	 * @return the side score of white
	 */
	public int getWhiteSideScore() {
		return whiteSideScore;
	}

	/** 
	 * Get side score of black. 
	 *
	 * @return the side score of black
	 */
	public int getBlackSideScore() {
		return blackSideScore;
	}

	/**
	 * Get the cell at (row, col).
	 *
	 * @param row  the row of cell 
	 * @param col  the col of cell
	 * @return     the cell with row and column as input
	 */
	public Cell getCell(int row, int col) {
		return board[row][col];
	}

	/** 
	 * Check whether the board is complete. 
	 *
	 * @return     true if the board is complete, otherwise false
	 */
	public boolean isComplete() {
		for(int row = 0; row < dim; row++) {
			for(int col = 0; col < dim; col++) {
				if(board[row][col].getToken().equals(TOKEN_EMPTY))
					return false;
			}
		}
		return true;
	}

	/** 
	 * Visualization of the board in string. 
	 * 
	 * @return      the visualization of the board in string
	 */
	public String toString() {
		String boardString = "";
		for(int row = 0; row < dim; row++) {
			for(int col = 0; col < dim; col++) {
				boardString += board[row][col].getToken() + " ";
			}
			boardString += "\n";
		}
		return boardString;
	}

	/** 
	 * Check whether a move is valid. 
	 *
	 * @param m  a Move object
	 * @return   true if the move is valid, otherwise false
	 */
	public boolean isValid(Move m) {
		return board[m.Row][m.Col].getToken().equals(TOKEN_EMPTY);
	}

	/** 
	 * Get the winner ID. 
	 *
	 * @see      aiproj.squatter.Piece
	 * @return   the status defined in Piece 
	 */
	public int getWinner() {
		if(!isComplete())
			return EMPTY;
		updateScore();
		if(whiteScore > blackScore)
			return WHITE;
		if(whiteScore < blackScore)
			return BLACK;
		return DEAD;
	}

	/** 
	 * Make a move. 
	 * 
	 * @param m  a Move object
	 * @return   true is the move is valid, otherwise false. 
	 */
	public boolean makeMove(Move m) {
		if(!isValid(m))
			return false;
		if(m.P == PLAYER_WHITE) {
			if(board[m.Row][m.Col].getPlayer() == PLAYER_NONE) 
				board[m.Row][m.Col].setToken(TOKEN_WHITE);
			else
				board[m.Row][m.Col].setToken(TOKEN_WHITE_CAPTURED);
		} else if(m.P == PLAYER_BLACK) {
			if(board[m.Row][m.Col].getPlayer() == PLAYER_NONE) 
				board[m.Row][m.Col].setToken(TOKEN_BLACK);
			else
				board[m.Row][m.Col].setToken(TOKEN_BLACK_CAPTURED);
		}
		updateBoard(m);
		updateScore();
		return true;
	}

	/* --------------------- Methods below are private ------------------- */

	/* Initialize a new board. */
	private void initboard() {
		for(int row = 0; row < dim; row++) {
			for(int col = 0; col < dim; col++) {
				board[row][col] = new Cell(row, col, TOKEN_EMPTY, PLAYER_NONE);
			}
		}
	}

	/* Update a board after one move by checking for caputured cells. */
	private void updateBoard(Move m) {
		ArrayList<Vector> visited = new ArrayList<>();
		ArrayList<Vector> unvisited = new ArrayList<>();
		ArrayList<Vector> captured = new ArrayList<>();
		ArrayList<Vector> neighbours = new ArrayList<>();
		int boundaries;
		String token;
		Vector currVec;
		boolean found;

		if(m.P == PLAYER_WHITE)
			token = TOKEN_WHITE;
		else
			token = TOKEN_BLACK;
		addNeighbours(m.Row, m.Col, token, neighbours);
		while(neighbours.size() > 0) {
			unvisited.clear();
			captured.clear();
			found = true;
			unvisited.add(neighbours.remove(0));
			while(unvisited.size() > 0) {
				currVec = unvisited.remove(0);
				if(!isVecInList(currVec, visited)) {
					boundaries = boundaryScore(currVec.Row, currVec.Col, token);
					visited.add(currVec);
					if(boundaries == 4)
						captured.add(currVec);
					else {
						found = false;
					}
					addNeighbours(currVec.Row, currVec.Col, token, unvisited);
				}
			}
			if(found) {
				for(Vector v : captured)
					capture(v.Row, v.Col, m.P);
			}
		}
		updateSelfCapture(token);
	}

	/* Add 4 adjacent cells into a ArrayList. */
	private void addNeighbours(int row, int col, String token, ArrayList<Vector> list) {
		/* Add cell left. */
		if(col > 0) {
			if(!board[row][col-1].getToken().equals(token))
				list.add(new Vector(row, col-1));
		}
		/* Add cell right. */
		if(col < dim - 1) {
			if(!board[row][col+1].getToken().equals(token))
				list.add(new Vector(row, col+1));
		}
		/* Add cell up. */
		if(row > 0) {
			if(!board[row-1][col].getToken().equals(token))
				list.add(new Vector(row-1, col));
		}
		/* Add cell down. */
		if(row < dim - 1) {
			if(!board[row+1][col].getToken().equals(token))
				list.add(new Vector(row+1, col));	
		}
	}

	/* The number of directions exist specific token. */
	private int boundaryScore(int row, int col, String token) {
		int boundaries = 0;
		/* Check left direction. */
		for(int i = 0; i < col; i++) {
			if(board[row][i].getToken().equals(token)) {
				boundaries++;
				break;
			}
		}
		/* Check right direction. */
		for(int i = dim-1; i > col; i--) {
			if(board[row][i].getToken().equals(token)) {
				boundaries++;
				break;
			}
		}
		/* Check up direction. */
		for(int i = 0; i < row; i++) {
			if(board[i][col].getToken().equals(token)) {
				boundaries++;
				break;
			}
		}
		/* Check down direction. */
		for(int i = dim-1; i > row; i--) {
			if (board[i][col].getToken().equals(token)) {
				boundaries++;
				break;
			}
		}
		return boundaries;
	}

	/* Capture a cell. Change tokens and set player of the cell as its owner. */
	private void capture(int row, int col, int player) {
		/* Conver the token to captured token. */
		if(board[row][col].getToken().equals(TOKEN_EMPTY))
			board[row][col].setToken(TOKEN_EMPTY_CAPTURED);
		if(board[row][col].getToken().equals(TOKEN_WHITE))
			board[row][col].setToken(TOKEN_WHITE_CAPTURED);
		if(board[row][col].getToken().equals(TOKEN_BLACK))
			board[row][col].setToken(TOKEN_BLACK_CAPTURED);
		/* Set the player as the owner of the cell. */
		board[row][col].setPlayer(player);
	}

	/* Update scores for the board. */
	private void updateScore() {
		whiteScore = 0;
		blackScore = 0;
        whitePScore = 0;
        blackPScore = 0;
		whiteSideScore = 0;
		blackSideScore = 0;
		for(int row = 0; row < dim; row ++) {
			for(int col = 0; col < dim; col++) {
				if(board[row][col].getPlayer() == PLAYER_WHITE)
					whiteScore += 1;
				if(board[row][col].getPlayer() == PLAYER_BLACK)
					blackScore += 1;
				if(board[row][col].getToken().equals(TOKEN_WHITE)) {
                    whitePScore += calcPotentialScore(row, col, TOKEN_WHITE);
					if(row == 0 || col == 0 || row == dim-1 || col == dim-1)
						whiteSideScore++;
				}
				if(board[row][col].getToken().equals(TOKEN_BLACK)) {
                    blackPScore += calcPotentialScore(row, col, TOKEN_BLACK);
					if(row == 0 || col == 0 || row == dim-1 || col == dim-1)
						blackSideScore++;
				}
			}
		}
	}

    private int calcPotentialScore(int row, int col, String token) {
        int score = 0, num = 1;
        if(row > 0 && col > 0) {
            if(board[row-1][col-1].getToken().equals(token))
                score += num++; // Left-Up
        }
        if(row > 0 && col < dim-1) {
            if(board[row-1][col+1].getToken().equals(token))
                score += num++; // Right-Up
        }
        if(row < dim-1 && col > 0) {
            if(board[row+1][col-1].getToken().equals(token))
                score += num++;
        }
        if(row < dim-1 && col < dim-1) {
            if(board[row+1][col+1].getToken().equals(token))
                score += num;
        }
        return score;
    }

	/* Find piece captured by the player him/herself. 
	   The strategy is check 4 adjacent cells, if none of them are non-captured opponent token or empty token. */
	private void updateSelfCapture(String token) {
		boolean check;
		String token_op;  // Opponent token
		int n;
		/* Set opponent token. */
		if(token.equals(TOKEN_BLACK))
			token_op = TOKEN_WHITE;
		else
			token_op = TOKEN_BLACK;
		for(int row = 0; row < dim; row++) {
			for(int col = 0; col < dim; col++) {
				if(board[row][col].getToken().equals(token)) {
					if(col > 0 && col < dim - 1 && row > 0 && row < dim - 1) {
						/* Check 4 adjacent cells whether none of them are non-captured opopnent token or empty token. */
						check = true;
						n = 0;
						if(board[row][col-1].getToken().equals(token_op) || board[row][col-1].getToken().equals(TOKEN_EMPTY))
							check = false; // Left
						if(board[row][col+1].getToken().equals(token_op) || board[row][col+1].getToken().equals(TOKEN_EMPTY))
							check = false; // Right
						if(board[row-1][col].getToken().equals(token_op) || board[row-1][col].getToken().equals(TOKEN_EMPTY))
							check = false; // Up
						if(board[row+1][col].getToken().equals(token_op) || board[row+1][col].getToken().equals(TOKEN_EMPTY))
							check = false; // Down
						/* Check whether there is at least one adjacent cell is captured. */
						if(board[row][col-1].getToken().equals(token))
							n++; // Left
						if(board[row][col+1].getToken().equals(token))
							n++; // Right
						if(board[row-1][col].getToken().equals(token))
							n++; // Up
						if(board[row+1][col].getToken().equals(token))
							n++; // Down
						/* Update if found one self captured piece. */
						if(check && n < 4)
							capture(row, col, PLAYER_NONE);
					}
				}
			}
		}
	}

	/* Check whether a Vector object is in a ArrayList. (Checking by value, not reference.) */
	private boolean isVecInList(Vector v, ArrayList<Vector> list) {
		for(Vector v_l : list) {
			if(v.Row == v_l.Row && v.Col == v_l.Col)
				return true;
		}
		return false;
	}
}
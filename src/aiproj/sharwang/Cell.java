package aiproj.sharwang;

/**
 *  Cell:
 *    The cells on the board.
 *    A 6x6 board contains 36 cells.
 *
 *  Authors:
 *      Rohan Sharma (rsharma1)
 *      Shiyi Wang (sharwang)
 */

public class Cell {
	private int row;
	private int col;
	private String token;
	private int player;

    /**
     * Initialize a cell.
     *
     * @param row
     * @param col
     * @param token
     * @param player
     */
	public Cell(int row, int col, String token, int player) {
		this.row = row;
		this.col = col;
		this.token = token;
		this.player = player;
	}

    /**
     * Copy a cell to this cell.
     *
     * @param c
     */
	public Cell(Cell c) {
		this.row = c.row;
		this.col = c.col;
		this.token = c.token;
		this.player = c.player;
	}

    /**
     * Get the token on the cell.
     *
     * @return the token on the cell
     */
	public String getToken() {
		return token;
	}

    /**
     * Set the token on the cell.
     *
     * @param token the token we want to set.
     */
	public void setToken(String token) {
		this.token = token;
	}

    /**
     * Get the owner of the cell.
     *
     * @return   the owner ID
     */
	public int getPlayer() {
		return player;
	}

    /**
     * Set the owner of the cell.
     * @param player  the owner ID
     */
	public void setPlayer(int player) {
		this.player = player;
	}
}
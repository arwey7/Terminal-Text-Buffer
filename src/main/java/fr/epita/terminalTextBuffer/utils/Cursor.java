package fr.epita.terminalTextBuffer.utils;

public class Cursor {
    private int row, col;
    private int screenHeight, screenWidth;

    /**
     * Creates a cursor for a TerminalTextBuffer object
     * @param row
     * Initial row of the cursor
     * @param col
     * Initial column of the cursor
     * @param screenHeight
     * Height of the terminal text buffer screen
     * @param screenWidth
     * Width of the terminal text buffer screen
     */
    public Cursor(int row, int col, int screenHeight, int screenWidth) {
        if (screenHeight < 0 || screenWidth < 0) {
            throw new IllegalArgumentException("Screen height or screen width is negative");
        }
        if (row < 0 || row >= screenHeight || col < 0 || col >= screenWidth) {
            throw new IllegalArgumentException("Cursor out of bounds");
        }
        this.row = row;
        this.col = col;
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
    }

    // --- Coordinate getters and setters ---

    /**
     * Gets the cursor's current row.
     * @return
     * integer corresponding to the cursor's row number
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the cursor's current column.
     * @return
     * integer corresponding to the cursor's column number
     */
    public int getCol() {
        return col;
    }

    /**
     * Moves the cursor to a given row.
     * @param row
     * positive integer corresponding to the target row number (must be less than the buffer's height)
     */
    public void setRow(int row) {
        if (row >= 0 && row < screenHeight) {
            this.row = row;
        }
    }

    /**
     * Moves the cursor to a given column.
     * @param col
     * positive integer corresponding to the target column number (must be less than the buffer's width)
     */
    public void setCol(int col) {
        if (col >= 0 && col < screenWidth) {
            this.col = col;
        }
    }

    /**
     * Moves the cursor by a given number of rows.
     * If the given number of lines is negative, moves the cursor up.
     * Otherwise, the cursor is moved down.
     * @param delta
     * integer giving the relative row cursor movement
     */
    public void moveRow(int delta) {
        row = Math.clamp(row + delta, 0, screenHeight - 1);
    }

    /**
     * Moves the cursor by a given number of columns.
     * If the given number of lines is negative, moves the cursor left.
     * Otherwise, the cursor is moved to the right.
     * @param delta
     * integer giving the relative column cursor movement
     */
    public void moveCol(int delta) {
        col = Math.clamp(col + delta, 0, screenWidth - 1);
    }

    /**
     * Moves the cursor up by a given number of cells.
     * @param delta
     * positive integer giving the cursor up movement
     */
    public void moveUp(int delta) {
        if (delta <= 0) {
            return;
        }
        moveRow(-delta);
    }

    /**
     * Moves the cursor dowm by a given number of cells.
     * @param delta
     * positive integer giving the cursor dowm movement
     */
    public void moveDown(int delta) {
        if (delta <= 0) {
            return;
        }
        moveRow(delta);
    }

    /**
     * Moves the cursor left by a given number of cells.
     * @param delta
     * positive integer giving the cursor left movement
     */
    public void moveLeft(int delta) {
        if (delta <= 0) {
            return;
        }
        moveCol(-delta);
    }

    /**
     * Moves the cursor right by a given number of cells.
     * @param delta
     * positive integer giving the cursor right movement
     */
    public void moveRight(int delta) {
        if (delta <= 0) {
            return;
        }
        moveCol(delta);
    }
}
package fr.epita.terminalTextBuffer.utils;

public class Cursor {
    private int row, col;
    private int screenHeight, screenWidth;

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

    // coordinate getters and setters

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setRow(int row) {
        if (row >= 0 && row < screenHeight) {
            this.row = row;
        }
    }

    public void setCol(int col) {
        if (col >= 0 && col < screenWidth) {
            this.col = col;
        }
    }

    // cursor movement by n cells

    // if n is negative, the cursor moves up, otherwise it moves right
    public void moveRow(int delta) {
        row = Math.clamp(row + delta, 0, screenHeight - 1);
    }

    // if n is negative, the cursor moves left, otherwise it moves right
    public void moveCol(int delta) {
        col = Math.clamp(col + delta, 0, screenWidth - 1);
    }

    // directional movement wrapper
    public void moveUp(int delta) {
        moveRow(-delta);
    }
    public void moveDown(int delta) {
        moveRow(delta);
    }
    public void moveLeft(int delta) {
        moveCol(-delta);
    }
    public void moveRight(int delta) {
        moveCol(delta);
    }
}
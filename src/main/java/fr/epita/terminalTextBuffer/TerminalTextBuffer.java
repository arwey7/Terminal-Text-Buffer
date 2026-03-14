package fr.epita.terminalTextBuffer;

import fr.epita.terminalTextBuffer.utils.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumSet;
import java.util.Optional;

public class TerminalTextBuffer {

    // Fields storing the state of the buffer

    private int screenHeight, screenWidth;
    private CharacterCell[][] screen;
    private int maxScrollbackSize;
    private Deque<CharacterCell[]> scrollback;

    private final Cursor cursor;
    private TerminalColor currentFgColor = TerminalColor.DEFAULT;
    private TerminalColor currentBgColor = TerminalColor.DEFAULT;
    private EnumSet<StyleFlag> currentStyles = EnumSet.noneOf(StyleFlag.class);

    // --- Constructor ---

    /**
     * Creates a new terminal text buffer object.
     * @param maxScrollbackSize
     * maximum scrollback size of the terminal
     * @param initialHeight
     * configurable buffer width
     * @param initialWidth
     * configurable buffer height
     */
    public TerminalTextBuffer(int maxScrollbackSize, int initialHeight, int initialWidth) {

        // Checking parameters' validity
        if (maxScrollbackSize < 0) {
            throw new IllegalArgumentException("Scrollback size must be positive");
        }
        if (initialWidth <= 0 || initialHeight <= 0) {
            throw new IllegalArgumentException("Width and height must be positive");
        }

        // Setting up the screen and the scrollback
        this.screenHeight = initialHeight;
        this.screenWidth = initialWidth;
        this.maxScrollbackSize = maxScrollbackSize;

        this.screen = new CharacterCell[initialHeight][initialWidth];
        this.scrollback = new ArrayDeque<>(maxScrollbackSize);

        // Setting all the cells to empty characters
        for (int row = 0; row < initialHeight; row++) {
            for (int col = 0; col < initialWidth; col++) {
                screen[row][col] = CharacterCell.empty();
            }
        }

        // Creating the cursor and moving it to the beginning of the buffer
        this.cursor = new Cursor(0, 0, initialHeight, initialWidth);
    }

    // --- Helpers ---

    /**
     * Pushes the top screen line into scrollback. Shifts all lines up, clears the bottom line.
     * Called when writing past the last row.
     */
    private void scrollUp() {
        if (maxScrollbackSize > 0) {
            if (scrollback.size() >= maxScrollbackSize) {
                scrollback.pollFirst();
            }
            scrollback.addLast(screen[0].clone());
        }
        // shift lines up
        for (int row = 0; row < screenHeight - 1; row++) {
            screen[row] = screen[row + 1];
        }

        // clear the bottom line
        screen[screenHeight - 1] = new CharacterCell[screenWidth];
        for (int col = 0; col < screenWidth; col++) {
            screen[screenHeight - 1][col] = CharacterCell.empty();
        }
    }

    /**
     * Advances the cursor by one cell. Wraps to the next line at the right edge.
     * Scrolls up if the cursor advances past the last row.
     */
    private void advanceCursor() {
        if (cursor.getCol() < screenWidth - 1) {
            cursor.moveRight(1);
        } else if (cursor.getRow() < screenHeight - 1) {
            cursor.setCol(0);
            cursor.moveDown(1);
        } else {
            scrollUp();
            cursor.setCol(0);
        }
    }

    // --- Cursor-dependent editing ---

    /**
     * Writes text stating at the cursor position. The existing content is overwritten.
     * Wraps to the next line when reaching the right edge. Scrolls if needed.
     * Moves the cursor to after the last written character.
     * @param text the string to write
     */
    public void writeText(String text) {
        for (char c : text.toCharArray()) {
            screen[cursor.getRow()][cursor.getCol()] = new CharacterCell(Optional.of(c), currentFgColor, currentBgColor, currentStyles);
            advanceCursor();
        }
    }

    /**
     * Inserts text at the cursor position. Shifts existing content to the right.
     * @param text the string to insert
     */
    public void insertText(String text) {
        for (char c : text.toCharArray()) {
            int row = cursor.getRow();
            int col = cursor.getCol();

            // shift cells right from end of line to cursor col, dropping the last cell
            for (int i = screenWidth - 1; i > col; i--) {
                screen[row][i] = screen[row][i - 1];
            }
            screen[row][col] = new CharacterCell(Optional.of(c), currentFgColor, currentBgColor, currentStyles);
            advanceCursor();
        }
    }

    /**
     * Fills the entire row at the cursor's current position with a given character.
     * The cursor doesn't move.
     * @param c optional character to fill the line with
     */
    public void fillLine(Optional<Character> c) {
        int row = cursor.getRow();
        for (int col = 0; col < screenWidth; col++) {
            screen[col][row] = c.map(character -> new CharacterCell(Optional.of(character), currentFgColor, currentBgColor, currentStyles)).orElseGet(CharacterCell::empty);
        }
    }

    // --- Cursor-independent editing ---

    /**
     * Inserts a blank line at the bottom of the screen. The top line is pushed into scrollback. All lines shift up.
     */
    public void insertEmptyLine() {
        scrollUp();
    }

    /**
     * Clears the entire screen. All cells become empty with default attributes.
     * Cursor moves to (0,0). Scrollback is not affected.
     */
    public void clearScreen() {
        for (int row = 0; row < screenHeight; row++) {
            for (int col = 0; col < screenWidth; col++) {
                screen[row][col] = CharacterCell.empty();
            }
        }
        cursor.setRow(0);
        cursor.setCol(0);
    }

    /**
     * Same behavior as clearScreen(), but the whole scrollback content is also cleared.
     */
    public void clearAll() {
        clearScreen();
        scrollback.clear();
    }
}
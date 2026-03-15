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
     *
     * @param maxScrollbackSize maximum scrollback size of the terminal
     * @param initialHeight     configurable buffer width
     * @param initialWidth      configurable buffer height
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

    // --- Getters ---

    /**
     * Gets the buffer's actual number of lines saved in scrollback.
     *
     * @return integer corresponding to the buffer's scrollback size
     */
    public int getScrollbackSize() {
        return scrollback.size();
    }

    /**
     * Gets the current cursor row.
     *
     * @return integer corresponding to the row of the cursor
     */
    public int getCursorRow() {
        return cursor.getRow();
    }

    /**
     * Gets the current cursor column.
     *
     * @return integer corresponding to the column of the cursor
     */
    public int getCursorCol() {
        return cursor.getCol();
    }

    /**
     * Gets the cell at given coordinates.
     * Throws and IllegalArgumentException if the coordinates are out of the buffer's bounds
     *
     * @param row row of the target cell
     * @param col column of the target cell
     * @return CharacterCell object located at screen[row][column]
     */
    public CharacterCell getScreenCell(int row, int col) {
        if (row < 0 || col < 0 || row >= screenHeight || col >= screenWidth) {
            throw new IllegalArgumentException("Row and column out of bounds");
        }
        return screen[row][col];
    }

    // --- Setters ---

    /**
     * Sets the buffer's background color to a given color.
     *
     * @param color TerminalColor object corresponding to the new buffer's color
     */
    public void setBackgroundColor(TerminalColor color) {
        this.currentBgColor = color;
    }

    /**
     * Sets the buffer's foreground color to a given color.
     *
     * @param color TerminalColor object corresponding to the new buffer's color
     */
    public void setForegroundColor(TerminalColor color) {
        this.currentFgColor = color;
    }

    /**
     * Sets the style flags of the buffer to those given as a parameter.
     *
     * @param styles EnumSet of styles containing StyleFlag objects
     */
    public void setStyles(EnumSet<StyleFlag> styles) {
        this.currentStyles = styles.isEmpty() ? EnumSet.noneOf(StyleFlag.class) : EnumSet.copyOf(styles);
    }

    /**
     * Adds a style flag to the terminal.
     *
     * @param style StyleFlag object to be added to the currentStyles EnumSet
     */
    public void addStyle(StyleFlag style) {
        this.currentStyles.add(style);
    }

    /**
     * Removes a style flag from the terminal.
     * Does nothing if the given flag is not found.
     *
     * @param style StyleFlag object to be removed from the currentStyles EnumSet
     */
    public void removeStyle(StyleFlag style) {
        this.currentStyles.remove(style);
    }

    /**
     * Clears all the style flags from the terminal.
     * Does nothing if the given flag is not found.
     */
    public void clearStyles() {
        this.currentStyles = EnumSet.noneOf(StyleFlag.class);
    }

    /**
     * Moves the cursor to given coordinates.
     * Throws IllegalArgumentException if the coordinates are out of the buffer's bounds.
     *
     * @param row integer for the target row
     * @param col integer for the target column
     */
    public void setCursor(int row, int col) {
        if (row < 0 || col < 0 || row >= screenHeight || col >= screenWidth) {
            throw new IllegalArgumentException("Row or column out of bounds");
        }
        cursor.setRow(row);
        cursor.setCol(col);
    }

    /**
     * Moves the cursor by the given relative coordinates.
     * Stops moving after reaching the buffer's bounds.
     *
     * @param rowDelta integer for row movement (negative: up, positive: down)
     * @param colDelta integer for column movement (negative: left, positive: right)
     */
    public void moveCursor(int rowDelta, int colDelta) {
        cursor.moveRow(rowDelta);
        cursor.moveCol(colDelta);
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
     *
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
     *
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
     *
     * @param c optional character to fill the line with
     */
    public void fillLine(Optional<Character> c) {
        int row = cursor.getRow();
        for (int col = 0; col < screenWidth; col++) {
            screen[row][col] = c.map(character -> new CharacterCell(Optional.of(character), currentFgColor, currentBgColor, currentStyles)).orElseGet(CharacterCell::empty);
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

    // --- Content access helpers ---

    /**
     * Gets a scrollback row by index. Row 0 is the oldest.
     */
    private CharacterCell[] getScrollbackRow(int row) {
        if (row < 0 || row >= scrollback.size()) {
            throw new IllegalArgumentException("Scrollback row out of bounds");
        }
        return (CharacterCell[]) scrollback.toArray()[row];
    }

    /**
     * Gets a cell from the scrollback.
     */
    private CharacterCell getScrollbackCell(int row, int col) {
        if (col < 0 || col >= screenWidth) {
            throw new IllegalArgumentException("Column out of bounds");
        }
        return getScrollbackRow(row)[col];
    }

// --- Content access functions ---

    /**
     * Gets the character at a given position on the screen.
     *
     * @param row screen row index
     * @param col screen column index
     * @return Optional<Character> at that position
     */
    public Optional<Character> getCharAt(int row, int col) {
        return getScreenCell(row, col).getCharacter();
    }

    /**
     * Gets the character at a given position in the scrollback.
     * Row 0 is the oldest line, row scrollbackSize-1 is the most recent.
     *
     * @param row scrollback row index
     * @param col scrollback column index
     * @return Optional<Character> at that position
     */
    public Optional<Character> getScrollbackCharAt(int row, int col) {
        return getScrollbackCell(row, col).getCharacter();
    }

    /**
     * Gets the attributes (as a CharacterCell) at a given screen position.
     *
     * @param row screen row index
     * @param col screen column index
     */
    public CharacterCell getAttributesAt(int row, int col) {
        return getScreenCell(row, col);
    }

    /**
     * Gets the attributes (as a CharacterCell) at a given scrollback position.
     *
     * @param row scrollback row index
     * @param col scrollback column index
     */
    public CharacterCell getScrollbackAttributesAt(int row, int col) {
        return getScrollbackCell(row, col);
    }


    /**
     * Gets a screen row as a plain string. Empty cells become spaces.
     * Row 0 is the oldest line.
     *
     * @param row screen row index
     * @return string representation of the row
     */
    public String getScreenLineAsString(int row) {
        if (row < 0 || row >= screenHeight) {
            throw new IllegalArgumentException("Row out of bounds");
        }
        StringBuilder sb = new StringBuilder(screenWidth);
        for (int col = 0; col < screenWidth; col++) {
            sb.append(screen[row][col].getCharacter().orElse(' '));
        }
        return sb.toString();
    }

    /**
     * Gets a scrollback row as a plain string. Empty cells become spaces.
     * Row 0 is the oldest line.
     *
     * @param row scrollback row index
     * @return string representation of the row
     */
    public String getScrollbackLineAsString(int row) {
        CharacterCell[] line = getScrollbackRow(row);
        StringBuilder sb = new StringBuilder(screenWidth);

        for (CharacterCell cell : line) {
            sb.append(cell.getCharacter().orElse(' '));
        }

        return sb.toString();
    }

    /**
     * Gets the entire screen content as a string.
     * Rows are separated by newlines. Empty cells become spaces.
     * @return string representation of the screen
     */
    public String getScreenAsString() {
        StringBuilder sb = new StringBuilder(screenHeight * (screenWidth + 1));

        for (int row = 0; row < screenHeight; row++) {
            sb.append(getScreenLineAsString(row));
            if (row < screenHeight - 1) {
                sb.append('\n');
            }
        }

        return sb.toString();
    }

    /**
     * Gets the entire scrollback + screen content as a string.
     * Oldest scrollback line first, screen last. Rows separated by newlines.
     * @return string representation of scrollback and screen combined
     */
    public String getAllAsString() {
        StringBuilder sb = new StringBuilder();
        int scrollbackSize = scrollback.size();

        if (scrollbackSize > 0) {
            for (int row = 0; row < scrollbackSize; row++) {
                sb.append(getScrollbackLineAsString(row));
                sb.append('\n');
            }
        }

        sb.append(getScreenAsString());
        return sb.toString();
    }
}
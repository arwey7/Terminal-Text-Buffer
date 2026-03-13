package fr.epita.terminalTextBuffer;

import fr.epita.terminalTextBuffer.utils.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumSet;
import java.util.Optional;

public class TerminalTextBuffer {
    // fields storing the state
    private int screenHeight, screenWidth;
    private Cursor cursor;

    // colors
    private TerminalColor currentFgColor;
    private TerminalColor currentBgColor;

    private final int maxScrollbackSize;
    private Deque<CharacterCell[]> scrollback;
    private CharacterCell[][] screen;

    public TerminalTextBuffer(int maxScrollbackSize, int initialHeight, int initialWidth) {

        // checking parameters validity
        if (maxScrollbackSize < 0) {
            throw new IllegalArgumentException("Scrollback size must be positive");
        }
        if (initialWidth <= 0 || initialHeight <= 0) {
            throw new IllegalArgumentException("Width and height must be positive");
        }

        // setting up the screen and the scrollback
        this.screenHeight = initialHeight;
        this.screenWidth = initialWidth;
        this.maxScrollbackSize = maxScrollbackSize;

        this.screen = new CharacterCell[initialHeight][initialWidth];
        this.scrollback = new ArrayDeque<>(maxScrollbackSize);

        // setting all the cells to empty characters
        for (int row = 0; row < initialHeight; row++) {
            for (int col = 0; col < initialWidth; col++) {
                screen[row][col] = new CharacterCell(Optional.empty(), TerminalColor.DEFAULT, TerminalColor.DEFAULT, EnumSet.noneOf(StyleFlag.class));
            }
        }

        // setting up the cursor to the beginning of the buffer
        this.cursor = new Cursor(0, 0, initialHeight, initialWidth);
    }
}
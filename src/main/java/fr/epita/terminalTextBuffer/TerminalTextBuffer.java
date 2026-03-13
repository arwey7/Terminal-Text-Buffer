package fr.epita.terminalTextBuffer;

import fr.epita.terminalTextBuffer.utils.*;

import java.util.EnumSet;
import java.util.Optional;

public class TerminalTextBuffer {
    // record classes for couples of integers
    private record ScreenSize(int row, int col) {}

    private ScreenSize screenSize;
    private Cursor cursor;

    private final int maxScrollbackSize;
    private CharacterCell[][] scrollback;
    private CharacterCell[][] screen;

    public TerminalTextBuffer(int maxScrollbackSize, int initialHeight, int initialWidth) {

        // checking parameters validity
        if (maxScrollbackSize <= 0) {
            throw new IllegalArgumentException("maxScrollbackSize must be positive");
        }
        if (initialWidth <= 0 || initialHeight <= 0) {
            throw new IllegalArgumentException("initialWidth and initialHeight must be positive");
        }

        // setting up the screen and the scrollback
        this.screenSize = new ScreenSize(initialWidth, initialHeight);
        this.maxScrollbackSize = maxScrollbackSize;

        this.screen = new CharacterCell[initialHeight][initialWidth];
        this.scrollback = new CharacterCell[maxScrollbackSize][];

        // setting all the cells to empty characters
        for (int row = 0; row < initialHeight; row++) {
            for (int col = 0; col < initialWidth; col++) {
                screen[row][col] = new CharacterCell(Optional.empty(), TerminalColor.WHITE, TerminalColor.BLACK, EnumSet.noneOf(StyleFlag.class));
            }
        }

        // setting up the cursor to the beginning of the buffer
        this.cursor = new Cursor(0, 0);
    }
}
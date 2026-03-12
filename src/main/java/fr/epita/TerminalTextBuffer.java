package fr.epita;

import fr.epita.utils.CharacterCell;

import java.util.ArrayList;

public class TerminalTextBuffer {
    private ArrayList<ArrayList<CharacterCell>> screen;
    private final int maxScrollbackSize;

    private record position(int row, int col) {};

    public TerminalTextBuffer(int maxScrollbackSize, int initialWidth, int initialHeight) {
        if (maxScrollbackSize <= 0) {
            throw new IllegalArgumentException("maxScrollbackSize must be positive");
        }
        if (initialWidth <= 0 || initialHeight <= 0) {
            throw new IllegalArgumentException("initialWidth and initialHeight must be positive");
        }

        this.maxScrollbackSize = maxScrollbackSize;
        screen = new ArrayList<>();
    }
}

package fr.epita.terminalTextBuffer.utils;

import java.util.EnumSet;
import java.util.Optional;

public class CharacterCell {
    private Optional<Character> character;
    private TerminalColor foregroundColor;
    private TerminalColor backgroundColor;
    private EnumSet<StyleFlag> styleFlags;

    public CharacterCell(Optional<Character> character, TerminalColor foregroundColor, TerminalColor backgroundColor, EnumSet<StyleFlag> styleFlags) {
        this.character = character;
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
        this.styleFlags = styleFlags.isEmpty() ? EnumSet.noneOf(StyleFlag.class) : EnumSet.copyOf(styleFlags);
    }

    /**
     * Factory method for creating an empty character cell
     * @return
     * a new empty character cell
     */
    public static CharacterCell empty() {
        return new CharacterCell(Optional.empty(), TerminalColor.DEFAULT, TerminalColor.DEFAULT, EnumSet.noneOf(StyleFlag.class));
    }

    // --- Getters and setters ---

    public Optional<Character> getCharacter() {
        return character;
    }

    public void setCharacter(Optional<Character> character) {
        this.character = character;
    }

    public TerminalColor getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(TerminalColor foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public TerminalColor getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(TerminalColor backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public EnumSet<StyleFlag> getStyleFlags() {
        return styleFlags;
    }

    public void setStyleFlags(EnumSet<StyleFlag> styleFlags) {
        this.styleFlags = styleFlags;
    }
}

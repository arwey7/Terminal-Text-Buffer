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
        this.styleFlags = styleFlags;
    }
}

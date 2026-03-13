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

    public static CharacterCell empty() {
        return new CharacterCell(Optional.empty(), TerminalColor.DEFAULT, TerminalColor.DEFAULT, EnumSet.noneOf(StyleFlag.class));
    }

    public static CharacterCell of(char c, TerminalColor foregroundColor, TerminalColor backgroundColor, EnumSet<StyleFlag> styleFlags) {
        return new CharacterCell(Optional.of(c),
                foregroundColor,
                backgroundColor,
                EnumSet.copyOf(styleFlags.isEmpty() ? EnumSet.noneOf(StyleFlag.class) : styleFlags));
    }
}

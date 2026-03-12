package fr.epita.utils;

import java.util.ArrayList;
import java.util.Optional;

public class CharacterCell {
    private Optional<Character> character;
    private TerminalColor foregroundColor;
    private TerminalColor backgroundColor;
    private ArrayList<StyleFlag> styleFlags;

    public CharacterCell(Optional<Character> character, TerminalColor foregroundColor, TerminalColor backgroundColor, ArrayList<StyleFlag> styleFlags) {
        this.character = character;
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
        this.styleFlags = styleFlags;
    }
}

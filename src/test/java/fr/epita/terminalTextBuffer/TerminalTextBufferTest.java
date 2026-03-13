package fr.epita.terminalTextBuffer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TerminalTextBufferTest {
    // constructor
    @Test
    void createBufferBasic() {

    }

    @Test
    void createBufferIllegal() {
        assertThrows(IllegalArgumentException.class, () -> {});
    }
}
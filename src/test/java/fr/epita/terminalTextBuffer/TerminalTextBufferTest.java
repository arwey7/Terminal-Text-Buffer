package fr.epita.terminalTextBuffer;

import fr.epita.terminalTextBuffer.utils.*;
import org.junit.jupiter.api.*;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TerminalTextBuffer — Editing")
class TerminalTextBufferEditingTest {

    private static final int HEIGHT = 5;
    private static final int WIDTH = 10;
    private static final int SCROLLBACK = 10;

    private TerminalTextBuffer buf;

    @BeforeEach
    void setUp() {
        buf = new TerminalTextBuffer(SCROLLBACK, HEIGHT, WIDTH);
    }

    // =========================================================================
    // writeText
    // =========================================================================

    @Nested
    @DisplayName("writeText")
    class WriteText {

        @Test
        @DisplayName("writes characters starting at cursor position")
        void writesAtCursor() {
            buf.writeText("hi");
            assertEquals('h', buf.getScreenCell(0, 0).getCharacter().get());
            assertEquals('i', buf.getScreenCell(0, 1).getCharacter().get());
        }

        @Test
        @DisplayName("overwrites existing content")
        void overwritesExisting() {
            buf.writeText("hello");
            buf.setCursor(0, 0);
            buf.writeText("world");
            assertEquals('w', buf.getScreenCell(0, 0).getCharacter().get());
            assertEquals('d', buf.getScreenCell(0, 4).getCharacter().get());
        }

        @Test
        @DisplayName("advances cursor by text length")
        void advancesCursor() {
            buf.writeText("hello");
            assertEquals(0, buf.getCursorRow());
            assertEquals(5, buf.getCursorCol());
        }

        @Test
        @DisplayName("wraps to next line at right edge")
        void wrapsAtRightEdge() {
            buf.writeText("0123456789X"); // 10 chars fill row 0, X wraps
            assertEquals('X', buf.getScreenCell(1, 0).getCharacter().get());
            assertEquals(1, buf.getCursorRow());
            assertEquals(1, buf.getCursorCol());
        }

        @Test
        @DisplayName("scrolls up when writing past last row")
        void scrollsAtBottom() {
            buf.writeText("0123456789".repeat(HEIGHT)); // fill all rows
            buf.writeText("X");                         // triggers scroll
            assertEquals(HEIGHT - 1, buf.getCursorRow());
        }

        @Test
        @DisplayName("stamps current foreground color on written cells")
        void stampsForegroundColor() {
            buf.setForegroundColor(TerminalColor.RED);
            buf.writeText("A");
            assertEquals(TerminalColor.RED, buf.getScreenCell(0, 0).getForegroundColor());
        }

        @Test
        @DisplayName("stamps current background color on written cells")
        void stampsBackgroundColor() {
            buf.setBackgroundColor(TerminalColor.BLUE);
            buf.writeText("A");
            assertEquals(TerminalColor.BLUE, buf.getScreenCell(0, 0).getBackgroundColor());
        }

        @Test
        @DisplayName("stamps current style flags on written cells")
        void stampsStyleFlags() {
            buf.addStyle(StyleFlag.BOLD);
            buf.writeText("A");
            assertTrue(buf.getScreenCell(0, 0).getStyleFlags().contains(StyleFlag.BOLD));
        }

        @Test
        @DisplayName("cells written before attribute change keep old attributes")
        void attributeChangeDoesNotAffectPreviousCells() {
            buf.setForegroundColor(TerminalColor.RED);
            buf.writeText("A");
            buf.setForegroundColor(TerminalColor.GREEN);
            buf.writeText("B");
            assertEquals(TerminalColor.RED,   buf.getScreenCell(0, 0).getForegroundColor());
            assertEquals(TerminalColor.GREEN, buf.getScreenCell(0, 1).getForegroundColor());
        }

        @Test
        @DisplayName("empty string does not move cursor")
        void emptyStringDoesNotMoveCursor() {
            buf.writeText("");
            assertEquals(0, buf.getCursorRow());
            assertEquals(0, buf.getCursorCol());
        }

        @Test
        @DisplayName("cells not written remain empty")
        void unwrittenCellsRemainEmpty() {
            buf.writeText("AB");
            assertTrue(buf.getScreenCell(0, 2).getCharacter().isEmpty());
        }

        @Test
        @DisplayName("writes at non-origin cursor position")
        void writesAtNonOriginCursor() {
            buf.setCursor(2, 3);
            buf.writeText("XY");
            assertEquals('X', buf.getScreenCell(2, 3).getCharacter().get());
            assertEquals('Y', buf.getScreenCell(2, 4).getCharacter().get());
        }
    }

    // =========================================================================
    // insertText
    // =========================================================================

    @Nested
    @DisplayName("insertText")
    class InsertText {

        @Test
        @DisplayName("inserts character at cursor, shifting content right")
        void insertsAndShifts() {
            buf.writeText("ABCD");
            buf.setCursor(0, 1);
            buf.insertText("X");
            assertEquals('A', buf.getScreenCell(0, 0).getCharacter().get());
            assertEquals('X', buf.getScreenCell(0, 1).getCharacter().get());
            assertEquals('B', buf.getScreenCell(0, 2).getCharacter().get());
            assertEquals('C', buf.getScreenCell(0, 3).getCharacter().get());
            assertEquals('D', buf.getScreenCell(0, 4).getCharacter().get());
        }

        @Test
        @DisplayName("advances cursor after insert")
        void advancesCursorAfterInsert() {
            buf.insertText("X");
            assertEquals(0, buf.getCursorRow());
            assertEquals(1, buf.getCursorCol());
        }

        @Test
        @DisplayName("overflow at right edge is dropped")
        void overflowDropped() {
            buf.writeText("ABCDEFGHIJ"); // fill row 0 exactly
            buf.setCursor(0, 0);
            buf.insertText("X");        // X inserted, J drops off
            assertEquals('X', buf.getScreenCell(0, 0).getCharacter().get());
            assertEquals('A', buf.getScreenCell(0, 1).getCharacter().get());
            assertEquals('I', buf.getScreenCell(0, 9).getCharacter().get());
        }

        @Test
        @DisplayName("stamps current attributes on inserted cells")
        void stampsAttributes() {
            buf.setForegroundColor(TerminalColor.CYAN);
            buf.insertText("Z");
            assertEquals(TerminalColor.CYAN, buf.getScreenCell(0, 0).getForegroundColor());
        }

        @Test
        @DisplayName("empty string does not move cursor or change cells")
        void emptyStringNoOp() {
            buf.insertText("");
            assertEquals(0, buf.getCursorRow());
            assertEquals(0, buf.getCursorCol());
            assertTrue(buf.getScreenCell(0, 0).getCharacter().isEmpty());
        }

        @Test
        @DisplayName("inserting at end of line shifts nothing")
        void insertAtEndOfLine() {
            buf.writeText("ABCDEFGHI"); // 9 chars, cursor at col 9
            buf.insertText("Z");
            assertEquals('Z', buf.getScreenCell(0, 9).getCharacter().get());
        }
    }

    // =========================================================================
    // fillLine
    // =========================================================================

    @Nested
    @DisplayName("fillLine")
    class FillLine {

        @Test
        @DisplayName("fills entire current row with given character")
        void fillsRowWithCharacter() {
            buf.fillLine(Optional.of('*'));
            for (int col = 0; col < WIDTH; col++) {
                assertEquals('*', buf.getScreenCell(0, col).getCharacter().get());
            }
        }

        @Test
        @DisplayName("fills entire current row with empty cells when Optional.empty()")
        void fillsRowWithEmpty() {
            buf.writeText("ABCDE");
            buf.setCursor(0, 0);
            buf.fillLine(Optional.empty());
            for (int col = 0; col < WIDTH; col++) {
                assertTrue(buf.getScreenCell(0, col).getCharacter().isEmpty());
            }
        }

        @Test
        @DisplayName("fills the correct row based on cursor position")
        void fillsCorrectRow() {
            buf.setCursor(2, 0);
            buf.fillLine(Optional.of('Z'));
            assertEquals('Z', buf.getScreenCell(2, 0).getCharacter().get());
            assertTrue(buf.getScreenCell(0, 0).getCharacter().isEmpty());
            assertTrue(buf.getScreenCell(1, 0).getCharacter().isEmpty());
        }

        @Test
        @DisplayName("cursor does not move after fillLine")
        void cursorDoesNotMove() {
            buf.setCursor(1, 3);
            buf.fillLine(Optional.of('X'));
            assertEquals(1, buf.getCursorRow());
            assertEquals(3, buf.getCursorCol());
        }

        @Test
        @DisplayName("stamps current attributes when filling with a character")
        void stampsAttributes() {
            buf.setForegroundColor(TerminalColor.YELLOW);
            buf.fillLine(Optional.of('A'));
            assertEquals(TerminalColor.YELLOW, buf.getScreenCell(0, 0).getForegroundColor());
        }

        @Test
        @DisplayName("empty fill produces cells with default attributes")
        void emptyFillHasDefaultAttributes() {
            buf.setForegroundColor(TerminalColor.RED);
            buf.fillLine(Optional.empty());
            assertEquals(TerminalColor.DEFAULT, buf.getScreenCell(0, 0).getForegroundColor());
        }

        @Test
        @DisplayName("does not affect other rows")
        void doesNotAffectOtherRows() {
            buf.setCursor(1, 0);
            buf.fillLine(Optional.of('X'));
            assertTrue(buf.getScreenCell(0, 0).getCharacter().isEmpty());
            assertTrue(buf.getScreenCell(2, 0).getCharacter().isEmpty());
        }
    }

    // =========================================================================
    // insertEmptyLine
    // =========================================================================

    @Nested
    @DisplayName("insertEmptyLine")
    class InsertEmptyLine {

        @Test
        @DisplayName("bottom row is empty after insert")
        void bottomRowIsEmpty() {
            buf.insertEmptyLine();
            for (int col = 0; col < WIDTH; col++) {
                assertTrue(buf.getScreenCell(HEIGHT - 1, col).getCharacter().isEmpty());
            }
        }

        @Test
        @DisplayName("top line is pushed to scrollback")
        void topLinePushedToScrollback() {
            buf.writeText("HELLO");
            buf.insertEmptyLine();
            assertEquals(1, buf.getScrollbackSize());
        }

        @Test
        @DisplayName("inserting more lines than scrollback capacity evicts oldest")
        void evictsOldestWhenFull() {
            TerminalTextBuffer small = new TerminalTextBuffer(2, HEIGHT, WIDTH);
            small.insertEmptyLine();
            small.insertEmptyLine();
            small.insertEmptyLine(); // should evict oldest
            assertEquals(2, small.getScrollbackSize());
        }

        @Test
        @DisplayName("with zero scrollback capacity, lines are discarded")
        void zeroScrollbackDiscardsLines() {
            TerminalTextBuffer noScrollback = new TerminalTextBuffer(0, HEIGHT, WIDTH);
            noScrollback.writeText("HELLO");
            noScrollback.insertEmptyLine();
            assertEquals(0, noScrollback.getScrollbackSize());
        }
    }

    // =========================================================================
    // clearScreen
    // =========================================================================

    @Nested
    @DisplayName("clearScreen")
    class ClearScreen {

        @Test
        @DisplayName("all screen cells become empty")
        void allCellsEmpty() {
            buf.writeText("HELLO");
            buf.clearScreen();
            for (int row = 0; row < HEIGHT; row++) {
                for (int col = 0; col < WIDTH; col++) {
                    assertTrue(buf.getScreenCell(row, col).getCharacter().isEmpty());
                }
            }
        }

        @Test
        @DisplayName("cursor moves to (0, 0)")
        void cursorMovesToOrigin() {
            buf.setCursor(3, 5);
            buf.clearScreen();
            assertEquals(0, buf.getCursorRow());
            assertEquals(0, buf.getCursorCol());
        }

        @Test
        @DisplayName("scrollback is not affected")
        void scrollbackUnaffected() {
            buf.insertEmptyLine();
            buf.clearScreen();
            assertEquals(1, buf.getScrollbackSize());
        }

        @Test
        @DisplayName("cleared cells have default attributes")
        void clearedCellsHaveDefaultAttributes() {
            buf.setForegroundColor(TerminalColor.RED);
            buf.writeText("HELLO");
            buf.clearScreen();
            assertEquals(TerminalColor.DEFAULT, buf.getScreenCell(0, 0).getForegroundColor());
        }
    }

    // =========================================================================
    // clearAll
    // =========================================================================

    @Nested
    @DisplayName("clearAll")
    class ClearAll {

        @Test
        @DisplayName("all screen cells become empty")
        void allCellsEmpty() {
            buf.writeText("HELLO");
            buf.clearAll();
            for (int row = 0; row < HEIGHT; row++) {
                for (int col = 0; col < WIDTH; col++) {
                    assertTrue(buf.getScreenCell(row, col).getCharacter().isEmpty());
                }
            }
        }

        @Test
        @DisplayName("cursor moves to (0, 0)")
        void cursorMovesToOrigin() {
            buf.setCursor(3, 5);
            buf.clearAll();
            assertEquals(0, buf.getCursorRow());
            assertEquals(0, buf.getCursorCol());
        }

        @Test
        @DisplayName("scrollback is also cleared")
        void scrollbackCleared() {
            buf.insertEmptyLine();
            buf.insertEmptyLine();
            buf.clearAll();
            assertEquals(0, buf.getScrollbackSize());
        }
    }
}
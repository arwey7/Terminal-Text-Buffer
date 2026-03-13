package fr.epita.terminalTextBuffer.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Cursor")
class CursorTest {

    private static final int HEIGHT = 24;
    private static final int WIDTH = 80;

    private Cursor cursor;

    @BeforeEach
    void setUp() {
        cursor = new Cursor(0, 0, HEIGHT, WIDTH);
    }

    // Construction

    @Nested
    @DisplayName("Construction")
    class Construction {

        @Test
        @DisplayName("stores valid initial position correctly")
        void validInitialPosition() {
            Cursor c = new Cursor(5, 10, HEIGHT, WIDTH);
            assertEquals(5, c.getRow());
            assertEquals(10, c.getCol());
        }

        @Test
        @DisplayName("origin (0, 0) is a valid position")
        void originIsValid() {
            Cursor c = new Cursor(0, 0, HEIGHT, WIDTH);
            assertEquals(0, c.getRow());
            assertEquals(0, c.getCol());
        }

        @Test
        @DisplayName("last valid cell (height-1, width-1) is accepted")
        void lastCellIsValid() {
            assertDoesNotThrow(() -> new Cursor(HEIGHT - 1, WIDTH - 1, HEIGHT, WIDTH));
        }

        @Test
        @DisplayName("row equal to screenHeight throws")
        void rowEqualsHeightThrows() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Cursor(HEIGHT, 0, HEIGHT, WIDTH));
        }

        @Test
        @DisplayName("col equal to screenWidth throws")
        void colEqualsWidthThrows() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Cursor(0, WIDTH, HEIGHT, WIDTH));
        }

        @Test
        @DisplayName("negative row throws")
        void negativeRowThrows() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Cursor(-1, 0, HEIGHT, WIDTH));
        }

        @Test
        @DisplayName("negative col throws")
        void negativeColThrows() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Cursor(0, -1, HEIGHT, WIDTH));
        }

        @Test
        @DisplayName("negative screenHeight throws")
        void negativeScreenHeightThrows() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Cursor(0, 0, -1, WIDTH));
        }

        @Test
        @DisplayName("negative screenWidth throws")
        void negativeScreenWidthThrows() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Cursor(0, 0, HEIGHT, -1));
        }
    }

    // =========================================================================
    // Setters
    // =========================================================================

    @Nested
    @DisplayName("setRow / setCol")
    class Setters {

        @Test
        @DisplayName("setRow updates to a valid row")
        void setRowValid() {
            cursor.setRow(10);
            assertEquals(10, cursor.getRow());
        }

        @Test
        @DisplayName("setCol updates to a valid col")
        void setColValid() {
            cursor.setCol(40);
            assertEquals(40, cursor.getCol());
        }

        @Test
        @DisplayName("setRow to 0 is valid")
        void setRowToZero() {
            cursor.setRow(5);
            cursor.setRow(0);
            assertEquals(0, cursor.getRow());
        }

        @Test
        @DisplayName("setRow to last row is valid")
        void setRowToLastRow() {
            cursor.setRow(HEIGHT - 1);
            assertEquals(HEIGHT - 1, cursor.getRow());
        }

        @Test
        @DisplayName("setRow beyond bounds does not update row")
        void setRowOutOfBoundsIgnored() {
            cursor.setRow(5);
            cursor.setRow(HEIGHT + 10);
            assertEquals(5, cursor.getRow());
        }

        @Test
        @DisplayName("setRow to negative does not update row")
        void setRowNegativeIgnored() {
            cursor.setRow(5);
            cursor.setRow(-1);
            assertEquals(5, cursor.getRow());
        }

        @Test
        @DisplayName("setCol beyond bounds does not update col")
        void setColOutOfBoundsIgnored() {
            cursor.setCol(10);
            cursor.setCol(WIDTH + 10);
            assertEquals(10, cursor.getCol());
        }

        @Test
        @DisplayName("setCol to negative does not update col")
        void setColNegativeIgnored() {
            cursor.setCol(10);
            cursor.setCol(-1);
            assertEquals(10, cursor.getCol());
        }

        @Test
        @DisplayName("setRow does not affect col")
        void setRowDoesNotAffectCol() {
            cursor.setCol(20);
            cursor.setRow(10);
            assertEquals(20, cursor.getCol());
        }

        @Test
        @DisplayName("setCol does not affect row")
        void setColDoesNotAffectRow() {
            cursor.setRow(10);
            cursor.setCol(20);
            assertEquals(10, cursor.getRow());
        }
    }

    // =========================================================================
    // moveRow / moveCol
    // =========================================================================

    @Nested
    @DisplayName("moveRow / moveCol")
    class MoveRowCol {

        @Test
        @DisplayName("moveRow with positive delta moves down")
        void moveRowPositive() {
            cursor.setRow(5);
            cursor.moveRow(3);
            assertEquals(8, cursor.getRow());
        }

        @Test
        @DisplayName("moveRow with negative delta moves up")
        void moveRowNegative() {
            cursor.setRow(5);
            cursor.moveRow(-3);
            assertEquals(2, cursor.getRow());
        }

        @Test
        @DisplayName("moveRow clamps at bottom boundary")
        void moveRowClampBottom() {
            cursor.setRow(HEIGHT - 1);
            cursor.moveRow(100);
            assertEquals(HEIGHT - 1, cursor.getRow());
        }

        @Test
        @DisplayName("moveRow clamps at top boundary")
        void moveRowClampTop() {
            cursor.setRow(0);
            cursor.moveRow(-100);
            assertEquals(0, cursor.getRow());
        }

        @Test
        @DisplayName("moveRow with delta 0 does not move")
        void moveRowZero() {
            cursor.setRow(5);
            cursor.moveRow(0);
            assertEquals(5, cursor.getRow());
        }

        @Test
        @DisplayName("moveRow does not affect col")
        void moveRowDoesNotAffectCol() {
            cursor.setRow(5);
            cursor.setCol(20);
            cursor.moveRow(3);
            assertEquals(20, cursor.getCol());
        }

        @Test
        @DisplayName("moveCol with positive delta moves right")
        void moveColPositive() {
            cursor.setCol(10);
            cursor.moveCol(5);
            assertEquals(15, cursor.getCol());
        }

        @Test
        @DisplayName("moveCol with negative delta moves left")
        void moveColNegative() {
            cursor.setCol(10);
            cursor.moveCol(-5);
            assertEquals(5, cursor.getCol());
        }

        @Test
        @DisplayName("moveCol clamps at right boundary")
        void moveColClampRight() {
            cursor.setCol(WIDTH - 1);
            cursor.moveCol(100);
            assertEquals(WIDTH - 1, cursor.getCol());
        }

        @Test
        @DisplayName("moveCol clamps at left boundary")
        void moveColClampLeft() {
            cursor.setCol(0);
            cursor.moveCol(-100);
            assertEquals(0, cursor.getCol());
        }

        @Test
        @DisplayName("moveCol with delta 0 does not move")
        void moveColZero() {
            cursor.setCol(10);
            cursor.moveCol(0);
            assertEquals(10, cursor.getCol());
        }

        @Test
        @DisplayName("moveCol does not affect row")
        void moveColDoesNotAffectRow() {
            cursor.setRow(10);
            cursor.setCol(20);
            cursor.moveCol(5);
            assertEquals(10, cursor.getRow());
        }
    }

    // =========================================================================
    // Directional wrappers
    // =========================================================================

    @Nested
    @DisplayName("Directional movement wrappers")
    class DirectionalMovement {

        @Test
        @DisplayName("moveUp(n) decreases row by n")
        void moveUp() {
            cursor.setRow(10);
            cursor.moveUp(3);
            assertEquals(7, cursor.getRow());
        }

        @Test
        @DisplayName("moveDown(n) increases row by n")
        void moveDown() {
            cursor.setRow(5);
            cursor.moveDown(4);
            assertEquals(9, cursor.getRow());
        }

        @Test
        @DisplayName("moveLeft(n) decreases col by n")
        void moveLeft() {
            cursor.setCol(20);
            cursor.moveLeft(5);
            assertEquals(15, cursor.getCol());
        }

        @Test
        @DisplayName("moveRight(n) increases col by n")
        void moveRight() {
            cursor.setCol(10);
            cursor.moveRight(7);
            assertEquals(17, cursor.getCol());
        }

        @Test
        @DisplayName("moveUp clamps at top boundary")
        void moveUpClampsAtTop() {
            cursor.setRow(2);
            cursor.moveUp(100);
            assertEquals(0, cursor.getRow());
        }

        @Test
        @DisplayName("moveDown clamps at bottom boundary")
        void moveDownClampsAtBottom() {
            cursor.setRow(HEIGHT - 2);
            cursor.moveDown(100);
            assertEquals(HEIGHT - 1, cursor.getRow());
        }

        @Test
        @DisplayName("moveLeft clamps at left boundary")
        void moveLeftClampsAtLeft() {
            cursor.setCol(2);
            cursor.moveLeft(100);
            assertEquals(0, cursor.getCol());
        }

        @Test
        @DisplayName("moveRight clamps at right boundary")
        void moveRightClampsAtRight() {
            cursor.setCol(WIDTH - 2);
            cursor.moveRight(100);
            assertEquals(WIDTH - 1, cursor.getCol());
        }

        @Test
        @DisplayName("moveUp does not affect col")
        void moveUpDoesNotAffectCol() {
            cursor.setRow(10);
            cursor.setCol(20);
            cursor.moveUp(3);
            assertEquals(20, cursor.getCol());
        }

        @Test
        @DisplayName("moveDown does not affect col")
        void moveDownDoesNotAffectCol() {
            cursor.setRow(5);
            cursor.setCol(20);
            cursor.moveDown(3);
            assertEquals(20, cursor.getCol());
        }

        @Test
        @DisplayName("moveLeft does not affect row")
        void moveLeftDoesNotAffectRow() {
            cursor.setRow(10);
            cursor.setCol(20);
            cursor.moveLeft(5);
            assertEquals(10, cursor.getRow());
        }

        @Test
        @DisplayName("moveRight does not affect row")
        void moveRightDoesNotAffectRow() {
            cursor.setRow(10);
            cursor.setCol(20);
            cursor.moveRight(5);
            assertEquals(10, cursor.getRow());
        }

        @Test
        @DisplayName("moveUp(0) does not move")
        void moveUpZero() {
            cursor.setRow(5);
            cursor.moveUp(0);
            assertEquals(5, cursor.getRow());
        }

        @Test
        @DisplayName("moveRight(0) does not move")
        void moveRightZero() {
            cursor.setCol(10);
            cursor.moveRight(0);
            assertEquals(10, cursor.getCol());
        }
    }
}
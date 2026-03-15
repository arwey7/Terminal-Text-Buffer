# Terminal text buffer

This terminal text buffer implementation in Java consists of three main classes: `TerminalTextBuffer`, `Cursor`, and `CharacterCell`, as well as two enums `TerminalColor` and `StyleFlag`.

The screen is stored as a 2D array indexed by `[row][col]`. This gives O(1) access for all read and write operations.

Scrollback is stored as `Deque<CharacterCell[]>` backed by `ArrayDeque`. Scrollback only ever needs O(1) push at the tail and O(1) eviction at the head, which is exactly what a double-ended queue provides. The tradeoff is that random index access requires `toArray()` which is O(n), but scrollback random access is rare compared to sequential rendering.

Each cell (`CharacterCell`) stores its character, foreground color, background color, and style flags independently. This means every cell is self-contained — the renderer needs no external context to draw it. `CharacterCell` is immutable: all write operations replace cells rather than mutating them, which eliminates a class of bugs where scrollback content could be silently modified after being pushed (since `scrollback` stores shallow-cloned row arrays).

## Arbitrary decisions I had to make

- Separating the screen and scrollback coordinate systems as a unified system would have made screen-only access more verbose.
- Scrollback row 0 is the oldest line, which matches the user's mental model of scrolling up.
- Inserting an empty line does not move the cursor.
- `CharacterCell` is immutable, which prevents bugs with shared cell references.
- `TerminalColor` includes a DEFAULT color to distinguish it with the color the user explicitly sets.
- Copying style flags in the `CharacterCell` constructor prevents the buffer's current state from being aliased into stored cells.
- `setCursor` throws on out-of-bounds while `moveCursor` clamps silently.

## Limitations and trade-offs

- Single-row scrollback access allocates a full array copy each time. `getAllAsString` avoids this by calling `toArray()` once and iterating. A `LinkedList` would give true O(n) indexed access without copying, but `ArrayDeque` was preferred for general performance.
- On resize, content that was wrapped due to a narrow width stays wrapped after widening.
- `insertText` drops overflow. When inserting text into a full line, the last cell is lost. An alternative would be to wrap overflow onto the next line, but I didn't find the added complexity justified.
- `insertEmptyLine` on a buffer with `maxScrollbackSize == 0` silently discards the top line with no way to recover it. Scrollback-disabled buffers are lossy by design.
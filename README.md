# Terminal Text Buffer

A Java implementation of a terminal text buffer — the core data structure that terminal emulators use to store and manipulate displayed text.

## Features
- Fixed-size screen grid of character cells (configurable width and height)
- Scrollback buffer with configurable maximum size
- Per-cell attributes: foreground color, background color, and style flags
- Cursor movement with bounds clamping
- Write and insert text with line wrapping and auto-scroll
- Fill, clear, and insert line operations
- Content access for both screen and scrollback
- Screen resize with truncation

## Building

### Compiling
Requires Java 21+ and Maven.

```sh
mvn compile
```

### Running tests
Requires JUnit 5.21.1+ or later.

```sh
mvn test
```

## Design notes
See [SOLUTION.md](SOLUTION.md) for a detailed expanation of the design decisions and trade-offs.

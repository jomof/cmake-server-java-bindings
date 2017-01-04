package com.jomofisher.cmake.database;

/**
 * Represents a compilation of a single source file.
 */
public class Compilation {
    final public String directory;
    final public String command;
    final public String file;

    Compilation() {
        this.directory = null;
        this.command = null;
        this.file = null;
    }
}

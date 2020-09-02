package com.github.klyser8.karmaoverload.storage;

public enum DebugLevel {

    NONE(0),
    LOW(1),
    HIGH(2);

    private final int level;

    DebugLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
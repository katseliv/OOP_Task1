package com.company;

public class Player {
    private int name;
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_PURPLE = "\u001B[35m";

    public Player(int id) {
        this.name = id;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return ANSI_GREEN + "\nPlayer: " + ANSI_BLACK + name;
    }
}

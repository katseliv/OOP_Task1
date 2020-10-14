package com.company;

public class Card {
    private String id;
    private char type;
    private final int compareNumber;

    public Card(String id, char type, int compareNumber) {
        this.id = id;
        this.type = type;
        this.compareNumber = compareNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public int getCompareNumber() {
        return compareNumber;
    }

    @Override
    public String toString() {
        return "Id = " + id + " Type = " + type;
    }
}

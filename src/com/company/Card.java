package com.company;

public class Card {
    private String id;
    private char type;

    public Card(String id, char type) {
        this.id = id;
        this.type = type;
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

    @Override
    public String toString() {
        return "Id = " + id + " Type = " + type;
    }
}

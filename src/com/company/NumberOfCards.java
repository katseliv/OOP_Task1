package com.company;

public enum NumberOfCards {
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    TEN("10"),
    JACK("J"),
    DAME("D"),
    KING("K"),
    ACE("A");

    private final String number;

    NumberOfCards(String number) {
        this.number = number;
    }

    public static final String[] NUMBER_OF_CARDS = new String[NumberOfCards.values().length];

    static {
        for (int i = 0; i < NumberOfCards.values().length; i++) {
            NUMBER_OF_CARDS[i] = NumberOfCards.values()[i].number;
        }
    }
}

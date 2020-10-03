package com.company;

public enum CardSuit {
    CLUBS('\u2663'),
    DIAMONDS('\u2666'),
    SPADES('\u2660'),
    HEARTS('\u2764');

    private char sign;

    CardSuit(char sign) {
        this.sign = sign;
    }

    public static final char[] CARD_SUIT = new char[CardSuit.values().length];

    static {
        for (int i = 0; i < CardSuit.values().length; i++)
            CARD_SUIT[i] = CardSuit.values()[i].sign;
    }

}

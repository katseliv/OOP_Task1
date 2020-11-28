package game.enums;

public enum CardSuit {
    CLUBS('\u2663'),
    DIAMONDS('\u2666'),
    SPADES('\u2660'),
    HEARTS('\u2764');

    private final char sign;

    CardSuit(char sign) {
        this.sign = sign;
    }

    public char getSign() {
        return sign;
    }

    public static final CardSuit[] CARD_SUIT = new CardSuit[CardSuit.values().length];

    static {
        for (int i = 0; i < CardSuit.values().length; i++)
            CARD_SUIT[i] = CardSuit.values()[i];
    }
}

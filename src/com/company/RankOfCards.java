package com.company;

public enum RankOfCards {
    TWO("2", 1),
    THREE("3", 2),
    FOUR("4", 2),
    FIVE("5", 3),
    SIX("6", 4),
    SEVEN("7", 5),
    EIGHT("8", 6),
    NINE("9", 7),
    TEN("10", 8),
    JACK("J", 9),
    DAME("D", 10),
    KING("K", 11),
    ACE("A", 12);

    private final String rank;
    private final int compareNumber;

    RankOfCards(String number, int compareNumber) {
        this.rank = number;
        this.compareNumber = compareNumber;
    }

    private static final int START_POSITION = 4;
    private static final int LENGTH_OF_SMALL_DECK = RankOfCards.values().length - START_POSITION;
    public static final RankOfCards[] BIG_DECK = new RankOfCards[RankOfCards.values().length];
    public static final RankOfCards[] SMALL_DECK = new RankOfCards[LENGTH_OF_SMALL_DECK];

    static {
        for (int i = 0; i < RankOfCards.values().length; i++) {
            BIG_DECK[i] = RankOfCards.values()[i];
        }
    }

    static {
        for (int i = 0; i < LENGTH_OF_SMALL_DECK; i++) {
            SMALL_DECK[i] = RankOfCards.values()[i + START_POSITION];
        }
    }

    public String getRank() {
        return rank;
    }

    public int getCompareNumber() {
        return compareNumber;
    }

    @Override
    public String toString() {
        return "RankOfCards{" + "number = '" + rank + '\'' + '}';
    }
}

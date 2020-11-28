package game.enums;

public enum RankOfCards {
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

    private final String rank;

    RankOfCards(String number) {
        this.rank = number;
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

}

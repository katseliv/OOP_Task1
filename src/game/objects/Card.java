package game.objects;

import game.enums.CardSuit;
import game.enums.RankOfCards;

public class Card {
    private final RankOfCards rank;
    private final CardSuit suit;

    public Card(RankOfCards id, CardSuit suit) {
        this.rank = id;
        this.suit = suit;
    }

    public RankOfCards getRank() {
        return rank;
    }

    public CardSuit getSuit() {
        return suit;
    }

    @Override
    public String toString() {
        final String ANSI_RED = "\u001B[31m";
        final String ANSI_BLACK = "\u001B[30m";

        String color = ANSI_BLACK;
        if(suit == CardSuit.HEARTS || suit == CardSuit.DIAMONDS){
            color = ANSI_RED;
        }

        return  "\n" + ANSI_BLACK + "Rank = " + color + rank.getRank() + ANSI_BLACK +" Suit = " + color + suit.getSign() + ANSI_BLACK;
    }
}

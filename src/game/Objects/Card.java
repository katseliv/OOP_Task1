package game.Objects;

import game.Enum.CardSuit;
import game.Enum.RankOfCards;

public class Card {
    private RankOfCards rank;
    private CardSuit suit;

    public Card(RankOfCards id, CardSuit suit) {
        this.rank = id;
        this.suit = suit;
    }

    public RankOfCards getRank() {
        return rank;
    }

    public void setRank(RankOfCards rank) {
        this.rank = rank;
    }

    public CardSuit getSuit() {
        return suit;
    }

    public void setSuit(CardSuit suit) {
        this.suit = suit;
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

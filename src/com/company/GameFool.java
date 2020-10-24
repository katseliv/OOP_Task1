package com.company;

import java.util.*;

public class GameFool {
    private Card trump;
    public final int NUMBER_OF_CARDS = 6;
    public final int NUMBER_OF_PLAYERS = 6;
    private CyclicList<Player> players = new CyclicList<>();
    private List<Card> cards = new ArrayList<>();
    private List<Step> steps = new ArrayList<>();
    private Map<Player, Set<Card>> ratio = new HashMap<>();

    public GameFool() {

    }

    public CyclicList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(CyclicList<Player> players) {
        this.players = players;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public Map<Player, Set<Card>> getRatio() {
        return ratio;
    }

    public void setRatio(Map<Player, Set<Card>> ratio) {
        this.ratio = ratio;
    }

    public Card getTrump() {
        return trump;
    }

    public void setTrump(Card trump) {
        this.trump = trump;
    }

    @Override
    public String toString() {
        final String ANSI_BLACK = "\u001B[30m";
        final String ANSI_PURPLE = "\u001B[35m";

        return ANSI_BLACK + ANSI_PURPLE + " \nGameFool {"
                + "\nplayers = in developing"
                + ",\ncards = " + cards + ANSI_PURPLE
                + "\nlength = "+ cards.size()
                + ", steps = " + steps
                + ", ratio = " + ratio + '}';
    }
}

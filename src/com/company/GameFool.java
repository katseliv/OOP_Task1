package com.company;

import java.util.*;

public class GameFool {
    private Card trump;
    private Player winPlayer;
    private int numberOfPlayers;
    public final int NUMBER_OF_CARDS = 6;
    public final int NUMBER_CARDS_FOR_TOSS_UP = NUMBER_OF_CARDS - 1;
    private final CyclicList<Player> players = new CyclicList<>();
    private final Map<Player, Set<Card>> ratio = new HashMap<>();
    private final List<Card> cards = new ArrayList<>();
    private final List<Step> steps = new ArrayList<>();
    private final List<Integer> winPlayers = new ArrayList<>();

    public GameFool() {

    }

    public CyclicList<Player> getPlayers() {
        return players;
    }

    public List<Card> getCards() {
        return cards;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public Map<Player, Set<Card>> getRatio() {
        return ratio;
    }

    public Card getTrump() {
        return trump;
    }

    public void setTrump(Card trump) {
        this.trump = trump;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public Player getWinPlayer() {
        return winPlayer;
    }

    public void setWinPlayer(Player winPlayer) {
        this.winPlayer = winPlayer;
    }

    public List<Integer> getWinPlayers() {
        return winPlayers;
    }

    public boolean isEnd(){
        return cards.size() == 0 && numberOfPlayers == 0;
    }

    @Override
    public String toString() {
        final String ANSI_BLACK = "\u001B[30m";
        final String ANSI_PURPLE = "\u001B[35m";

        return ANSI_BLACK + ANSI_PURPLE + " \nGameFool {"
                + "\nplayers = " + numberOfPlayers
                + ",\ncards = " + cards + ANSI_PURPLE
                + "\nlength = "+ cards.size()
                + ", steps = " + steps
                + ", ratio = " + ratio + '}';
    }
}

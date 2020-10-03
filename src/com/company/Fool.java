package com.company;

import java.util.*;

public class Fool {
    private ArrayDeque<Player> players = new ArrayDeque<>();
    private List<Card> cards = new ArrayList<>();
    private List<Step> steps = new ArrayList<>();
    private Map<Player, Set<Card>> ratio = new HashMap<>();

    public Fool() {}

    public ArrayDeque<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayDeque<Player> players) {
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

}

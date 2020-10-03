package com.company;

import java.util.*;

public class Service { //методы для обработки данных
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public Service() {
    }

    public void start(Fool fool, int amountOfPlayers, int amountOfCards) {
        initialization(fool, amountOfPlayers, amountOfCards);
        distributeCards(fool);
    }

    public void initialization(Fool fool, int amountOfPlayers, int amountOfCards) { //создание всего
        initializationPlayers(fool, amountOfPlayers);
        List<Card> cards = initializationCards(fool, amountOfCards);
        shuffleCards(cards);
    }

    private List<Card> initializationCards(Fool fool, int amount) {
        List<Card> cards = fool.getCards();

        String[] numberOfCards = NumberOfCards.NUMBER_OF_CARDS;
        char[] cardSuit = CardSuit.CARD_SUIT;

        if (amount == 36) {
            for (int i = 5; i < numberOfCards.length; i++) {
                for (char suit : cardSuit) {
                    cards.add(new Card(numberOfCards[i], suit));
                }
            }
        } else if (amount == 52) {
            for (String numberOfCard : numberOfCards) {
                for (char suit : cardSuit) {
                    cards.add(new Card(numberOfCard, suit));
                }
            }
        } else {
            System.out.println(ANSI_PURPLE + "Такое кол-во карт недопустимо!!!" + ANSI_RESET);
        }

        for (Card card : cards) {
            //System.out.println(card);
        }

        return cards;
    }

    private void initializationPlayers(Fool fool, int amount) {
        ArrayDeque<Player> players = fool.getPlayers();

        for (int i = 1; i <= amount; i++) {
            players.add(new Player(i));
        }

        for (Player player : players) {
            //System.out.println(player);
        }
    }

    private void shuffleCards(List<Card> cards) {
        System.out.println(ANSI_RESET + ANSI_BLUE + "Генерация колоды:" + ANSI_BLACK);
        Collections.shuffle(cards);
        for (Card card : cards) {
            System.out.println(card);
        }
    }

    void distributeCards(Fool fool) {
        Map<Player, Set<Card>> ratio = fool.getRatio();
        ArrayDeque<Player> players = fool.getPlayers();
        List<Card> cards = fool.getCards();

        for (Player player : players) {
            Set<Card> cardsOfPlayer = new HashSet<>();
            for (int i = 1; i <= 6; i++) {
                int j = (int) (Math.random() * cards.size());
                cardsOfPlayer.add(cards.get(j));
                cards.remove(j);
            }
            ratio.put(player, cardsOfPlayer);
        }

        System.out.println();

        for (Map.Entry<Player, Set<Card>> playerSetEntry : ratio.entrySet()) {
            System.out.println(ANSI_RESET + ANSI_GREEN + playerSetEntry.getKey() + ANSI_BLACK +" -> ");
            for (Card card : playerSetEntry.getValue()) {
                System.out.println(card);
            }
        }
    }

    void doStep(){

    }
}

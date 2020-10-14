package com.company;

import java.util.*;

public class Service {
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

    public void start(GameFool gameFool, int amountOfPlayers, int amountOfCards) {
        initialization(gameFool, amountOfPlayers, amountOfCards);
        distributeCards(gameFool);
        //playTillTheEnd(gameFool, amountOfPlayers);
        //giveCards(fool);
    }

    public void initialization(GameFool gameFool, int amountOfPlayers, int amountOfCards) { //создание всего
        initializationPlayers(gameFool, amountOfPlayers);
        List<Card> cards = initializationCards(gameFool, amountOfCards);
        shuffleCards(cards);
        chooseTrump(cards);

    }

    private List<Card> initializationCards(GameFool gameFool, int amount) {
        List<Card> cards = gameFool.getCards();
        String[] numberOfCards = NumberOfCards.NUMBER_OF_CARDS;
        char[] cardSuit = CardSuit.CARD_SUIT;

        if (amount == 36) {
            for (int i = 4; i < numberOfCards.length; i++) {
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
        return cards;
    }

    private void initializationPlayers(GameFool gameFool, int amount) {
        CyclicList<Player> players = gameFool.getPlayers();
        for (int i = 1; i <= amount; i++) {
            players.add(new Player(i));
        }

        System.out.println("Инициализация игроков");
        for (Player player : players) {
            System.out.println(player);
        }

    }

    private void shuffleCards(List<Card> cards) {
        System.out.println(ANSI_RESET + ANSI_BLUE + "Генерация колоды:" + ANSI_BLACK);
        Collections.shuffle(cards);

        int i = 0;
        for (Card card : cards) {
            System.out.println(card);
            i++;
        }
        System.out.println("Кол-во карт " + i);
    }

    void distributeCards(GameFool gameFool) {
        Map<Player, Set<Card>> ratio = gameFool.getRatio();
        CyclicList<Player> players = gameFool.getPlayers();
        List<Card> cards = gameFool.getCards();
        List<Card> toRemove = new ArrayList<>();

        for (Player player : players) {
            Set<Card> cardsOfPlayer = new HashSet<>();
            for (int i = 0; i < 3; i++) { // 6 must be
                cardsOfPlayer.add(cards.get(i));
                toRemove.add(cards.get(i));
            }
            cards.removeAll(toRemove);
            ratio.put(player, cardsOfPlayer);
        }

        System.out.println();
        for (Map.Entry<Player, Set<Card>> playerSetEntry : ratio.entrySet()) {
            System.out.println(ANSI_RESET + ANSI_GREEN + playerSetEntry.getKey() + ANSI_BLACK + " -> ");
            for (Card card : playerSetEntry.getValue()) {
                System.out.println(card);
            }
        }

        System.out.println();
        int i = 0;
        for (Card card : cards) {
            System.out.println(card);
            i++;
        }
        System.out.println("Кол-во карт " + i);
    }

    void chooseTrump(List<Card> cards) {
        System.out.println();
        int number = cards.size() - 1;
        Card trump = cards.get(number);
        System.out.println(ANSI_RESET + "Trump -> " + ANSI_RED + trump);
        System.out.println();
        for (Card card : cards) {
            System.out.println(card);
        }
    }

    void playTillTheEnd(GameFool gameFool, int amountOfPlayers) {
        CyclicList<Player> players = gameFool.getPlayers();
        int i = 0;
        while (players.getCount() != 0) {

            for(Player player : players){

            }

            if (i == amountOfPlayers) {
                i = 0;
            } else {
                i++;
            }

        }
    }

    void attack(GameFool context, Player targetPlayer) { // кого атакуют

    }

    void beatOff() {

    }

    void tossUp() {

    }

    void giveCards(GameFool gameFool) {
        System.out.println();
        System.out.println("Добавили карты: ");
        Map<Player, Set<Card>> ratio = gameFool.getRatio();
        List<Card> cards = gameFool.getCards();

        for (Map.Entry<Player, Set<Card>> playerSetEntry : ratio.entrySet()) {
            int size = 6 - playerSetEntry.getValue().size();
            if (size > 0) {

                for (int i = 0; i < size; i++) {
                    playerSetEntry.getValue().add(cards.get(0));
                    cards.remove(0);
                }
            }
        }

        for (Map.Entry<Player, Set<Card>> playerSetEntry : ratio.entrySet()) {
            System.out.println(ANSI_RESET + ANSI_GREEN + playerSetEntry.getKey() + ANSI_BLACK + " -> ");
            for (Card card : playerSetEntry.getValue()) {
                System.out.println(card);
            }
        }

        System.out.println();
        int i = 0;
        for (Card card : cards) {
            System.out.println(card);
            i++;
        }
        System.out.println("Кол-во карт " + i);
    }

}

package com.company;

import java.util.*;

public class Service {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public Service() {
    }

    public void start(GameFool gameFool, int amountOfPlayers, int amountOfCards) {
        initialization(gameFool, amountOfPlayers, amountOfCards);
        distributeCards(gameFool);
        //playTillTheEnd(gameFool, amountOfPlayers);
    }

    public void initialization(GameFool gameFool, int amountOfPlayers, int amountOfCards) { //создание всего
        initializationPlayers(gameFool, amountOfPlayers);
        List<Card> cards = initializationCards(gameFool, amountOfCards);
        shuffleCards(cards);
        chooseTrump(cards);

    }

    private List<Card> initializationCards(GameFool gameFool, int amount) {
        String ANSI_PURPLE = "\u001B[35m";
        List<Card> cards = gameFool.getCards();
        String[] numberOfCards = NumberOfCards.NUMBER_OF_CARDS;
        char[] cardSuit = CardSuit.CARD_SUIT;
        int i = 0;
        if (amount == 36) {
            for (i = 4; i < numberOfCards.length; i++) {
                for (char suit : cardSuit) {
                    cards.add(new Card(numberOfCards[i], suit, i));
                }
            }
        } else if (amount == 52) {
            for (String numberOfCard : numberOfCards) {
                for (char suit : cardSuit) {
                    cards.add(new Card(numberOfCard, suit, i));
                }
                i++;
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
        String ANSI_BLACK = "\u001B[30m";
        String ANSI_BLUE = "\u001B[34m";
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
        String ANSI_BLACK = "\u001B[30m";
        String ANSI_GREEN = "\u001B[32m";
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
        String ANSI_RED = "\u001B[31m";
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

            for (Player player : players) {
                System.out.println(player);
            }

            if (i == amountOfPlayers) {
                i = 0;
            } else {
                i++;
            }
            break;
        }
    }

    Card attack(GameFool context, Player attackPlayer, Card trump) {
        Map<Player, Set<Card>> ratio = context.getRatio();
        int minNoTrump = Integer.MAX_VALUE;
        int minTrump = Integer.MAX_VALUE;
        Card cardNoTrump = null, cardTrump = null;

        for (Card card : ratio.get(attackPlayer)) {
            if (card.getCompareNumber() < minNoTrump && !isTrump(card, trump)) {
                minNoTrump = card.getCompareNumber();
                cardNoTrump = card;
            } else if (card.getCompareNumber() < minTrump) {
                minTrump = card.getCompareNumber();
                cardTrump = card;
            }
        }

        if (cardNoTrump == null) {
            return cardTrump;
        }
        return cardNoTrump;
    } //temporary

    boolean isTrump(Card card, Card trump) {
        return card.getType() == trump.getType();
    }

    Card beatOff(List<Card> remainingCards, Card attackCard) {
        int min = Integer.MAX_VALUE;
        Card minCard = null;

        for (Card card : remainingCards) {
            if (card.getType() == attackCard.getType() && card.getCompareNumber() > attackCard.getCompareNumber() && card.getCompareNumber() < min) {
                min = card.getCompareNumber();
                minCard = card;
            }
        }

        return minCard;
    }  //temporary

    List<Card> tossUp(List<Card> cardsOnTheTable, List<Card> cardsOfPlayer) {
        List<Card> cardsForTossUp = new ArrayList<>();
        List<Card> cardsForRemoving = new ArrayList<>();

        for (Card cardOnTheTable : cardsOnTheTable){
            for (Card cardForTossUp : cardsOfPlayer) {
                if (cardOnTheTable.getId().equals(cardForTossUp.getId())) {
                    cardsForTossUp.add(cardForTossUp);
                    cardsForRemoving.add(cardForTossUp);
                }
            }
            cardsOfPlayer.removeAll(cardsForRemoving);
        }

        return cardsForTossUp;
    } //temporary

    void giveCards(GameFool gameFool) {
        String ANSI_GREEN = "\u001B[32m";
        String ANSI_BLACK = "\u001B[30m";
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
    } //fulfilled
}

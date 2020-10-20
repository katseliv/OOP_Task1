package com.company;

import java.util.*;

public class Service {

    public void start(GameFool gameFool, int amountOfPlayers, int amountOfCards) {
        initialization(gameFool, amountOfPlayers, amountOfCards);
        distributeCards(gameFool);
        chooseTrump(gameFool);
        //playTillTheEnd(gameFool, amountOfPlayers);
        giveCards(gameFool);
    }

    public void initialization(GameFool gameFool, int amountOfPlayers, int amountOfCards) {
        System.out.print("\u001B[30m" + "Start our game !!!");
        initializationPlayers(gameFool, amountOfPlayers);
        List<Card> cards = initializationCards(gameFool, amountOfCards);
        shuffleCards(cards);
    }

    private void initializationPlayers(GameFool gameFool, int amount) {
        CyclicList<Player> players = gameFool.getPlayers();
        for (int i = 1; i <= amount; i++) {
            players.add(new Player(i));
        }

        for (Player player : players) {
            System.out.print(player);
        }
    }

    private List<Card> initializationCards(GameFool gameFool, int amountOfCards) {
        List<Card> cards = gameFool.getCards();
        RankOfCards[] rankOfCards = new RankOfCards[0];
        CardSuit[] cardSuit = CardSuit.CARD_SUIT;

        if (amountOfCards == 36) {
            rankOfCards = RankOfCards.SMALL_DECK;
        } else if (amountOfCards == 52) {
            rankOfCards = RankOfCards.BIG_DECK;
        }

        for (RankOfCards numberOfCard : rankOfCards) {
            for (CardSuit suit : cardSuit) {
                cards.add(new Card(numberOfCard, suit));
            }
        }

        System.out.println(gameFool);
        return cards;
    }

    private void shuffleCards(List<Card> cards) {
        Collections.shuffle(cards);
        System.out.println();
        System.out.println("\u001B[34m" + "Shuffle cards: " + "\u001B[30m" + cards + "\nlength = " + cards.size());
    }

    private void distributeCards(GameFool gameFool) {
        Map<Player, Set<Card>> ratio = gameFool.getRatio();
        CyclicList<Player> players = gameFool.getPlayers();
        List<Card> cards = gameFool.getCards();
        List<Card> toRemove = new ArrayList<>();

        for (Player player : players) {
            Set<Card> cardsOfPlayer = new HashSet<>();
            for (int i = 0; i < gameFool.NUMBER_OF_PLAYERS; i++) {
                cardsOfPlayer.add(cards.get(i));
                toRemove.add(cards.get(i));
            }
            cards.removeAll(toRemove);
            ratio.put(player, cardsOfPlayer);
        }

        System.out.println();
        System.out.println("Распределение карт: " + gameFool);
    }

    private void chooseTrump(GameFool gameFool) {
        List<Card> cards = gameFool.getCards();
        int firstIndex = 0;
        gameFool.setTrump(cards.get(firstIndex));
        cards.remove(firstIndex);
        cards.add(cards.size(), gameFool.getTrump());
        System.out.println("\nTrump = " + gameFool.getTrump());
        System.out.print(gameFool);
    }

    void playTillTheEnd(GameFool gameFool, int amountOfPlayers) {
        CyclicList<Player> players = gameFool.getPlayers();

        int i = 0;
//        while (players.getCount() != 0) {
//
//
//        }
    } //final method

    Card attack(GameFool context, Player attackPlayer, Card trump) {
        Map<Player, Set<Card>> ratio = context.getRatio();
        int minNoTrump = Integer.MAX_VALUE;
        int minTrump = Integer.MAX_VALUE;
        Card cardNoTrump = null, cardTrump = null;

        for (Card card : ratio.get(attackPlayer)) {
            if (card.getRank().getCompareNumber() < minNoTrump && !isTrump(card, trump)) {
                minNoTrump = card.getRank().getCompareNumber();
                cardNoTrump = card;
            } else if (card.getRank().getCompareNumber() < minTrump) {
                minTrump = card.getRank().getCompareNumber();
                cardTrump = card;
            }
        }

        if (cardNoTrump == null) {
            return cardTrump;
        }

        return cardNoTrump;
    } //temporary

    boolean isTrump(Card card, Card trump) {
        return card.getSuit() == trump.getSuit();
    }

    Card beatOff(Card attackCard, List<Card> remainingCards) {
        int min = Integer.MAX_VALUE;
        Card minCard = null;

        for (Card card : remainingCards) {
            if (card.getSuit() == attackCard.getSuit() && card.getRank().getCompareNumber() > attackCard.getRank().getCompareNumber() && card.getRank().getCompareNumber() < min) {
                min = card.getRank().getCompareNumber();
                minCard = card;
            }
        }

        return minCard;
    }  //temporary

    List<Card> tossUp(List<Card> cardsOnTheTable, List<Card> cardsOfPlayer) {
        List<Card> cardsForTossUp = new ArrayList<>();
        List<Card> cardsForRemoving = new ArrayList<>();

        for (Card cardOnTheTable : cardsOnTheTable) {
            for (Card cardForTossUp : cardsOfPlayer) {
                if (cardOnTheTable.getRank().equals(cardForTossUp.getRank())) {
                    cardsForTossUp.add(cardForTossUp);
                    cardsForRemoving.add(cardForTossUp);
                }
            }
            cardsOfPlayer.removeAll(cardsForRemoving);
        }

        return cardsForTossUp;
    } //temporary

    void giveCards(GameFool gameFool) {
        System.out.print("\u001B[34m" + "\nGive cards:" + "\u001B[30m");
        Map<Player, Set<Card>> ratio = gameFool.getRatio();
        List<Card> cards = gameFool.getCards();

        for (Map.Entry<Player, Set<Card>> playerSetEntry : ratio.entrySet()) {
            int size = gameFool.NUMBER_OF_CARDS - playerSetEntry.getValue().size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    playerSetEntry.getValue().add(cards.get(0));
                    cards.remove(0);
                }
            }
        }
        System.out.print(gameFool);
    }
}

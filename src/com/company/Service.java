package com.company;

import java.util.*;

public class Service {

    public void start(GameFool gameFool, int amountOfPlayers, int amountOfCards) {
        initialization(gameFool, amountOfPlayers, amountOfCards);
        distributeCards(gameFool);
        chooseTrump(gameFool);
        playTillTheEnd(gameFool);
//        giveCards(gameFool);
    }

    public void initialization(GameFool gameFool, int amountOfPlayers, int amountOfCards) {
        System.out.print("\u001B[30m" + "Start our game !!!");
        initializationPlayers(gameFool, amountOfPlayers);
        List<Card> cards = initializationCards(gameFool, amountOfCards);
        shuffleCards(cards);
    }

    private void initializationPlayers(GameFool gameFool, int amount) {
        CyclicList<Player> players = gameFool.getPlayers();
        gameFool.setNumberOfPlayers(amount);
        for (int i = 1; i <= amount; i++) {
            players.add(new Player(i));
        }

        printPlayers(players, amount);
    }

    public void printPlayers(CyclicList<Player> list, int amount) {
        int counter = 0;

        for (Player player : list) {
            System.out.print(player);
            counter++;
            if (counter == amount) {
                break;
            }
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
        List<Card> cardsForRemoving = new ArrayList<>();

        int counter = 0;
        for (Player player : players) {
            Set<Card> cardsOfPlayer = new HashSet<>();
            for (int i = 0; i < gameFool.NUMBER_OF_CARDS; i++) {
                cardsOfPlayer.add(cards.get(i));
                cardsForRemoving.add(cards.get(i));
            }

            cards.removeAll(cardsForRemoving);
            ratio.put(player, cardsOfPlayer);
            counter++;
            if (counter == gameFool.getNumberOfPlayers()) {
                break;
            }

        }

        System.out.println();
        System.out.println("Распределение карт: " + gameFool);
    }

    private void chooseTrump(GameFool gameFool) {
        int firstIndex = 0;
        List<Card> cards = gameFool.getCards();
        gameFool.setTrump(cards.get(firstIndex));
        cards.remove(firstIndex);
        cards.add(cards.size(), gameFool.getTrump());

        System.out.println("\nTrump = " + gameFool.getTrump());
    }

    void playTillTheEnd(GameFool gameFool) {
        CyclicList<Player> players = gameFool.getPlayers();
        Map<Player, Set<Card>> ratio = gameFool.getRatio();
        List<Card> cardsOnTheTable = new ArrayList<>();
        List<Step> steps = gameFool.getSteps();

        System.out.println();
        System.out.println("\u001B[32m" + "PLAY !!!" + "\u001B[30m");

        boolean isMissTurn = false;
        int possibilityOfGame = gameFool.getNumberOfPlayers();
        Player playerTarget;
        Player playerAttack = null;
        for (Player player : players) {
            System.out.println("Итерация" + player.getName());
            if (isMissTurn) {
                isMissTurn = false;
                playerAttack = null;
                possibilityOfGame--;
                if (possibilityOfGame < 0) {
                    System.out.print("Игра не возможна!!!");
                    break;
                }
                continue;
            }

            if (playerAttack == null) {
                playerAttack = player;
                System.out.println("\u001B[34m" + "Player attack: " + playerAttack.getName() + "\u001B[30m");
                continue;
            }

            playerTarget = player;
            System.out.println("\u001B[34m" + "Player Target: " + playerTarget.getName() + "\u001B[30m");

            // - старт - //

            Card attackCard = attack(gameFool, playerAttack);
            cardsOnTheTable.add(attackCard);
            System.out.println("Атакующая карта: " + attackCard + " от Игрока " + playerAttack.getName());

            Card beatOffCard = beatOff(gameFool, attackCard, ratio.get(playerTarget));
            if (beatOffCard != null) {
                cardsOnTheTable.add(beatOffCard);
            } else {
                ratio.get(playerTarget).addAll(cardsOnTheTable);
                isMissTurn = true;
                continue;
            }

            System.out.println("Отбил: " + beatOffCard + " Игрок " + playerTarget.getName());

            Step step = new Step(playerTarget);
            HashMap<Card, Card> cardHashMap = new HashMap<>();
            cardHashMap.put(attackCard, beatOffCard);
            step.getList().put(playerAttack, cardHashMap);
            steps.add(step);

            System.out.println(step);

            break;

            //подкидывание
//            for (Player playerAttack : players) {
//                if (playerAttack == playerTarget) {
//                    continue;
//                }
//
//                List<Card> cardsForTossUp = tossUp(cardsOnTheTable, ratio.get(playerAttack));
//                if(cardsForTossUp.size() < 12 - cardsOnTheTable.size()){
//                    cardsOnTheTable.addAll(cardsForTossUp);
//                }
//            }

            //giveCards(gameFool);

        }

    } //final method

    void printAttack() {

    }

    Card attack(GameFool context, Player attackPlayer) {
        Map<Player, Set<Card>> ratio = context.getRatio();
        Card trump = context.getTrump();
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

    void beatOffAllCards(GameFool gameFool, Player target, List<Card> noBeatOffCards) {

    }

    Card beatOff(GameFool gamefool, Card attackCard, Set<Card> remainingCards) {
        int minNoTrump = Integer.MAX_VALUE;
        int minTrump = Integer.MAX_VALUE;
        Card cardNoTrump = null, cardTrump = null;

        System.out.println("ОТБИВАНИЕ !!! {");
        for (Card card : remainingCards) {
            System.out.println(card.getSuit() + " " + attackCard.getSuit());
            System.out.println(card.getRank().getCompareNumber() + " " + attackCard.getRank().getCompareNumber());
            System.out.println(card.getRank().getCompareNumber() + " " + minNoTrump);
            if ((card.getSuit() == attackCard.getSuit()) && (card.getRank().getCompareNumber() > attackCard.getRank().getCompareNumber()) && (card.getRank().getCompareNumber() < minNoTrump)) {
                minNoTrump = card.getRank().getCompareNumber();
                cardNoTrump = card;
            }
            if (isTrump(card, gamefool.getTrump()) && (card.getRank().getCompareNumber() < minTrump)) {
                minTrump = card.getRank().getCompareNumber();
                cardTrump = card;
            }
        }
        System.out.println("}");

        if (cardNoTrump == null) {
            return cardTrump;
        }

        return cardNoTrump;
    }  //temporary

    List<Card> tossUp(List<Card> cardsOnTheTable, Set<Card> cardsOfPlayer) {
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
        List<Card> cardsRemoving = new ArrayList<>();

        for (Map.Entry<Player, Set<Card>> playerSetEntry : ratio.entrySet()) {
            int size = gameFool.NUMBER_OF_CARDS - playerSetEntry.getValue().size();

            if (cards.size() - size < 0) {
                size = cards.size();
            }

            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    playerSetEntry.getValue().add(cards.get(i));
                    cardsRemoving.add(cards.get(i));
                }
                cards.removeAll(cardsRemoving);
            }
        }

        System.out.print(gameFool);
    }

}

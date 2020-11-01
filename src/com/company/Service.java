package com.company;

import java.util.*;

public class Service {

    public void start(GameFool gameFool, int amountOfPlayers, int amountOfCards) {
        initialization(gameFool, amountOfPlayers, amountOfCards);
        distributeCards(gameFool);
        chooseTrump(gameFool);
        playTillTheEnd(gameFool);
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

//        System.out.println(gameFool);
        return cards;
    }

    private void shuffleCards(List<Card> cards) {
        Collections.shuffle(cards);
//        System.out.println();
//        System.out.println("\u001B[34m" + "Shuffle cards: " + "\u001B[30m" + cards + "\n length = " + cards.size());
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
        System.out.println("\u001B[32m" + " PLAY !!! " + "\u001B[30m");

        Player playerTarget;
        Player playerAttack = null;
        boolean isMissTurn = false;
        int possibilityOfGame = gameFool.getNumberOfPlayers() + 1;

        int num = 0;

        for (Player player : players) {
            System.out.println("\u001B[32m" + "Итерация " + player.getName() + " " + isMissTurn + "\u001B[30m");
            if (isMissTurn) {
                System.out.println("\u001B[31m" + "Пропуск хода!!!");
                isMissTurn = false;
                playerAttack = null;
                possibilityOfGame--;
                if (possibilityOfGame < 0) {
                    System.out.print("\u001B[31m" + "Игра не возможна!!!");
                    break;
                }
                continue;
            }

            // - старт - //
            if (playerAttack == null) {
                playerAttack = player;
                System.out.println("\u001B[34m" + "Player Attack: " + playerAttack.getName() + "\u001B[30m");
                continue;
            }

            playerTarget = player;
            System.out.println("\u001B[34m" + "Player Target: " + playerTarget.getName() + "\u001B[30m");

            Card attackCard = attack(gameFool, playerAttack);
            cardsOnTheTable.add(attackCard);
            System.out.println("Атака: " + attackCard + " от Игрока " + playerAttack.getName());

            Card beatOffCard = beatOff(gameFool, playerTarget, attackCard);
            if (beatOffCard != null) {
                cardsOnTheTable.add(beatOffCard);
            } else {
                ratio.get(playerTarget).addAll(cardsOnTheTable);
                isMissTurn = true;
                System.out.println("\u001B[31m" + "Не отбил атакующую карту" + "\u001B[30m");
                continue;
            }
            System.out.println("Отбил: " + beatOffCard + " Игрок " + playerTarget.getName());


//            Step step = new Step(playerTarget);
//            HashMap<Card, Card> cardHashMap = new HashMap<>();
//            cardHashMap.put(attackCard, beatOffCard);
//            step.getList().put(playerAttack, cardHashMap);
//            steps.add(step);
//            System.out.println(step);

            // - подкидывание - //
            int numberForTossup = gameFool.NUMBER_OF_CARDS - 1;
            int size = 0;
            int count = 0;
            for (Player playerTossUp : players) {
                if (playerTossUp == playerTarget) {
                    continue;
                }

                List<Card> cardsForTossUp = tossUp(gameFool, playerTossUp, cardsOnTheTable);
                size = size + cardsForTossUp.size();

                if (cardsForTossUp.size() == 0) {
                    count++;
                    if (count == gameFool.getNumberOfPlayers() - 1) {
                        break;
                    }
                    continue;
                }

                if (size <= numberForTossup) {
                    cardsOnTheTable.addAll(cardsForTossUp);

                    List<Card> beatOffCards = beatOffAllCards(gameFool, playerTarget, cardsForTossUp);

                    if (beatOffCards == null) {
                        ratio.get(playerTarget).addAll(cardsOnTheTable);
                        System.out.println("Не отбился от всего(");
                        cardsOnTheTable.clear();
                        isMissTurn = true;
                        break;
                    }
                    cardsOnTheTable.addAll(beatOffCards);
                }
            }

            System.out.println();
            cardsOnTheTable.clear();
            if (isMissTurn) {
                playerAttack = null;
                isMissTurn = false;
            } else {
                playerAttack = playerTarget;
            }
            System.out.println(gameFool);
            //System.out.println("\u001B[34m" + "Player Attack: " + playerAttack.getName() + "\u001B[30m");
            num++;

            if (gameFool.getCards().size() != 0) {
                giveCards(gameFool);
            }

            if (num == 6) {
                break;
            }
        }
    }

    Card attack(GameFool context, Player attackPlayer) {
        Set<Card> cards = context.getRatio().get(attackPlayer);
        Card trump = context.getTrump();
        int minNoTrump = Integer.MAX_VALUE;
        int minTrump = Integer.MAX_VALUE;
        Card cardNoTrump = null, cardTrump = null;

        for (Card card : cards) {
            if (card.getRank().getCompareNumber() < minNoTrump && !isTrump(card, trump)) {
                minNoTrump = card.getRank().getCompareNumber();
                cardNoTrump = card;
            } else if (card.getRank().getCompareNumber() < minTrump) {
                minTrump = card.getRank().getCompareNumber();
                cardTrump = card;
            }
        }

        if (cardNoTrump == null) {
            cards.remove(cardTrump);
            return cardTrump;
        }

        cards.remove(cardNoTrump);
        return cardNoTrump;
    }

    boolean isTrump(Card card, Card trump) {
        return card.getSuit() == trump.getSuit();
    }

    List<Card> beatOffAllCards(GameFool gameFool, Player target, List<Card> noBeatOffCards) {
        Map<Player, Set<Card>> ratio = gameFool.getRatio();
        List<Card> beatOffCards = new ArrayList<>();
        List<Card> cardsForRemoving = new ArrayList<>();

        System.out.println("\u001B[32m" + "Подкидывание!!!" + "\u001B[30m");
        for (Card card : noBeatOffCards) {
            Card cardBeatOff = beatOff(gameFool, target, card);

            if (cardBeatOff == null) {
                ratio.get(target).addAll(beatOffCards);
                return null;
            }

            System.out.print("Аттака" + card + "\nОтбил" + cardBeatOff + "\n");
            cardsForRemoving.add(cardBeatOff);
            beatOffCards.add(cardBeatOff);
        }

        ratio.get(target).removeAll(cardsForRemoving);

        return beatOffCards;
    }

    Card beatOff(GameFool gamefool, Player target, Card attackCard) {
        Set<Card> remainingCards = gamefool.getRatio().get(target);
        int minNoTrump = Integer.MAX_VALUE;
        int minTrump = Integer.MAX_VALUE;
        Card cardNoTrump = null, cardTrump = null;

        for (Card card : remainingCards) {

            if ((card.getSuit() == attackCard.getSuit())
                    && (card.getRank().getCompareNumber() < minNoTrump)
                    && (card.getRank().getCompareNumber() > attackCard.getRank().getCompareNumber())
            ) {
                minNoTrump = card.getRank().getCompareNumber();
                cardNoTrump = card;
            }

            if (isTrump(card, gamefool.getTrump()) && (card.getRank().getCompareNumber() < minTrump)) {
                minTrump = card.getRank().getCompareNumber();
                cardTrump = card;
            }

        }

        if (cardNoTrump == null) {
            remainingCards.remove(cardTrump);
            return cardTrump;
        }

        remainingCards.remove(cardNoTrump);
        return cardNoTrump;
    }

    List<Card> tossUp(GameFool gameFool, Player attackPlayer, List<Card> cardsOnTheTable) {
        Set<Card> cardsOfPlayer = gameFool.getRatio().get(attackPlayer);
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
    }

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

package com.company;

import java.util.*;

public class Service {

    public void start(GameFool gameFool, int amountOfPlayers, int amountOfCards) {
        //System.out.print("\u001B[30m" + "Start our game !!!");
        initialization(gameFool, amountOfPlayers, amountOfCards);
        distributeCards(gameFool);
        chooseTrump(gameFool);
        playTillTheEnd(gameFool);
    }

    private void initialization(GameFool gameFool, int amountOfPlayers, int amountOfCards) {
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
        //Printer.printPlayers(players, amount);
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
        //System.out.println(gameFool);
        return cards;
    }

    private void shuffleCards(List<Card> cards) {
        Collections.shuffle(cards);
        //System.out.println();
        //System.out.println("\u001B[34m" + "Shuffle cards: " + "\u001B[30m" + cards + "\n length = " + cards.size());
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
        //System.out.println("Распределение карт: " + gameFool);
    }

    private void chooseTrump(GameFool gameFool) {
        int firstIndex = 0;
        List<Card> cards = gameFool.getCards();
        gameFool.setTrump(cards.get(firstIndex));
        cards.remove(firstIndex);
        cards.add(cards.size(), gameFool.getTrump());

        System.out.println("\nTrump = " + gameFool.getTrump());
    }

    private void playTillTheEnd(GameFool gameFool) {
        Map<Player, Set<Card>> ratio = gameFool.getRatio();
        CyclicList<Player> players = gameFool.getPlayers();
        List<Card> cardsOnTheTable = new ArrayList<>();

        Printer.printConditionOfGame("start");

        Player playerTarget;
        Player playerAttack = null;
        boolean isMissTurn = false;
        int possibilityOfGame = gameFool.getNumberOfPlayers() + 1;

        for (Player player : players) {
            // - игроки, которые закончили играть - //
            if (doesPlayerEndGame(gameFool, player)) {
                continue;
            }

            if (isEnd(gameFool)) {
                break;
            }

            // - пропуск хода - //
            if (isMissTurn) {
                isMissTurn = false;
                playerAttack = null;
                if (gameIsImpossible(possibilityOfGame--)) {
                    break;
                }
                continue;
            }

            // - старт - //
            if (playerAttack == null) {
                playerAttack = player;
                Printer.printConditionOfPlayers("attack", playerAttack);
                continue;
            }

            playerTarget = player;
            Printer.printConditionOfPlayers("target", playerTarget);

            Card attackCard = attack(gameFool, playerAttack);
            Printer.printProcessOfGame("attack", playerAttack, attackCard);

            cardsOnTheTable.add(attackCard);

            Card beatOffCard = beatOffOneCard(gameFool, playerTarget, attackCard);
            if (beatOffCard != null) {
                cardsOnTheTable.add(beatOffCard);
            } else {
                ratio.get(playerTarget).addAll(cardsOnTheTable);
                isMissTurn = true;
                Printer.printConditionOfGame("no beat off attack cards");
                continue;
            }
            Printer.printProcessOfGame("beat off", playerTarget, beatOffCard);

            setConditionOfPlayers(gameFool, playerAttack);
            setConditionOfPlayers(gameFool, playerTarget);

            if (doesPlayerEndGame(gameFool, playerTarget)) {
                continue;
            }

            // - подкидывание - //
            int countCardsForTossUp = 0;
            int countNoTossUpPlayers = 0;
            for (Player playerTossUp : players) {

                // - игроки, которые закончили играть - //
                if (doesPlayerEndGame(gameFool, playerTossUp)) {
                    continue;
                }

                // - если это игрок, которого атакуют - //
                if (playerTossUp == playerTarget) {
                    continue;
                }

                List<Card> cardsForTossUp = tossUp(gameFool, playerTossUp, cardsOnTheTable);
                countCardsForTossUp = countCardsForTossUp + cardsForTossUp.size();

                setConditionOfPlayers(gameFool, playerTossUp);

                // - если нечего подкидывать - //
                if (cardsForTossUp.size() == 0) {
                    countNoTossUpPlayers++;
                    if (countNoTossUpPlayers == gameFool.getNumberOfPlayers()) {
                        break;
                    }
                    continue;
                }

                countNoTossUpPlayers = 0;

                // - можно подкидывать или нет - //
                if (countCardsForTossUp <= gameFool.NUMBER_CARDS_FOR_TOSS_UP) {
                    cardsOnTheTable.addAll(cardsForTossUp);

                    List<Card> beatOffCards = beatOffAllCards(gameFool, playerTarget, cardsForTossUp);

                    setConditionOfPlayers(gameFool, playerTarget);
                    setConditionOfPlayers(gameFool, playerTossUp);

                    if (beatOffCards == null) {
                        ratio.get(playerTarget).addAll(cardsOnTheTable);
                        Printer.printConditionOfGame("no beat off");
                        cardsOnTheTable.clear();
                        isMissTurn = true;
                        break;
                    }

                    cardsOnTheTable.addAll(beatOffCards);
                }

                if (isEnd(gameFool)) {
                    break;
                }
            }

            // - раздача карт - //
            if (gameFool.getCards().size() != 0) {
                giveCards(gameFool);
            }

            setConditionOfPlayers(gameFool, playerAttack);
            setConditionOfPlayers(gameFool, playerTarget);

            // - для случая, если игрок не смог отбить подкидывающие карты - //
            if (isMissTurn) {
                playerAttack = null;
                isMissTurn = false;
            } else {
                playerAttack = playerTarget;
            }

            // - очистка и что получилось в итоге - //
            cardsOnTheTable.clear();
            System.out.println(gameFool);

            if (isEnd(gameFool)) {
                break;
            }
        }

    }

    private void addSteps(GameFool gameFool, Player playerAttack, Player playerTarget, Card attackCard, Card beatOffCard) {
        List<Step> steps = gameFool.getSteps();
        Step step = new Step(playerTarget);
        HashMap<Card, Card> cardHashMap = new HashMap<>();
        cardHashMap.put(attackCard, beatOffCard);
        step.getList().put(playerAttack, cardHashMap);
        steps.add(step);
        System.out.println(step);
    }

    private boolean doesPlayerEndGame(GameFool gameFool, Player player) {
        return gameFool.getWinPlayers().contains(player.getName());
    }

    private boolean gameIsImpossible(int number) {
        if (number < 0) {
            Printer.printConditionOfGame("possibility");
            return true;
        }
        return false;
    }

    private void setConditionOfPlayers(GameFool gameFool, Player player) {
        Map<Player, Set<Card>> ratio = gameFool.getRatio();

        if (ratio.get(player).size() == 0 && gameFool.getWinPlayer() == null) {
            gameFool.setWinPlayer(player);
            gameFool.setNumberOfPlayers(gameFool.getNumberOfPlayers() - 1);
            gameFool.getWinPlayers().add(player.getName());
            Printer.printConditionOfPlayers("winner", player);
        } else if (ratio.get(player).size() == 0 && !doesPlayerEndGame(gameFool, player) && gameFool.getWinPlayers().size() != gameFool.getNumberOfPlayers() - 1) {
            gameFool.setNumberOfPlayers(gameFool.getNumberOfPlayers() - 1);
            gameFool.getWinPlayers().add(player.getName());
            Printer.printConditionOfPlayers("post winner", player);
        } else if (ratio.get(player).size() == 0 && gameFool.getWinPlayer() == null && gameFool.getWinPlayers().size() == gameFool.getNumberOfPlayers() - 1) {
            gameFool.setFoolPlayer(player);
            gameFool.setNumberOfPlayers(gameFool.getNumberOfPlayers() - 1);
            gameFool.getWinPlayers().add(player.getName());
            Printer.printConditionOfPlayers("fool", player);
        }

    }

    private boolean isEnd(GameFool gameFool) {
        if (gameFool.getNumberOfPlayers() == 1 && gameFool.getCards().size() == 0) {
            Printer.printConditionOfGame("game is over");
            Printer.printConditionOfPlayers("winner", gameFool.getWinPlayer());
            return true;
        }
        return false;
    }

    private Card attack(GameFool gameFool, Player attackPlayer) {
        Set<Card> cards = gameFool.getRatio().get(attackPlayer);
        Card trump = gameFool.getTrump();
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

    private List<Card> beatOffAllCards(GameFool gameFool, Player target, List<Card> noBeatOffCards) {
        Map<Player, Set<Card>> ratio = gameFool.getRatio();
        List<Card> beatOffCards = new ArrayList<>();
        List<Card> cardsForRemoving = new ArrayList<>();

        System.out.println("\u001B[32m" + "Подкидывание!!!" + "\u001B[30m");
        for (Card card : noBeatOffCards) {
            Card cardBeatOff = beatOffOneCard(gameFool, target, card);

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

    private Card beatOffOneCard(GameFool gamefool, Player target, Card attackCard) {
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

    private boolean isTrump(Card card, Card trump) {
        return card.getSuit() == trump.getSuit();
    }

    private List<Card> tossUp(GameFool gameFool, Player attackPlayer, List<Card> cardsOnTheTable) {
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

    private void giveCards(GameFool gameFool) {
        //System.out.print("\u001B[34m" + "\nGive cards:" + "\u001B[30m");
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
        //System.out.print(gameFool);
    }

}

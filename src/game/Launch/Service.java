package game.Launch;

import game.Enum.CardSuit;
import game.Enum.Condition;
import game.Enum.RankOfCards;
import game.Objects.GameFool;
import game.Objects.Card;
import game.Objects.CyclicList;
import game.Objects.Player;
import game.Objects.Step;
import game.Printer.Printer;

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
        CyclicList<Player> players = gameFool.getPlayers();
        List<Card> cardsOnTheTable = new ArrayList<>();

        Printer.printConditionOfGame("start");

        for (Player player : players) {
            if (!firstStepInGame(gameFool, player, cardsOnTheTable)) {
                continue;
            }
            tossUpInGame(gameFool, cardsOnTheTable);
            missTurn(gameFool);

            if (gameFool.getCards().size() != 0) {
                giveCards(gameFool);
            }

            cardsOnTheTable.clear();
            System.out.println(gameFool);

            if (isContinueGame(gameFool, Condition.IMPOSSIBLE_GAME)) { // ?
                break;
            }

            if (isContinueGame(gameFool, Condition.END_GAME)) {
                break;
            }
        }
    }

    private boolean firstStepInGame(GameFool gameFool, Player player, List<Card> cardsOnTheTable) {
        if (gameFool.getPlayerAttack() == null) {
            gameFool.setPlayerAttack(player);
            return false;
        }
        Printer.printConditionOfPlayers("attack", gameFool.getPlayerAttack());

        gameFool.setPlayerTarget(player);
        Printer.printConditionOfPlayers("target", gameFool.getPlayerTarget());

        Card attackCard = attack(gameFool, gameFool.getPlayerAttack());
        Printer.printProcessOfGame("attack", gameFool.getPlayerAttack(), attackCard);

        cardsOnTheTable.add(attackCard);

        Card beatOffCard = beatOffOneCard(gameFool, gameFool.getPlayerTarget(), attackCard);
        if (beatOffCard != null) {
            cardsOnTheTable.add(beatOffCard);

            Printer.printProcessOfGame("beat off", gameFool.getPlayerTarget(), beatOffCard);
        } else {
            gameFool.getRatio().get(gameFool.getPlayerTarget()).addAll(cardsOnTheTable);
            gameFool.setMissTurn(true);
            missTurn(gameFool);

            Printer.printConditionOfGame("no beat off attack cards");
            return false;
        }

        return true;
    }

    private void tossUpInGame(GameFool gameFool, List<Card> cardsOnTheTable) {
        CyclicList<Player> players = gameFool.getPlayers();
        Map<Player, Set<Card>> ratio = gameFool.getRatio();

        int countCardsForTossUp = 0;
        int countNoTossUpPlayers = 0;
        for (Player playerTossUp : players) {
            if (playerTossUp == gameFool.getPlayerTarget()) {
                continue;
            }

            List<Card> cardsForTossUp = tossUp(gameFool, playerTossUp, cardsOnTheTable);
            countCardsForTossUp = countCardsForTossUp + cardsForTossUp.size();

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

                List<Card> beatOffCards = beatOffAllCards(gameFool, gameFool.getPlayerTarget(), cardsForTossUp);

                if (beatOffCards == null) {
                    ratio.get(gameFool.getPlayerTarget()).addAll(cardsOnTheTable);
                    Printer.printConditionOfGame("no beat off");

                    cardsOnTheTable.clear();
                    gameFool.setMissTurn(true);
                    break;
                }
                cardsOnTheTable.addAll(beatOffCards);
            }

            if (isContinueGame(gameFool, Condition.END_GAME)) {
                break;
            }
        }
    }

    private boolean isContinueGame(GameFool gameFool, Condition condition) {
        switch (condition) {
            case IMPOSSIBLE_GAME:
                return false;
            case END_GAME:
                if (gameFool.isEnd()) {
                    return true;
                }
                break;
        }
        return false;
    }

    private void missTurn(GameFool gameFool) {
        if (gameFool.isMissTurn()) {
            gameFool.setPlayerAttack(null);
            gameFool.setMissTurn(false);
            //if (isImpossibleGame(possibilityOfGame--)) {
            //    return false;
            //}
        } else {
            gameFool.setPlayerAttack(gameFool.getPlayerTarget());
        }
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

    private void addSteps(GameFool gameFool, Player playerAttack, Player playerTarget, Card attackCard, Card beatOffCard) {
        List<Step> steps = gameFool.getSteps();
        Step step = new Step(playerTarget);
        HashMap<Card, Card> cardHashMap = new HashMap<>();
        cardHashMap.put(attackCard, beatOffCard);
        step.getList().put(playerAttack, cardHashMap);
        steps.add(step);
        System.out.println(step);
    }

    private boolean isImpossibleGame(int number) {
        if (number < 0) {
            Printer.printConditionOfGame("possibility");
            return true;
        }
        return false;
    }

    private void setConditionOfPlayers(GameFool gameFool, Player player) {
        Map<Player, Set<Card>> ratio = gameFool.getRatio();

//        if (ratio.get(player).size() == 0 && gameFool.getPlayerWin() == null) {
//            gameFool.setPlayerWin(player);
//            gameFool.setNumberOfPlayers(gameFool.getNumberOfPlayers() - 1);
//            gameFool.getWinPlayers().add(player.getName());
//            Printer.printConditionOfPlayers("winner", player);
//        } else if (ratio.get(player).size() == 0 && !didPlayerEndGame(gameFool, player) && gameFool.getWinPlayers().size() != gameFool.getNumberOfPlayers() - 1) {
//            gameFool.setNumberOfPlayers(gameFool.getNumberOfPlayers() - 1);
//            gameFool.getWinPlayers().add(player.getName());
//            Printer.printConditionOfPlayers("post winner", player);
//        } else if (ratio.get(player).size() == 0 && gameFool.getPlayerWin() == null && gameFool.getWinPlayers().size() == gameFool.getNumberOfPlayers() - 1) {
//            gameFool.setPlayerFool(player);
//            gameFool.setNumberOfPlayers(gameFool.getNumberOfPlayers() - 1);
//            gameFool.getWinPlayers().add(player.getName());
//            Printer.printConditionOfPlayers("fool", player);
//        }


        if (ratio.get(player).size() == 0 && gameFool.getPlayerWin() != null) {
            gameFool.getPlayers().remove(player);
            Printer.printConditionOfPlayers("winner", player);
        } else if (ratio.get(player).size() == 0) {
            gameFool.getPlayers().remove(player);
            Printer.printConditionOfPlayers("post winner", player);
        } else if (ratio.get(player).size() == 0 && gameFool.isEnd()) {
            gameFool.getPlayers().remove(player);
            gameFool.setPlayerFool(player);
            Printer.printConditionOfPlayers("fool", player);
        }
    }

}

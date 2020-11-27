package game.Launch;

import game.Enum.CardSuit;
import game.Enum.Condition;
import game.Enum.RankOfCards;
import game.Objects.GameFool;
import game.Objects.Card;
import game.Objects.CyclicList;
import game.Objects.Player;
import game.Printer.Printer;

import java.util.*;

public class Service {

    public void start(GameFool gameFool, int amountOfPlayers, int amountOfCards) {
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
        for (int i = 1; i <= amount; i++) {
            players.add(new Player(i));
        }
        Printer.printPlayers(players, amount);
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
            if (counter == players.getSize()) {
                break;
            }

        }
        final String BLUE = "\u001B[34m";
        System.out.println(BLUE + "\nDistribution of cards: " + gameFool);
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

        Printer.printConditionOfGame("start");

        for (Player player : players) {
            if (checkCondition(gameFool, player, Condition.NEXT_STEP)) {
                if (gameFool.isEnd()) {
                    break;
                }
                continue;
            }

            if (checkCondition(gameFool, player, Condition.TOSS_UP)) {
                if (gameFool.isEnd()) {
                    break;
                }
            }

            if (gameFool.getCards().size() != 0) {
                Operation.giveCards(gameFool);
            }

            missTurn(gameFool);
            gameFool.getCardsOnTheTable().clear();
            System.out.println(gameFool);
        }
    }

    private boolean checkCondition(GameFool gameFool, Player player, Condition condition) {
        switch (condition) {
            case NEXT_STEP:
                return firstAttackInGame(gameFool, player);
            case TOSS_UP:
                if (gameFool.getPlayers().getSize() == 1) {
                    return true;
                }
                return tossUpInGame(gameFool);
        }
        return false;
    }

    private boolean firstAttackInGame(GameFool gameFool, Player player) {
        try {

            if (gameFool.getPlayerAttack() == null) {
                gameFool.setPlayerAttack(player);
                return true;
            }

            Printer.printConditionOfPlayers("attack", gameFool.getPlayerAttack());
            gameFool.setPlayerTarget(player);
            Printer.printConditionOfPlayers("target", gameFool.getPlayerTarget());

            Card attackCard = Operation.attack(gameFool, gameFool.getPlayerAttack());
            Printer.printProcessOfGame("attack", gameFool.getPlayerAttack(), attackCard);

            if (Operation.isFinalStepForPlayer(gameFool, gameFool.getPlayerAttack())) {
                gameFool.getPlayers().remove(gameFool.getPlayerAttack());
                gameFool.setPlayerAttack(null);
            }

            gameFool.getCardsOnTheTable().add(attackCard);

            Card beatOffCard = Operation.beatOffOneCard(gameFool, gameFool.getPlayerTarget(), attackCard);
            //Operation.addSteps(gameFool, gameFool.getPlayerAttack(), gameFool.getPlayerTarget(), attackCard, beatOffCard);
            if (beatOffCard != null) {
                gameFool.getCardsOnTheTable().add(beatOffCard);
                Printer.printProcessOfGame("beat off", gameFool.getPlayerTarget(), beatOffCard);
            } else {
                gameFool.getRatio().get(gameFool.getPlayerTarget()).addAll(gameFool.getCardsOnTheTable());
                gameFool.setMissTurn(true);
                missTurn(gameFool);
                Printer.printConditionOfGame("no beat off attack cards");
                return true;
            }

            if (Operation.isFinalStepForPlayer(gameFool, gameFool.getPlayerTarget())) {
                gameFool.getPlayers().remove(gameFool.getPlayerTarget());
                System.out.println(gameFool);
                return true;
            }

            if (gameFool.getCards().size() == 0 && gameFool.getPlayers().getSize() == 1) {
                return true;
            }

        } catch (Exception e) {
            System.err.println("Error in method firstAttack() : " + e.getMessage());
            System.out.println(e.toString());
        }

        return false;
    }

    private boolean tossUpInGame(GameFool gameFool) {
        CyclicList<Player> players = gameFool.getPlayers();

        int countCardsForTossUp = 0;
        int countNoTossUpPlayers = 0;

        Printer.printConditionOfGame("toss up");
        try {
            for (Player playerTossUp : players) {
                if (playerTossUp.getName() == gameFool.getPlayerTarget().getName()) {
                    if (gameFool.getCards().size() == 0 && gameFool.getPlayers().getSize() == 1) {
                        return true;
                    }
                    continue;
                }

                int i = beatOff(gameFool, playerTossUp, countCardsForTossUp);

                if (i > 0) {
                    countNoTossUpPlayers = 0;
                    countCardsForTossUp += i;
                    if (Operation.isFinalStepForPlayer(gameFool, playerTossUp)) {
                        gameFool.getPlayers().remove(playerTossUp);
                    }
                } else if (i == -1) {
                    countNoTossUpPlayers++;
                    if (gameFool.getCards().size() == 0 && gameFool.getPlayers().getSize() == 1) {
                        return true;
                    }
                    if (Operation.isFinalStepForPlayer(gameFool, playerTossUp)) {
                        gameFool.getPlayers().remove(playerTossUp);
                    }
                    if (countNoTossUpPlayers == players.getSize() + 1) {
                        return false;
                    }

                } else {
                    if (Operation.isFinalStepForPlayer(gameFool, playerTossUp)) {
                        gameFool.getPlayers().remove(playerTossUp);
                    }
                    return false;
                }

            }
        } catch (Exception e) {
            System.err.println("Error in method tossUpInGame() : " + e.getMessage());
        }
        return false;
    }

    private int beatOff(GameFool gameFool, Player attackPlayer, int countCardsForTossUp) {
        Set<Card> cardsOfPlayer = gameFool.getRatio().get(attackPlayer);
        List<Card> cardsForRemoving = new ArrayList<>();
        List<Card> beatOffCards = new ArrayList<>();

        int number = countCardsForTossUp;
        int limit = gameFool.NUMBER_CARDS_FOR_TOSS_UP;

        for (Card cardOnTheTable : gameFool.getCardsOnTheTable()) {
            for (Card cardForTossUp : cardsOfPlayer) {
                if (cardOnTheTable.getRank().equals(cardForTossUp.getRank()) && number <= limit) {
                    System.out.print("\nAttack" + cardForTossUp + " from Player " + attackPlayer.getName());
                    Card cardBeatOff = Operation.beatOffOneCard(gameFool, gameFool.getPlayerTarget(), cardForTossUp);

                    if (Operation.isFinalStepForPlayer(gameFool, gameFool.getPlayerTarget())) {
                        gameFool.getPlayers().remove(gameFool.getPlayerTarget());
                        gameFool.setPlayerTarget(null);
                        System.out.println("\nBeat Off" + cardBeatOff + "\n");
                        cardsForRemoving.add(cardForTossUp);
                        cardsOfPlayer.removeAll(cardsForRemoving);
                        return -2;
                    }

                    if (cardBeatOff == null) {
                        Printer.printConditionOfGame("no beat off");
                        System.out.println(gameFool);

                        beatOffCards.add(cardForTossUp);
                        gameFool.getRatio().get(gameFool.getPlayerTarget()).addAll(beatOffCards);
                        gameFool.getRatio().get(gameFool.getPlayerTarget()).addAll(gameFool.getCardsOnTheTable());
                        gameFool.getCardsOnTheTable().clear();
                        gameFool.setMissTurn(true);

                        cardsForRemoving.add(cardForTossUp);
                        cardsOfPlayer.removeAll(cardsForRemoving);

                        return -1;
                    }

                    beatOffCards.add(cardBeatOff);
                    beatOffCards.add(cardForTossUp);

                    System.out.println("\nBeat Off" + cardBeatOff + "\n");
                    cardsForRemoving.add(cardForTossUp);
                    number++;

                }
            }
            cardsOfPlayer.removeAll(cardsForRemoving);
        }

        return countCardsForTossUp - number;
    }

    private void missTurn(GameFool gameFool) {
        if (gameFool.isMissTurn()) {
            gameFool.setPlayerAttack(null);
            gameFool.setMissTurn(false);
        } else {
            if (gameFool.getPlayerTarget() != null) {
                gameFool.setPlayerAttack(gameFool.getPlayerTarget());
            } else {
                gameFool.setPlayerAttack(null);
                gameFool.setMissTurn(false);
            }
        }
    }

}

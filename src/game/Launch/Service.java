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
            if (counter == players.getSize()) {
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
            if (firstStepInGame(gameFool, player, cardsOnTheTable)) {
                if (gameFool.isEnd()) {
                    break;
                }
                continue;
            }
            if (tossUpInGame(gameFool, cardsOnTheTable) == -1) {
                break;
            }

            missTurn(gameFool);

            if (gameFool.getCards().size() != 0) {
                Operation.giveCards(gameFool);
            }

            cardsOnTheTable.clear();
            System.out.println(gameFool);

            if (gameFool.isEnd()) {
                break;
            }
        }
    }

    private boolean firstStepInGame(GameFool gameFool, Player player, List<Card> cardsOnTheTable) {
        if (gameFool.getPlayerAttack() == null) {
            gameFool.setPlayerAttack(player);
            return true;
        }
        Printer.printConditionOfPlayers("attack", gameFool.getPlayerAttack());

        gameFool.setPlayerTarget(player);
        Printer.printConditionOfPlayers("target", gameFool.getPlayerTarget());

        Card attackCard = Operation.attack(gameFool, gameFool.getPlayerAttack());
        Printer.printProcessOfGame("attack", gameFool.getPlayerAttack(), attackCard);
        Operation.isFinalStepForPlayer(gameFool, gameFool.getPlayerAttack());

        cardsOnTheTable.add(attackCard);

        Card beatOffCard = Operation.beatOffOneCard(gameFool, gameFool.getPlayerTarget(), attackCard);
        if (beatOffCard != null) {
            cardsOnTheTable.add(beatOffCard);
            Printer.printProcessOfGame("beat off", gameFool.getPlayerTarget(), beatOffCard);
        } else {
            gameFool.getRatio().get(gameFool.getPlayerTarget()).addAll(cardsOnTheTable);
            gameFool.setMissTurn(true);
            missTurn(gameFool);
            Printer.printConditionOfGame("no beat off attack cards");
            return true;
        }

        if (gameFool.isEnd()) {
            return true;
        }

        return Operation.isFinalStepForPlayer(gameFool, gameFool.getPlayerTarget());
    }

    private int tossUpInGame(GameFool gameFool, List<Card> cardsOnTheTable) {
        CyclicList<Player> players = gameFool.getPlayers();
        Map<Player, Set<Card>> ratio = gameFool.getRatio();

        int countCardsForTossUp = 0;
        int countNoTossUpPlayers = 0;
        for (Player playerTossUp : players) {
            if (playerTossUp.getName() == gameFool.getPlayerTarget().getName()) {
                if (players.getSize() == 1) {
                    return -1;
                }
                continue;
            }

            List<Card> cardsForTossUp = Operation.tossUp(gameFool, playerTossUp, cardsOnTheTable);
            countCardsForTossUp = countCardsForTossUp + cardsForTossUp.size();
            Operation.isFinalStepForPlayer(gameFool, playerTossUp);

            // - если нечего подкидывать - //
            if (cardsForTossUp.size() == 0) {
                countNoTossUpPlayers++;
                if (countNoTossUpPlayers == players.getSize() + 1) {
                    break;
                }
                continue;
            }

            countNoTossUpPlayers = 0;

            // - можно подкидывать или нет - //
            if (countCardsForTossUp <= gameFool.NUMBER_CARDS_FOR_TOSS_UP) {
                cardsOnTheTable.addAll(cardsForTossUp);

                List<Card> beatOffCards = Operation.beatOffAllCards(gameFool, gameFool.getPlayerTarget(), cardsForTossUp);

                if (beatOffCards == null) {
                    ratio.get(gameFool.getPlayerTarget()).addAll(cardsOnTheTable);
                    Printer.printConditionOfGame("no beat off");
                    cardsOnTheTable.clear();
                    gameFool.setMissTurn(true);
                    break;
                }

                cardsOnTheTable.addAll(beatOffCards);

            }

            if (gameFool.isEnd()) {
                return -1;
            }

        }
        return 1;
    }

    private void missTurn(GameFool gameFool) {
        if (gameFool.isMissTurn()) {
            gameFool.setPlayerAttack(null);
            gameFool.setMissTurn(false);
        } else {
            gameFool.setPlayerAttack(gameFool.getPlayerTarget());
        }
    }

    private boolean isImpossibleGame(int number) {
        if (number < 0) {
            Printer.printConditionOfGame("possibility");
            return true;
        }
        return false;
    }

}

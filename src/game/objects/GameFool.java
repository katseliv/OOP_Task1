package game.objects;

import game.printer.Printer;

import java.util.*;

public class GameFool {
    private Card trump;
    private boolean isMissTurn = false;

    private Player playerAttack = null;
    private Player playerTarget = null;
    private Player playerFool;

    public final int NUMBER_OF_CARDS = 6;
    public final int NUMBER_CARDS_FOR_TOSS_UP = NUMBER_OF_CARDS - 1;
    private final CyclicList<Player> players = new CyclicList<>();
    private final Map<Player, Set<Card>> ratio = new HashMap<>();
    private final List<Card> cardsOnTheTable = new ArrayList<>();
    private final List<Card> cards = new ArrayList<>();
    private final List<Step> steps = new ArrayList<>();

    public CyclicList<Player> getPlayers() {
        return players;
    }

    public List<Card> getCardsOnTheTable() {
        return cardsOnTheTable;
    }

    public List<Card> getCards() {
        return cards;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public Map<Player, Set<Card>> getRatio() {
        return ratio;
    }

    public Card getTrump() {
        return trump;
    }

    public void setTrump(Card trump) {
        this.trump = trump;
    }

    public Player getPlayerAttack() {
        return playerAttack;
    }

    public void setPlayerAttack(Player playerAttack) {
        this.playerAttack = playerAttack;
    }

    public Player getPlayerTarget() {
        return playerTarget;
    }

    public void setPlayerTarget(Player playerTarget) {
        this.playerTarget = playerTarget;
    }

    public void setPlayerFool(Player playerFool) {
        this.playerFool = playerFool;
    }

    public boolean isMissTurn() {
        return isMissTurn;
    }

    public void setMissTurn(boolean missTurn) {
        isMissTurn = missTurn;
    }

    public boolean isEnd() {
        if (cards.size() == 0 && players.getSize() == 1) {
            Printer.printConditionOfGame("game is over");
            for (Player player : players) {
                setPlayerFool(player);
                break;
            }
            Printer.printConditionOfPlayers("fool", playerFool);
            System.out.println(toString());
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        final String BLACK = "\u001B[30m";
        final String PURPLE = "\u001B[35m";

        return BLACK + PURPLE + " \nGameFool {"
                + "\nplayers = " + players.getSize()
                + ",\ncards = " + cards + PURPLE
                + "\nlength = " + cards.size()
                + ", steps = " + steps
                + ", ratio = " + ratio + '}';
    }

}

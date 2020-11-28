package game.services;

import game.exceptions.OutOfLimitException;
import game.enums.CardSuit;
import game.enums.Condition;
import game.enums.RankOfCards;
import game.objects.*;
import game.printer.Printer;

import java.util.*;

public class Service {

    public void start(GameFool gameFool, int amountOfPlayers, int amountOfCards) throws OutOfLimitException {
        initialization(gameFool, amountOfPlayers, amountOfCards);
        chooseTrump(gameFool);
        distributeCards(gameFool);
        playTillTheEnd(gameFool);
    }

    private void initialization(GameFool gameFool, int amountOfPlayers, int amountOfCards) throws OutOfLimitException {
        if (amountOfPlayers <= 6 && amountOfCards == 36) {
            initializationPlayers(gameFool, amountOfPlayers);
            List<Card> cards = initializationCards(gameFool, amountOfCards);
            shuffleCards(cards);
        } else if (amountOfPlayers <= 8 && amountOfCards == 52) {
            initializationPlayers(gameFool, amountOfPlayers);
            List<Card> cards = initializationCards(gameFool, amountOfCards);
            shuffleCards(cards);
        } else {
            throw new OutOfLimitException("Too much players");
        }

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
        return cards;
    }

    private void shuffleCards(List<Card> cards) {
        Collections.shuffle(cards);
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
            if (isFinalStepForPlayer(gameFool, player)) {
                continue;
            }

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
                giveCards(gameFool);
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
        Map<Player, Set<Card>> ratio = gameFool.getRatio();

        try {
            if (gameFool.getPlayerAttack() == null) {
                gameFool.setPlayerAttack(player);
                return true;
            }
            Printer.printConditionOfPlayers("attack", gameFool.getPlayerAttack());
            gameFool.setPlayerTarget(player);
            Printer.printConditionOfPlayers("target", gameFool.getPlayerTarget());

            Card attackCard = attack(gameFool, gameFool.getPlayerAttack());
            Printer.printProcessOfGame("attack", gameFool.getPlayerAttack(), attackCard);

            if (isFinalStepForPlayer(gameFool, gameFool.getPlayerAttack())) {
                gameFool.getPlayers().remove(gameFool.getPlayerAttack());
                gameFool.setPlayerAttack(null);
            }

            gameFool.getCardsOnTheTable().add(attackCard);

            Card beatOffCard = beatOffOneCard(gameFool, gameFool.getPlayerTarget(), attackCard);
            //Operation.addSteps(gameFool, gameFool.getPlayerAttack(), gameFool.getPlayerTarget(), attackCard, beatOffCard);
            if (beatOffCard != null) {
                gameFool.getCardsOnTheTable().add(beatOffCard);
                Printer.printProcessOfGame("beat off", gameFool.getPlayerTarget(), beatOffCard);
            } else {
                ratio.get(gameFool.getPlayerTarget()).addAll(gameFool.getCardsOnTheTable());
                gameFool.setMissTurn(true);
                missTurn(gameFool);
                Printer.printConditionOfGame("no beat off attack cards");
                return true;
            }

            if (isFinalStepForPlayer(gameFool, gameFool.getPlayerTarget())) {
                gameFool.getPlayers().remove(gameFool.getPlayerTarget());
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

                int i = beatOffInGame(gameFool, playerTossUp, countCardsForTossUp);

                if (i > 0) {
                    countNoTossUpPlayers = 0;
                    countCardsForTossUp += i;
                    if (isFinalStepForPlayer(gameFool, playerTossUp)) {
                        gameFool.getPlayers().remove(playerTossUp);
                    }
                } else if (i == -1) {
                    countNoTossUpPlayers++;
                    if (gameFool.getCards().size() == 0 && gameFool.getPlayers().getSize() == 1) {
                        return true;
                    }
                    if (isFinalStepForPlayer(gameFool, playerTossUp)) {
                        gameFool.getPlayers().remove(playerTossUp);
                        //gameFool.setPlayerAttack(null);
                    }
                    if (countNoTossUpPlayers == players.getSize() + 1) {
                        return false;
                    }

                } else {
                    if (isFinalStepForPlayer(gameFool, playerTossUp)) {
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

    private int beatOffInGame(GameFool gameFool, Player attackPlayer, int countCardsForTossUp) {
        Set<Card> cardsOfPlayer = gameFool.getRatio().get(attackPlayer);
        List<Card> cardsForRemoving = new ArrayList<>();
        List<Card> beatOffCards = new ArrayList<>();

        int number = countCardsForTossUp;
        int limit = gameFool.NUMBER_CARDS_FOR_TOSS_UP;

        for (Card cardOnTheTable : gameFool.getCardsOnTheTable()) {
            for (Card cardForTossUp : cardsOfPlayer) {
                if (cardOnTheTable.getRank().equals(cardForTossUp.getRank()) && number <= limit) {
                    System.out.print("\nAttack" + cardForTossUp + " from Player " + attackPlayer.getName());
                    Card cardBeatOff = beatOffOneCard(gameFool, gameFool.getPlayerTarget(), cardForTossUp);

                    //Operation.addSteps(gameFool, attackPlayer, gameFool.getPlayerTarget(), cardForTossUp, cardBeatOff);
                    if (isFinalStepForPlayer(gameFool, gameFool.getPlayerTarget())) {
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

    public Card attack(GameFool gameFool, Player attackPlayer) {
        Set<Card> cards = gameFool.getRatio().get(attackPlayer);
        Card trump = gameFool.getTrump();
        int minNoTrump = Integer.MAX_VALUE;
        int minTrump = Integer.MAX_VALUE;
        Card cardNoTrump = null, cardTrump = null;

        for (Card card : cards) {
            if (card.getRank().ordinal() < minNoTrump && !isTrump(card, trump)) {
                minNoTrump = card.getRank().ordinal();
                cardNoTrump = card;
            } else if (card.getRank().ordinal() < minTrump) {
                minTrump = card.getRank().ordinal();
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

    public List<Card> beatOffAllCards(GameFool gameFool, Player target, List<Card> noBeatOffCards) {
        Map<Player, Set<Card>> ratio = gameFool.getRatio();
        List<Card> beatOffCards = new ArrayList<>();
        List<Card> cardsForRemoving = new ArrayList<>();

        System.out.println("\u001B[32m" + "\nTOSS UP!!!" + "\u001B[30m");
        for (Card card : noBeatOffCards) {
            Card cardBeatOff = beatOffOneCard(gameFool, target, card);

            System.out.print("\nAttack" + card);
            if (cardBeatOff == null) {
                ratio.get(target).addAll(beatOffCards);
                return null;
            }
            System.out.println("\nBeat Off" + cardBeatOff + "\n");

            cardsForRemoving.add(cardBeatOff);
            beatOffCards.add(cardBeatOff);

            if (isFinalStepForPlayer(gameFool, target)) {
                ratio.get(target).removeAll(cardsForRemoving);
                gameFool.getPlayers().remove(target);
                return beatOffCards;
            }
        }

        ratio.get(target).removeAll(cardsForRemoving);

        return beatOffCards;
    }

    public Card beatOffOneCard(GameFool gamefool, Player target, Card attackCard) {
        Set<Card> remainingCards = gamefool.getRatio().get(target);
        int minNoTrump = Integer.MAX_VALUE;
        int minTrump = Integer.MAX_VALUE;
        Card cardNoTrump = null, cardTrump = null;

        for (Card card : remainingCards) {

            if ((card.getSuit() == attackCard.getSuit())
                    && (card.getRank().ordinal() < minNoTrump)
                    && (card.getRank().ordinal() > attackCard.getRank().ordinal())
            ) {
                minNoTrump = card.getRank().ordinal();
                cardNoTrump = card;
            }

            if (isTrump(card, gamefool.getTrump())
                    && !isTrump(attackCard, gamefool.getTrump())
                    && (card.getRank().ordinal() < minTrump)
            ) {
                minTrump = card.getRank().ordinal();
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

    public boolean isTrump(Card card, Card trump) {
        return card.getSuit() == trump.getSuit();
    }

    public List<Card> tossUpCards(GameFool gameFool, Player attackPlayer, List<Card> cardsOnTheTable, int countCardsForTossUp) {
        Set<Card> cardsOfPlayer = gameFool.getRatio().get(attackPlayer);
        List<Card> cardsForTossUp = new ArrayList<>();
        List<Card> cardsForRemoving = new ArrayList<>();
        int number = countCardsForTossUp;
        int limit = gameFool.NUMBER_CARDS_FOR_TOSS_UP;

        for (Card cardOnTheTable : cardsOnTheTable) {
            for (Card cardForTossUp : cardsOfPlayer) {
                if (cardOnTheTable.getRank().equals(cardForTossUp.getRank())) {
                    if (number <= limit) {
                        cardsForTossUp.add(cardForTossUp);
                        cardsForRemoving.add(cardForTossUp);
                        number++;
                    }
                }
            }
            cardsOfPlayer.removeAll(cardsForRemoving);
        }

        return cardsForTossUp;
    }

    public void giveCards(GameFool gameFool) {
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

    public void addSteps(GameFool gameFool, Player playerAttack, Player playerTarget, Card attackCard, Card beatOffCard) {
        List<Step> steps = gameFool.getSteps();
        Step step = new Step(playerTarget);
        HashMap<Card, Card> cardHashMap = new HashMap<>();
        cardHashMap.put(attackCard, beatOffCard);
        step.getList().put(playerAttack, cardHashMap);
        steps.add(step);
        System.out.println(step);
    }

    public boolean isFinalStepForPlayer(GameFool gameFool, Player player) {
        Map<Player, Set<Card>> ratio = gameFool.getRatio();

        if (ratio.get(player).size() == 0 && gameFool.getCards().size() == 0 && gameFool.getPlayers().getSize() != 1) {
            Printer.printConditionOfPlayers("winner", player);
            return true;
        }

        return false;
    }

}

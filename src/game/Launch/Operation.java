package game.Launch;

import game.Objects.Card;
import game.Objects.GameFool;
import game.Objects.Player;
import game.Objects.Step;
import game.Printer.Printer;

import java.util.*;

public class Operation {

    public static Card attack(GameFool gameFool, Player attackPlayer) {
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

    public static List<Card> beatOffAllCards(GameFool gameFool, Player target, List<Card> noBeatOffCards) {
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

    public static Card beatOffOneCard(GameFool gamefool, Player target, Card attackCard) {
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

            if (isTrump(card, gamefool.getTrump())
                    && !isTrump(attackCard, gamefool.getTrump())
                    && (card.getRank().getCompareNumber() < minTrump)
            ) {
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

    public static boolean isTrump(Card card, Card trump) {
        return card.getSuit() == trump.getSuit();
    }

    public static List<Card> tossUpCards(GameFool gameFool, Player attackPlayer, List<Card> cardsOnTheTable, int countCardsForTossUp) {
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

    public static void giveCards(GameFool gameFool) {
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

    public static void addSteps(GameFool gameFool, Player playerAttack, Player playerTarget, Card attackCard, Card beatOffCard) {
        List<Step> steps = gameFool.getSteps();
        Step step = new Step(playerTarget);
        HashMap<Card, Card> cardHashMap = new HashMap<>();
        cardHashMap.put(attackCard, beatOffCard);
        step.getList().put(playerAttack, cardHashMap);
        steps.add(step);
        System.out.println(step);
    }

    public static boolean isFinalStepForPlayer(GameFool gameFool, Player player) {
        Map<Player, Set<Card>> ratio = gameFool.getRatio();

        if (ratio.get(player).size() == 0 && gameFool.getCards().size() == 0 && gameFool.getPlayerWin() == null) {
            Printer.printConditionOfPlayers("winner", player);
            gameFool.setPlayerWin(player);
            return true;
        } else if (ratio.get(player).size() == 0 && gameFool.getCards().size() == 0 && gameFool.getPlayers().getSize() != 1) {
            Printer.printConditionOfPlayers("post winner", player);
            return true;
        }

        return false;
    }

}

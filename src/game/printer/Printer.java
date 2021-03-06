package game.printer;

import game.objects.CyclicList;
import game.objects.Card;
import game.objects.Player;

public class Printer {

    public static void printPlayers(CyclicList<Player> list, int amount) {
        int counter = 0;

        for (Player player : list) {
            System.out.print(player);
            counter++;
            if (counter == amount) {
                break;
            }
        }

        System.out.println();
    }

    public static void printConditionOfGame(String string) {
        final String BLACK = "\u001B[30m";
        final String RED = "\u001B[31m";
        final String GREEN = "\u001B[32m";

        System.out.println();
        switch (string) {
            case "start":
                System.out.println(GREEN + "    PLAY !!!    " + BLACK);
                break;
            case "toss up":
                System.out.println(GREEN + "    TOSS UP!!!    " + BLACK);
                break;
            case "no beat off attack cards":
                System.out.println(RED + "Player couldn't beat off attack cards" + BLACK);
                break;
            case "no beat off":
                System.out.println(RED + "Player couldn't beat off all cards" + BLACK);
                break;
            case "game is over":
                System.out.println(RED + " GAME IS OVER!!! " + BLACK);
                break;
        }
    }

    public static void printConditionOfPlayers(String string, Player player) {
        final String BLACK = "\u001B[30m";
        final String RED = "\u001B[31m";
        final String GREEN = "\u001B[32m";
        final String BLUE = "\u001B[34m";

        switch (string) {
            case "attack":
                System.out.println(BLUE + "\nPlayer Attack: " + player.getName() + BLACK);
                break;
            case "target":
                System.out.println(BLUE + "Player Target: " + player.getName() + BLACK);
                break;
            case "winner":
                System.out.println(GREEN + "\nPlayer " + player.getName() + " finished Game" + BLACK + "\n");
                break;
            case "fool":
                System.out.println(RED + "Fool is Player " + player.getName() + BLACK);
                break;
        }
    }

    public static void printProcessOfGame(String string, Player player, Card card) {
        switch (string) {
            case "attack":
                System.out.println("\nAttack: " + card + " from Player " + player.getName());
                break;
            case "beat off":
                System.out.println("Beat off: " + card + " Player " + player.getName() + "\n");
                break;
        }
    }

}

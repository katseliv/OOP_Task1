package game.Launch;

import game.Objects.CyclicList;
import game.Objects.GameFool;
import game.Objects.Player;

public class Main {

    public static void main(String[] args) {
        Service service = new Service();
        service.start(new GameFool(), 3, 36);
        //removing();
    }

    public static void removing(){
        CyclicList<Player> players = new CyclicList<>();
        for (int i = 1; i <= 10; i++) {
            players.add(new Player(i));
        }

        int counter = 0;
        for (Player player : players) {
            System.out.print(player);
            counter++;
            if (counter == 10) {
                break;
            }
        }

        System.out.println();
        counter = 0;
        for (Player player : players) {
            counter++;
            System.out.print(player);
            if (counter == 2) {
                players.remove(player);
                System.out.println();
                System.out.print(" remove " + player);
                System.out.println();
            }
            if (counter == 20) {
                break;
            }
        }

        System.out.print(" size " + players.getSize());

    }

}

package game.Launch;

import game.Objects.GameFool;

public class Main {

    public static void main(String[] args) {
        Service service = new Service();
        service.start(new GameFool(), 3, 36);
    }

}

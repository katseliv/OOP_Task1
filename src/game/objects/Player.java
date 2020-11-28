package game.objects;

public class Player {
    private final int name;

    public Player(int name) {
        this.name = name;
    }

    public int getName() {
        return name;
    }

    @Override
    public String toString() {
        final String ANSI_GREEN = "\u001B[32m";
        final String ANSI_BLACK = "\u001B[30m";

        return ANSI_GREEN + "\nPlayer: " + ANSI_BLACK + name;
    }
}

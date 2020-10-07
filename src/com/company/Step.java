package com.company;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Step {
    Player target;
    LinkedHashMap<Player, HashMap<Card, Card>> list = new LinkedHashMap<>();

    public Step(Player target) {
        this.target = target;
    }

    public Player getTarget() {
        return target;
    }

    public LinkedHashMap<Player, HashMap<Card, Card>> getList() {
        return list;
    }
}

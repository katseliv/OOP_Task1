package com.company;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Step {
    Player target;
    LinkedHashMap<Player, HashMap<Card, Card>> list = new LinkedHashMap<>();
    int startField;
    int finishField;
}

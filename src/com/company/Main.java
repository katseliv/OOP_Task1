package com.company;

public class Main {

    public static void main(String[] args) {
        Service service = new Service();
        service.start(new Fool(), 3, 36);
    }
}

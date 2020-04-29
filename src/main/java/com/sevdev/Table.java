package com.sevdev;

import java.util.Random;

public class Table {

    private Integer tableId = 0;
    private Deck deck;

    public Table() {
        if (tableId == 0) {
            Random r = new Random();
            tableId = r.nextInt(1000);
        }

        deck = new Deck();
        deck.initialize();
    }
    public String getTableId() { return tableId.toString(); }

    public String getDeckAsJSON() {
        return deck.getDeckAsJSON();
    }

    public void initializeDeck() {
        deck.initialize();
    }
}

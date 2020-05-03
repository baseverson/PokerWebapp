package com.sevdev;

public class Card {

    public Boolean hidden;
    private String suit;
    private String rank;

    public Card(String newSuit, String newRank) {
       hidden = true;
       suit = new String(newSuit);
       rank = new String(newRank);
    }

    public String getSuit() { return suit; }
    public String getRank() { return rank; }
}

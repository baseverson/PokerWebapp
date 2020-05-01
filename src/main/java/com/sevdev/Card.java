package com.sevdev;

public class Card {

    public Boolean hidden;
    public String suit;
    public String rank;

    public Card(String newSuit, String newRank) {
       hidden = false;
       suit = new String(newSuit);
       rank = new String(newRank);
    }

    public String getSuit() { return suit; }
    public String getRank() { return rank; }
}

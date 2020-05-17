package com.sevdev;

public class Card implements Comparable {

    public Boolean hidden;
    private String suit;
    private String rank;
    private Integer numberRank;

    /**
     * Constructor
     *
     * @param newSuit - card suit
     * @param newRank - card rank
     */
    public Card(String newSuit, String newRank) {
       hidden = true;
       suit = newSuit;
       rank = newRank;

       switch (rank) {
           case "2":
               numberRank = 2; break;
           case "3":
               numberRank = 3; break;
           case "4":
               numberRank = 4; break;
           case "5":
               numberRank = 5; break;
           case "6":
               numberRank = 6; break;
           case "7":
               numberRank = 7; break;
           case "8":
               numberRank = 8; break;
           case "9":
               numberRank = 9; break;
           case "10":
               numberRank = 10; break;
           case "J":
               numberRank = 11; break;
           case "Q":
               numberRank = 12; break;
           case "K":
               numberRank = 13; break;
           case "A":
               numberRank = 14; break;
       }
    }

    /**
     * Copy constructor.
     *
     * @param copyCard - card to copy
     */
    public Card(Card copyCard) {
        this.hidden = copyCard.hidden;
        this.suit = copyCard.suit;
        this.rank = copyCard.rank;
        this.numberRank = copyCard.numberRank;
    }

    public String getSuit() { return suit; }
    public String getRank() { return rank; }
    public Integer getNumberRank() { return numberRank; }

    @Override
    public int compareTo(Object compareCard) {
        return this.numberRank - ((Card)compareCard).getNumberRank();
    }
}

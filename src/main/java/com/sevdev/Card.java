package com.sevdev;

public class Card implements Comparable {

    public static final String[] suits = {"spades", "diamonds", "clubs", "hearts"};
    public static final String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
    public static final Integer[] rankNums = {2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};

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
       this.hidden = true;
       this.suit = newSuit;
       this.rank = newRank;

       switch (rank) {
           case "2":
               this.numberRank = 2; break;
           case "3":
               this.numberRank = 3; break;
           case "4":
               this.numberRank = 4; break;
           case "5":
               this.numberRank = 5; break;
           case "6":
               this.numberRank = 6; break;
           case "7":
               this.numberRank = 7; break;
           case "8":
               this.numberRank = 8; break;
           case "9":
               this.numberRank = 9; break;
           case "10":
               this.numberRank = 10; break;
           case "J":
               this.numberRank = 11; break;
           case "Q":
               this.numberRank = 12; break;
           case "K":
               this.numberRank = 13; break;
           case "A":
               this.numberRank = 14; break;
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

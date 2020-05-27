package com.sevdev;

import java.util.ArrayList;
import java.util.List;

public class Seat {
    private Integer seatNum;
    private Boolean inHand;
    private Boolean isAllIn;
    private Seat next;
    private Player player;
    //private Card[] cards;
    private List<Card> cards;
    private Integer playerBet=0;

    public Integer getSeatNum() { return this.seatNum; }
    public Seat getNext() { return this.next; }
    public Player getPlayer() { return this.player; }
    public Boolean getInHand() { return this.inHand;}
    public Boolean getIsAllIn() { return this.isAllIn;}
    public Integer getPlayerBet() { return this.playerBet; }

    public void setPlayer(Player newPlayer) { this.player = newPlayer; }
    public void setNext(Seat newNext) { this.next = newNext; }
    public void setInHand(Boolean inCurrentHand) { this.inHand = inCurrentHand.booleanValue(); }
    public void setIsAllIn(Boolean newIsAllIn) { this.isAllIn = newIsAllIn.booleanValue(); }
    public void setPlayerBet(Integer newBet) { this.playerBet = newBet.intValue(); }
    public void increasePlayerBet(Integer newBet) { this.playerBet += newBet.intValue(); }

    public Seat (int newSeatNum) {
        seatNum = newSeatNum;
        inHand = false;
        isAllIn = false;
        //cards = new Card[2];
        cards = new ArrayList<Card>();
    }

    public void clearCards() {
        cards.clear();
        //cards[0] = null;
        //cards[1] = null;
    }

    //public void addCard(int index, Card card) {
    public void addCard(Card card) {
        cards.add(card);
        //cards[index] = card;
    }

    //public Card[] getCards() { return cards; }
    public List<Card> getCards() { return cards; }
}

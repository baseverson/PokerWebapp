package com.sevdev;

public class Seat {
    private Integer seatNum;
    private Boolean inHand;
    private Seat next;
    private Player player;
    private Card[] cards;

    public Integer getSeatNum() { return this.seatNum; }
    public Seat getNext() { return this.next; }
    public Player getPlayer() { return this.player; }
    public Boolean getInHand() {return this.inHand;}

    public void setPlayer(Player newPlayer) { this.player = newPlayer; }
    public void setNext(Seat newNext) { this.next = newNext; }
    public void setInHand(Boolean inCurrentHand) { this.inHand = inCurrentHand.booleanValue(); }

    public Seat (int newSeatNum) {
        seatNum = newSeatNum;
        inHand=false;
        cards = new Card[2];
    }

    public void clearCards() {
        cards[0] = null;
        cards[1] = null;
    }

    public void addCard(int index, Card card) {
        cards[index] = card;
    }
    public Card[] getCards() { return cards; }
}

package com.sevdev;

public class Seat {
    private Integer seatNum;
    private Seat next;
    private Player player;
    private Card[] cards;

    public Integer getSeatNum() { return seatNum; }
    public Seat getNext() { return next; }
    public Player getPlayer() { return player; }

    public void setPlayer(Player newPlayer) { player = newPlayer; }
    public void setNext(Seat newNext) { next = newNext; }

    public Seat (int newSeatNum) {
        seatNum = newSeatNum;
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

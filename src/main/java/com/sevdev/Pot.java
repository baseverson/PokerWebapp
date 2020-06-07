package com.sevdev;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class Pot {

    List<Card> board;
    List<Seat> seatList = new ArrayList<Seat>();
    List<Hand> handList;
    Integer potSize = 0;
    private HandType winningHand = HandType.UNDEFINED;

    public void setBoard(List<Card> newBoard) { this.board = newBoard; }
    public void setHandList(List<Hand> newHandList) { this.handList = newHandList; }

    public List<Hand> getHandList() { return this.handList; }
    public Integer getPotSize() { return this.potSize; }

    /**
     * Return a list of the player names that are eligible for this pot.
     *
     * @return - list of player names eligible for this pot
     */
    public List<Integer> getSeatNumberList() {
        List<Integer> seatNumberList = new ArrayList<Integer>();

        // Loop through the list of seats and collect the names of the players
        for (Seat seat : seatList) {
            seatNumberList.add(seat.getSeatNum());
        }

        return seatNumberList;
    }

    /**
     * Add a new player as eligible to win this pot.
     *
     * @param seat - seat object eligible to win this pot
     */
    public void addSeat(Seat seat) {
        if (seat != null) {
            seatList.add(seat);
        }
    }

    /**
     * Remove the specificed seat from the list of seats eligible to win this pot.
     *
     * @param seatNum - Seat number to remove from the eligible list for this pot.
     */
    public void removeSeat(Integer seatNum) {
        // Run through the list of seats looking for the one with the specified number
        ListIterator<Seat> seatIterator = seatList.listIterator();
        while (seatIterator.hasNext()) {
            Seat seat = seatIterator.next();
            if (seat.getSeatNum() == seatNum) {
                seatIterator.remove();
            }
        }
/*
        for (Seat seat : seatList) {
            if (seat.getSeatNum() == seatNum) {
                // The number matches. Remove this seat from the list.
                seatList.remove(seat);
            }
        }
 */
    }

    /**
     * Add chips to this pot by increasing its size by the amount specified.
     *
     * @param addedChips - amount of chips to add to this pot.
     */
    public void incrementSize(Integer addedChips) {
        potSize += addedChips;
    }
}

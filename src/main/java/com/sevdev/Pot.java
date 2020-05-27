package com.sevdev;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pot {

    List<Card> board;
    List<Seat> seatList = new ArrayList<Seat>();
    List<Hand> handList;
    Integer potSize = 0;

    public List<Hand> getHandList() { return this.handList; }
    public void setHandList(List<Hand> newHandList) { this.handList = newHandList; }

    public Integer getPotSize() {
        return this.potSize;
    }

    public void setBoard(List<Card> newBoard) {
        this.board = newBoard;
    }

    /**
     * Add a new player as eligible to win this pot.
     *
     * @param seat - seat object eligible to win this pot
     * @throws Exception seat is null
     */
    public void addSeat(Seat seat) throws Exception {
        if (seat == null) {
            throw new Exception("Player is null");
        }

        // Add the specified player to the player list.
        seatList.add(seat);
    }

    /**
     * Remove the specificed seat from the list of seats eligible to win this pot.
     *
     * @param seatNum - Seat number to remove from the eligible list for this pot.
     */
    public void removeSeat(Integer seatNum) {
        // Run through the list of seats looking for the one with the specified number
        for (Seat seat : seatList) {
            if (seat.getSeatNum() == seatNum) {
                // The number matches. Remove this seat from the list.
                seatList.remove(seat);
            }
        }
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

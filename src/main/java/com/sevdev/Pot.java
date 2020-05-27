package com.sevdev;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pot {

    List<Seat> seatList = new ArrayList<Seat>();
    List<Hand> handList = new ArrayList<Hand>();
    Integer size;

    public Integer getSize() { return this.size; }

    /**
     * Constructor
     */
    public void Pot() {

    }

    /**
     * Add a new player as eligible to win this pot.
     *
     * @param seat - seat object eligible to win this pot
     *
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
        size += addedChips;
    }

    /**
     * Determine which of the eligible seats is/are the winner(s).  Award the chips.
     */
    public void awardPot() {
        // TODO
    }
}

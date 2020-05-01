package com.sevdev;

public class TableState {

    public Integer tableId = 0;
    public Integer numSeats = 0;
    public Integer numPlayers = 0;
    public Integer bigBlind = 0;
    public Integer pot = 0;
    public Integer dealerPosition = 0;
    public Integer smallBlindPosition = 0;
    public Integer bigBlindPosition = 0;
    public Integer currentAction = 0;
    public Card board[];
    public Seat seats[];

    public void initialize(Integer newTableId, Integer newNumSeats, Integer newBigBlind) {
        tableId = newTableId;
        numSeats = newNumSeats;
        bigBlind = newBigBlind;
        pot = 123;
        dealerPosition = 1;
        smallBlindPosition = 2;
        bigBlindPosition = 3;
        currentAction = 4;
        board = new Card[5];
        seats = new Seat[8];
    }

    /**
     * Specify a player is taking an open seat.
     *
     * @param playerId - Name of the player
     * @param seatNum - Seat the player wants to occupy
     */
    public void addPlayer(String playerId, int seatNum) {
        // TODO
    }

    /**
     * Specify a player is leaving a seat.
     *
     * @param seatNum - Seat the player is vacating
     */
    public void removePlayer(int seatNum) {
        // TODO
    }

    /**
     * Reset the game for the next round.
     */
    public void resetGame() {
        // Reset the board
        for (int i=0; i<5; i++) {
            board[i] = null;
        }

        // TODO - clean up the pot
        // TODO - clean up the player cards
        // TODO - move the dealer button to the next player
    }

    public void dealPlayerCard(int seatNum, Card card) {

    }

    /**
     * Method for passing in a card for the board.
     *
     * @param cardNum - specifies the card in the board
     * @param card - card dealt to the board
     */
    public void dealBoardCard(int cardNum, Card card) {
        board[cardNum] = card;
    }

    public void incrementDealerPosition() {
        // TODO
    }

    public void incrementActionPosition() {
        // TODO
    }

    public void newRound() {
        // TODO
    }
}

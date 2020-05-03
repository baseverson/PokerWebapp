package com.sevdev;

public class TableState {

    private String playerName = new String("");
    private Integer tableId = 0;
    private Integer numSeats = 0;
    private Integer numPlayers = 0;
    private Integer bigBlind = 0;
    private Integer pot = 0;
    private Integer dealerPosition = 0;
    private Integer smallBlindPosition = 0;
    private Integer bigBlindPosition = 0;
    private Integer currentAction = 0;
    private RoundState roundState = RoundState.UNDEFINED;
    private Card board[];
    private Seat seats[];

    public String getPlayerName() { return playerName; }
    public Integer getTableId() { return tableId; }
    public Integer getNumSeats() { return numSeats; }
    public Integer getNumPlayers() { return numPlayers; }
    public Integer getBigBlind() { return bigBlind; }
    public Integer getPot() { return pot; }
    public Integer getDealerPosition() { return dealerPosition; }
    public Integer getSmallBlindPosition() { return smallBlindPosition; }
    public Integer getBigBlindPosition() { return bigBlindPosition; }
    public Integer getCurrentAction() { return currentAction; }
    public RoundState getRoundState() { return roundState; }
    public Card[] getBoard() { return board; }
    public Seat[] getSeats() { return seats; }

    public void setPlayerName(String newPlayerName) { playerName = newPlayerName; }
    public void setTableId(Integer newTableId) { tableId = newTableId.intValue(); }
    public void setNumSeats(Integer newNumSeats) { numSeats = newNumSeats.intValue(); }
    public void setNumPlayers(Integer newNumPlayers) { numPlayers = newNumPlayers.intValue(); }
    public void setBigBlind(Integer newBigBlind) { bigBlind = newBigBlind.intValue(); }
    public void setPot(Integer newPot) { pot = newPot.intValue(); }
    public void setDealerPosition(Integer newDealerPosition) { dealerPosition = newDealerPosition.intValue(); }
    public void setSmallBlindPosition(Integer newSmallBlindPosition) { smallBlindPosition = newSmallBlindPosition.intValue(); }
    public void setBigBlindPosition(Integer newBigBlindPosition) { bigBlindPosition = newBigBlindPosition.intValue(); }
    public void setCurrentAction(Integer newCurrentAction) { currentAction = newCurrentAction.intValue(); }
    public void setRoundState(RoundState newRoundState) { roundState = newRoundState; }

    /**
     * Copy board contents only for the card allowed to be displayed for the current round state.
     *
     * @param newBoard - board info to be copied
     */
    public void setBoard(Card[] newBoard) {
        board = new Card[newBoard.length];

        // Include board card info depending on the roundState
        switch (roundState) {
            case UNDEFINED:
                // do nothing - leave the board cards all null

                break;

            case PRE_FLOP:
                // Create card content w/ empty suit and rank.
                for (int i=0; i<board.length; i++) {
                    board[i] = new Card("","");
                    board[i].hidden = true;
                }

                break;

            case FLOP:
                // Create Flop content w/ rank/suit for first 3 cards, empty suit/rank for the rest.
                for (int i=0; i<3; i++) {
                    board[i] = new Card(board[i].getSuit(), board[i].getRank());
                    board[i].hidden = false;
                }
                // Turn
                board[3] = new Card("","");
                board[3].hidden = true;
                // Flop
                board[4] = new Card("","");
                board[4].hidden = true;

                break;

            case TURN:
                // Create Flop and Turn content w/ rank/suit for first 3 cards, empty suit/rank for the rest.
                for (int i=0; i<4; i++) {
                    board[i] = new Card(board[i].getSuit(), board[i].getRank());
                    board[i].hidden = false;
                }
                // Flop
                board[4] = new Card("","");
                board[4].hidden = true;

                break;

            case RIVER:
            case CLEAN_UP:
                // Create Flop and Turn content w/ rank/suit for first 3 cards, empty suit/rank for the rest.
                for (int i=0; i<5; i++) {
                    board[i] = new Card(board[i].getSuit(), board[i].getRank());
                    board[i].hidden = false;
                }

                break;
        }
    }

    /**
     * Copy only the seat info the current player is allowed to see
     *
     * @param newSeats - seat info to copy from
     */
    public void setSeats(Seat[] newSeats) {
        seats = new Seat[numSeats];

        for (int i=0; i<numSeats; i++) {
            seats[i] = new Seat(newSeats[i].getSeatNum().intValue());

            // If there is a player in this seat, copy the player info
            if (newSeats[i].getPlayer() != null) {
                seats[i].setPlayer(new Player(newSeats[i].getPlayer().getPlayerName(),
                                              newSeats[i].getPlayer().getStackSize()));

                // Only show the card info if this is the seat for the current player
                Card card1, card2;
                if (seats[i].getPlayer().getPlayerName() == playerName) {
                    Card[] newCards = newSeats[i].getCards();
                    card1 = new Card(newCards[0].getSuit(), newCards[0].getRank());
                    card1.hidden = false;
                    card2 = new Card(newCards[0].getSuit(), newCards[0].getRank());
                    card2.hidden = false;
                }
                else {
                    card1 = new Card("", "");
                    card1.hidden = true;
                    card2 = new Card("", "");
                    card2.hidden = true;
                }
                seats[i].addCard(0, card1);
                seats[i].addCard(1, card2);
            }
        }
    }
}

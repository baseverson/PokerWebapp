package com.sevdev;

import com.sevdev.RoundState.*;

import java.util.ArrayList;
import java.util.List;

import static com.sevdev.RoundState.*;

public class TableState {

    private String playerName = new String("");
    private Integer tableId = 0;
    private Integer numSeats = 0;
    private Integer numPlayers = 0;
    private Integer bigBlind = 0;
    private Integer pot = 0;
    private Integer currentBet = 0;
    private Integer currentBetPosition = 0;
    private Integer dealerPosition = 0;
    private Integer smallBlindPosition = 0;
    private Integer bigBlindPosition = 0;
    private Integer currentAction = 0;
    private Integer winningSeat = 0;
    private HandType winningHand = HandType.UNDEFINED;
    private RoundState roundState = RoundState.UNDEFINED;
    // TODO remove
    //private Card board[];
    //private Seat seats[];
    private List<Card> board;
    private List<Seat> seats;

    public String getPlayerName() { return playerName; }
    public Integer getTableId() { return tableId; }
    public Integer getNumSeats() { return numSeats; }
    public Integer getNumPlayers() { return numPlayers; }
    public Integer getBigBlind() { return bigBlind; }
    public Integer getPot() { return pot; }
    public Integer getCurrentBet() { return currentBet; }
    public Integer getCurrentBetPosition() { return currentBetPosition; }
    public Integer getDealerPosition() { return dealerPosition; }
    public Integer getSmallBlindPosition() { return smallBlindPosition; }
    public Integer getBigBlindPosition() { return bigBlindPosition; }
    public Integer getCurrentAction() { return currentAction; }
    public Integer getWinningSeat() { return winningSeat; }
    public HandType getWinningHand() { return winningHand; }
    public RoundState getRoundState() { return roundState; }
    public List<Card> getBoard() { return board; }
    public List<Seat> getSeats() { return seats; }

    public void setPlayerName(String newPlayerName) { playerName = newPlayerName; }
    public void setTableId(Integer newTableId) { tableId = newTableId.intValue(); }
    public void setNumSeats(Integer newNumSeats) { numSeats = newNumSeats.intValue(); }
    public void setNumPlayers(Integer newNumPlayers) { numPlayers = newNumPlayers.intValue(); }
    public void setBigBlind(Integer newBigBlind) { bigBlind = newBigBlind.intValue(); }
    public void setPot(Integer newPot) { pot = newPot.intValue(); }
    public void setCurrentBet(Integer newCurrentBet) { currentBet = newCurrentBet.intValue(); }
    public void setCurrentBetPosition(Integer newCurrentBetPosition) { currentBetPosition = newCurrentBetPosition.intValue(); }
    public void setDealerPosition(Integer newDealerPosition) { dealerPosition = newDealerPosition.intValue(); }
    public void setSmallBlindPosition(Integer newSmallBlindPosition) { smallBlindPosition = newSmallBlindPosition.intValue(); }
    public void setBigBlindPosition(Integer newBigBlindPosition) { bigBlindPosition = newBigBlindPosition.intValue(); }
    public void setCurrentAction(Integer newCurrentAction) { currentAction = newCurrentAction.intValue(); }
    public void setWinningSeat(Integer newWinningSeat) { winningSeat = newWinningSeat.intValue(); }
    public void setWinningHand(HandType newWinningHand) { winningHand = newWinningHand; }
    public void setRoundState(RoundState newRoundState) { roundState = newRoundState; }

    /**
     * Copy board contents only for the card allowed to be displayed for the current round state.
     *
     * @param newBoard - board info to be copied
     */
    // TODO remove
    //public void setBoard(Card[] newBoard) {
    //    board = new Card[newBoard.length];
    public void setBoard(List<Card> newBoard) {
        board = new ArrayList<Card>();

        // Include board card info depending on the roundState
        switch (roundState) {
            case UNDEFINED:
                // do nothing - leave the board cards all null

                break;

            case PRE_FLOP:
                // Create card content w/ empty suit and rank.
                // TODO remove
                //for (int i=0; i<board.length; i++) {
                //    board[i] = new Card("","");
                //    board[i].hidden = true;
                //}
                for (int i=0; i<newBoard.size(); i++) {
                    board.add(new Card("",""));
                    board.get(i).hidden = true;
                }

                break;

            case FLOP:
                // Create Flop content w/ rank/suit for first 3 cards, empty suit/rank for the rest.
                for (int i=0; i<3; i++) {
                    // TODO remove
                    //board[i] = new Card(newBoard[i].getSuit(), newBoard[i].getRank());
                    //board[i].hidden = false;
                    board.add(new Card(newBoard.get(i).getSuit(), newBoard.get(i).getRank()));
                    board.get(i).hidden = false;
                }
                // Turn
                // TODO remove
                //board[3] = new Card("","");
                //board[3].hidden = true;
                board.add(new Card("",""));
                board.get(3).hidden = true;
                // Flop
                // TODO remove
                //board[4] = new Card("","");
                //board[4].hidden = true;
                board.add(new Card("",""));
                board.get(4).hidden = true;

                break;

            case TURN:
                // Create Flop and Turn content w/ rank/suit for first 4 cards, empty suit/rank for the rest.
                for (int i=0; i<4; i++) {
                    // TODO remove
                    //board[i] = new Card(newBoard[i].getSuit(), newBoard[i].getRank());
                    //board[i].hidden = false;
                    board.add(new Card(newBoard.get(i).getSuit(), newBoard.get(i).getRank()));
                    board.get(i).hidden = false;
                }
                // Flop
                // TODO remove
                //board[4] = new Card("","");
                //board[4].hidden = true;
                board.add(new Card("",""));
                board.get(4).hidden = true;

                break;

            case RIVER:
            case SHOWDOWN:
                // Create board content for all cards
                for (int i=0; i<5; i++) {
                    board.add(new Card(newBoard.get(i).getSuit(), newBoard.get(i).getRank()));
                    board.get(i).hidden = false;
                }

                break;
        }
    }

    /**
     * Copy only the seat info the current player is allowed to see
     *
     * @param newSeats - seat info to copy from
     */
    // TODO remove
    //public void setSeats(Seat[] newSeats) {
    //    seats = new Seat[numSeats];
    public void setSeats(List<Seat> newSeats) {
        seats = new ArrayList<Seat>();

        for (int i=0; i<numSeats; i++) {
            // TODO remove
            //seats[i] = new Seat(newSeats[i].getSeatNum().intValue());
            seats.add(new Seat(newSeats.get(i).getSeatNum()));

            // If there is a player in this seat, copy the player info
            // TODO remove
            //if (newSeats[i].getPlayer() != null) {
            if (newSeats.get(i).getPlayer() != null) {
                // Get the seat to populate with copied data
                Seat seat = seats.get(i);

                // Get the seat to copy data from
                Seat newSeat = newSeats.get(i);

                // Copy over the player info - name and stack size
                seat.setPlayer(newSeat.getPlayer());

                // Copy over the in hand status
                seat.setInHand(newSeat.getInHand());

                // Copy over the All In status
                seat.setIsAllIn(newSeat.getIsAllIn());

                // Copy over the player bet
                seat.setPlayerBet(newSeat.getPlayerBet());

                // Only show the card info if this is the seat for the current player
                List<Card> displayCards = new ArrayList<Card>();
                Card displayCard;

                List<Card> newCards = newSeat.getCards();
                //if (newCards[0]!=null && newCards[1]!= null) {
                if (newCards.size() == 2) {
                    if (seat.getPlayer().getPlayerName().equals(playerName) || roundState == SHOWDOWN) {
                        displayCard = new Card(newCards.get(0).getSuit(), newCards.get(0).getRank());
                        displayCard.hidden = false;
                        seat.addCard(displayCard);

                        displayCard = new Card(newCards.get(1).getSuit(), newCards.get(1).getRank());
                        displayCard.hidden = false;
                        seat.addCard(displayCard);
                    }
                    else {
                        displayCard = new Card("", "");
                        displayCard.hidden = true;
                        seat.addCard(displayCard);

                        displayCard = new Card("", "");
                        displayCard.hidden = true;
                        seat.addCard(displayCard);
                    }
                }
                else {
                    // Add 2 null cards to show there are no cards
                    seat.addCard(null);
                    seat.addCard(null);
                }
            }
        }
    }
}

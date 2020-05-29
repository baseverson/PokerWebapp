package com.sevdev;

import com.sevdev.RoundState.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static com.sevdev.RoundState.*;

public class TableState {

    private String playerName = new String("");
    private Integer tableId = 0;
    private Integer numSeats = 0;
    private Integer numPlayers = 0;
    private Integer bigBlind = 0;
    private Integer currentBet = 0;
    private Integer currentBetPosition = 0;
    private Integer dealerPosition = 0;
    private Integer smallBlindPosition = 0;
    private Integer bigBlindPosition = 0;
    private Integer currentAction = 0;
    private List<Integer> winningSeats;
    private HandType winningHand = HandType.UNDEFINED;
    private RoundState roundState = RoundState.UNDEFINED;
    private List<Card> board;
    private List<Seat> seats;
    private List<Pot> potList;
    private Queue<String> log;

    public String getPlayerName() { return playerName; }
    public Integer getTableId() { return tableId; }
    public Integer getNumSeats() { return numSeats; }
    public Integer getNumPlayers() { return numPlayers; }
    public Integer getBigBlind() { return bigBlind; }
    public Integer getCurrentBet() { return currentBet; }
    public Integer getCurrentBetPosition() { return currentBetPosition; }
    public Integer getDealerPosition() { return dealerPosition; }
    public Integer getSmallBlindPosition() { return smallBlindPosition; }
    public Integer getBigBlindPosition() { return bigBlindPosition; }
    public Integer getCurrentAction() { return currentAction; }
    public List<Integer> getWinningSeats() { return winningSeats; }
    public RoundState getRoundState() { return roundState; }
    public List<Card> getBoard() { return board; }
    public List<Seat> getSeats() { return seats; }
    public List<Pot> getPotList() { return potList; }
    public Queue<String> getLog() { return log; }

    public void setPlayerName(String newPlayerName) { this.playerName = newPlayerName; }
    public void setTableId(Integer newTableId) { this.tableId = newTableId.intValue(); }
    public void setNumSeats(Integer newNumSeats) { this.numSeats = newNumSeats.intValue(); }
    public void setNumPlayers(Integer newNumPlayers) { this.numPlayers = newNumPlayers.intValue(); }
    public void setBigBlind(Integer newBigBlind) { this.bigBlind = newBigBlind.intValue(); }
    public void setCurrentBet(Integer newCurrentBet) { this.currentBet = newCurrentBet.intValue(); }
    public void setCurrentBetPosition(Integer newCurrentBetPosition) { this.currentBetPosition = newCurrentBetPosition.intValue(); }
    public void setDealerPosition(Integer newDealerPosition) { this.dealerPosition = newDealerPosition.intValue(); }
    public void setSmallBlindPosition(Integer newSmallBlindPosition) { this.smallBlindPosition = newSmallBlindPosition.intValue(); }
    public void setBigBlindPosition(Integer newBigBlindPosition) { this.bigBlindPosition = newBigBlindPosition.intValue(); }
    public void setCurrentAction(Integer newCurrentAction) { this.currentAction = newCurrentAction.intValue(); }
    public void setWinningSeats(List<Integer> newWinningSeats) { this.winningSeats = newWinningSeats; }
    public void setRoundState(RoundState newRoundState) { this.roundState = newRoundState; }
    public void setPotList(List<Pot> newPotList) { this.potList = newPotList; }
    public void setLog(Queue<String> newLog) { this.log = newLog; }

    /**
     * Copy board contents only for the card allowed to be displayed for the current round state.
     *
     * @param newBoard - board info to be copied
     */
    public void setBoard(List<Card> newBoard) {
        board = new ArrayList<Card>();

        // Include board card info depending on the roundState
        switch (roundState) {
            case UNDEFINED:
                // do nothing - leave the board cards all null

                break;

            case PRE_FLOP:
                // Create card content w/ empty suit and rank.
                for (int i=0; i<newBoard.size(); i++) {
                    board.add(new Card("",""));
                    board.get(i).hidden = true;
                }

                break;

            case FLOP:
                // Create Flop content w/ rank/suit for first 3 cards, empty suit/rank for the rest.
                for (int i=0; i<3; i++) {
                    board.add(new Card(newBoard.get(i).getSuit(), newBoard.get(i).getRank()));
                    board.get(i).hidden = false;
                }
                // Turn
                board.add(new Card("",""));
                board.get(3).hidden = true;
                // Flop
                board.add(new Card("",""));
                board.get(4).hidden = true;

                break;

            case TURN:
                // Create Flop and Turn content w/ rank/suit for first 4 cards, empty suit/rank for the rest.
                for (int i=0; i<4; i++) {
                    board.add(new Card(newBoard.get(i).getSuit(), newBoard.get(i).getRank()));
                    board.get(i).hidden = false;
                }
                // Flop
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
    public void setSeats(List<Seat> newSeats) {
        seats = new ArrayList<Seat>();

        for (int i=0; i<numSeats; i++) {
            seats.add(new Seat(newSeats.get(i).getSeatNum()));

            // If there is a player in this seat, copy the player info
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

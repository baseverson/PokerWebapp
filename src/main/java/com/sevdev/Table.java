package com.sevdev;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Random;

public class Table {

    private Deck deck;

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

    public Integer getTableId() { return tableId; }

    /**
     * Constructor
     */
    public Table() {
        if (tableId == 0) {
            Random r = new Random();
            tableId = r.nextInt(1000);
        }
    }

    /**
     * Initialize the table for a fresh start. Clears everything back to a starting state.
     * @param newNumSeats
     * @param newBigBlind
     */
    public void initialize(Integer newNumSeats, Integer newBigBlind) {
        deck = new Deck();

        numSeats = newNumSeats;
        bigBlind = newBigBlind;
        pot = 0;
        dealerPosition = 0;
        currentAction = 0;
        board = new Card[5];
        seats = new Seat[newNumSeats];

        // initialize the seat array
        int i=0;
        if (numSeats >=1) { seats[i] = new Seat(i+1); }
        for (i=1; i<numSeats; i++) {
           seats[i] = new Seat(i+1);
           seats[i-1].setNext(seats[i]);
        }
        seats[i-1].setNext(seats[0]);
    }

    /**
     * Retrieve the current state of the table. This will be customized based on the playerName,
     * showing him/her only the info they are currently allowed to see.
     *
     * @param playerName - the name of the player requesting the table state
     *
     * @return String containing the current state of the table (JSON formatted string)
     */
    public String getTableStateAsJSON(String playerName) {

        TableState tableState = new TableState();

        // Include all publicly allowed data.
        tableState.setPlayerName(playerName);
        tableState.setTableId(tableId);
        tableState.setNumSeats(numSeats);
        tableState.setNumPlayers(numPlayers);
        tableState.setBigBlind(bigBlind);
        tableState.setPot(pot);
        tableState.setDealerPosition(dealerPosition);
        tableState.setSmallBlindPosition(smallBlindPosition);
        tableState.setBigBlindPosition(bigBlindPosition);
        tableState.setCurrentAction(currentAction);
        tableState.setRoundState(roundState);
        tableState.setBoard(board);
        tableState.setSeats(seats);

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(tableState);
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error converting TableState object to JSON.";
        }
    }

    /**
     * Method for indicating a player wishes to sit in a seat.
     *
     * @param playerName - the name of the player wishing to sit in the seat
     * @param seatNum - the seat the player wishes to sit in
     *
     * @return String indicating the outcome of the sit-down request
     */
    public String sitDown(String playerName, int seatNum) {
        // check to see if the seat is already taken
        if (seats[seatNum-1].getPlayer() != null) {
            // This seat is already taken.
            return "Failed to seat player \"" + playerName + "\" in seat " + seatNum + ". Seat already taken.";
        }
        else {
            // Seat is open
            // TODO - fix hard coded stack size
            seats[seatNum-1].setPlayer(new Player(playerName, 1000));
            numPlayers++;
            return "player \"" + playerName + "\" successfully seated in seat " + seatNum;
        }
    }

    /**
     * Method for indicating a player wishes to leave the table.
     *
     * @param seatNum - Seat the player wants to leave
     *
     * @return String indicating the outcome of the request to leave the table
     */
    public String leaveTable(int seatNum) {

        // TODO: Check to make sure the player is really in the seat
        if (seats[seatNum-1].getPlayer() != null) {
            seats[seatNum-1].setPlayer(null);
            numPlayers--;
            // TODO - other cleanup for player leaving? (e.g. update DB for remaining chips)

            return "The player has successfully left seat " + seatNum;
        }
        else {
            return " No player in seat " + seatNum + ".";
        }
    }

    /**
     * Return the current seat number the player designated by playerName is occupying.
     *
     * @param playerName - Name of the player for which to find the seat number.
     *
     * @return The seat number the player is currently occupying. Retunrs 0 if the player is not currently in a seat.
     */
    public Integer getPlayerSeatNum(String playerName) {
        for (int i=0; i<numSeats; i++) {
            if (seats[i]!=null && seats[i].getPlayer().getPlayerName()==playerName) {
                // Found the player. Return the seat number.
                return seats[i].getSeatNum();
            }
        }

        // If we got here, the player was not found in any seat. Return 0;
        return 0;
    }

    /**
     * Reset the table to finish a round and prepare for the next round.
     */
    public void finishRound() {
        // Reset the board
        for (int i=0; i<5; i++) {
            board[i] = null;
        }
        // TODO - clean up the pot
        // TODO - clean up the player cards
    }

    /**
     * Sets up the next round.
     *
     * @throws Exception - Not enough players sitting at the table to start a new round.  Need minimum of 2.
     */
    public void newRound() throws Exception {
        roundState = RoundState.PRE_FLOP;

        // Can only start a new round if there are at least 2 players at the table
        if (numPlayers <2) {
            Exception e = new Exception("Not enough players to start a new round.  Need a minimum of 2.");
            throw e;
        }

        // Move the dealer button
        if (dealerPosition==0) {
            // This must be the very first game.  Randomly pick a seat and then move the dealer button to the next player.
            Random r = new Random();
            dealerPosition = r.nextInt(numSeats) + 1;
        }
        incrementDealerPosition();

        // Initialize a new deck
        deck.shuffle();
        // TODO: remove test output
        System.out.println(deck.getDeckAsJSON().toString());

        // TODO - pull small/big blinds

        // TODO - deal player cards
        // Deal 2 cards to each player
        for (int i=0; i<2; i++) {
            Seat seatPtr = seats[dealerPosition-1].getNext();
            while (seatPtr.getSeatNum() != dealerPosition) {
                // If there is a player in this seat, deal a card
                if (seatPtr.getPlayer() != null) {
                    seatPtr.addCard(i, deck.getCard());
                }
                seatPtr = seatPtr.getNext();
            }
            // Don't forget to deal a card to the dealer
            seatPtr.addCard(i, deck.getCard());
        }

        // Deal board cards
        // Flop
        board[0] = deck.getCard();
        board[1] = deck.getCard();
        board[2] = deck.getCard();
        // Burn a card
        deck.getCard();
        // Turn
        board[3] = deck.getCard();
        // Burn a card
        deck.getCard();
        // River
        board[4] = deck.getCard();

        // TODO: set action position
    }

    /**
     * Move the dealer button to the next player (the next seat with a player in it).
     */
    public void incrementDealerPosition() {
        // Find the next seat with a player in it.
        Seat nextSeat = seats[dealerPosition-1].getNext();

        // Keep going around until either the next player is found,
        // or we've circled the whole table and are back to the current dealer.
        while (nextSeat != seats[dealerPosition-1]) {
            // if nextSeat has a player in it, then this is the new dealer.
            if (nextSeat.getPlayer() != null) {
                dealerPosition = nextSeat.getSeatNum();
                break;
            }
            // If not, go to the next seat.
            else {
                nextSeat = nextSeat.getNext();
            }
        }
    }

    /**
     * TEST FUNCTION - Retrieve the current deck of cards.
     *
     * @return String containing the current deck of cards (JSON formatted string)
     */
    public String getDeckAsJSON() {
        return deck.getDeckAsJSON();
    }

    /**
     * TEST FUNCTION - Initialize a new deck of cards.
     *
     * @return None.
     */
    public void initializeDeck() {
        deck.shuffle();
    }

    public static void main(String[] args) {
        Table myTable = new Table();

        try {
//            for (int i=0; i<1000000; i++) {
                myTable.initialize(8, 2);

                myTable.sitDown("Brandt", 4);
                myTable.sitDown("Traci", 5);
                myTable.sitDown("Zoe", 1);
                myTable.sitDown("Claire", 7);

                myTable.newRound();
                System.out.println("dealerPosition: " + myTable.dealerPosition);

                for (int j=0; j<myTable.numSeats; j++) {
                    Seat seat = myTable.seats[j];
                    Player player = seat.getPlayer();
                    System.out.println("Seat " + seat.getSeatNum());
                    if (player == null) {
                        System.out.println("Seat empty.");
                    }
                    else {
                        System.out.println(player.getPlayerName());
                        System.out.println(
                                seat.getCards()[0].getRank() + "_" + seat.getCards()[0].getSuit() + ", " +
                                seat.getCards()[1].getRank() + "_" + seat.getCards()[1].getSuit());
                    }
                    System.out.println("");
                }
 //           }
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}

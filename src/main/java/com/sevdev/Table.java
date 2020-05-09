package com.sevdev;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Random;

import static com.sevdev.RoundState.*;

public class Table {

    // Make this a singleton
    private static Table tableInstance = null;
    public static Table getTable() {
        if (tableInstance==null) {
            tableInstance = new Table();
        }
        return tableInstance;
    }

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

        // TODO: For now, run intialize in the construtor. Later we may support multiple tables and the creation
        // of new tables.
        initialize(8, 2);
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

        System.out.println("Getting table state - playerName = '" + playerName + "'");

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
     * Send a notification to the specified player that the table state has been updated
     * and the UI should be refreshed.
     *
     * A playerName value of "ALL" will notify all players sitting at the table.
     *
     * @param playerName
     */
    public void sendTableStateChangeNotification(String playerName) {
        WebSocketSessionManager sessionManager = WebSocketSessionManager.getInstance();

        if (playerName.equals("ALL_SEATED_PLAYERS")) {
            // Notify all players
            for (int i=0; i<seats.length; i++) {
                if (seats[i].getPlayer() != null) {
                    sessionManager.notifyPlayer(seats[i].getPlayer().getPlayerName(), "TableUpdated");
                }
            }
        }
        else {
            sessionManager.notifyPlayer(playerName, "TableUpdated");
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

            // Notify all players that the table has been updates
            sendTableStateChangeNotification("ALL");

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

            // Notify all players that the table has been updates
            sendTableStateChangeNotification("ALL");

            return "The player has successfully left seat " + seatNum;
        }
        else {
            return " No player in seat " + seatNum + ".";
        }
    }

    /**
     * Method for indicating a player wishes to fold.
     *
     * @param playerName - player who wished to fold
     *
     * @return - result of the fold
     */
    public String fold(String playerName) {
        int seatNum = getPlayerSeatNum(playerName);
        seats[seatNum-1].setInHand(false);
        seats[seatNum-1].clearCards();

        sendTableStateChangeNotification("ALL");

        return(playerName + " folded.");
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
            if (seats[i]!=null && seats[i].getPlayer().getPlayerName().equals(playerName)) {
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
        for (int i=0; i<seats.length; i++) {
            seats[i].setInHand(false);
            seats[i].clearCards();
        }
    }

    /**
     * Sets up the next round.
     *
     * @throws Exception - Not enough players sitting at the table to start a new round.  Need minimum of 2.
     */
    public void newRound() throws Exception {
        // Can only start a new round if there are at least 2 players at the table
        System.out.println("New Round requested. Current number of player: " + numPlayers);

        if (numPlayers <2) {
            Exception e = new Exception("Not enough players to start a new round.  Need a minimum of 2.");
            throw e;
        }

        roundState = PRE_FLOP;

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
                    seatPtr.setInHand(true);
                }
                seatPtr = seatPtr.getNext();
                seatPtr.setInHand(true);
            }
            // Don't forget to deal a card to the dealer
            seatPtr.addCard(i, deck.getCard());
        }

        // Deal board cards
        // Burn a card
        deck.getCard();
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

        // Notify all players that the table has been updates
        sendTableStateChangeNotification("ALL");
    }

    /**
     * Manually advance the round to the next state.
     *
     * @return New round state.
     */
    public RoundState advanceRound() {
       switch (roundState) {
           case PRE_FLOP:
               roundState = FLOP;
               break;
           case FLOP:
               roundState = TURN;
               break;
           case TURN:
               roundState = RIVER;
               break;
           case RIVER:
               roundState = SHOWDOWN;
               break;
           case SHOWDOWN:
              roundState = CLEAN_UP;
              finishRound();
              break;
       }

       // Notify all players that the table has been updates
       sendTableStateChangeNotification("ALL");

       return roundState;
    }

    /**
     * Find the next player from the specified seat position
     *
     * @param seatNum - seat number to start looking from
     *
     * @returns Seat position of the next player; 0 if next player not found
     */
    public int findNextPlayer(int seatNum) {
        // Find the next seat with a player in it.
        Seat nextSeat = seats[seatNum-1].getNext();

        // Keep going around until either the next player is found,
        // or we've circled the whole table and are back to the current position.
        while (nextSeat != seats[seatNum-1]) {
            // if nextSeat has a player in it, then this is the next player.
            if (nextSeat.getPlayer() != null) {
                return nextSeat.getSeatNum();
            }
            // If not, go to the next seat.
            else {
                nextSeat = nextSeat.getNext();
            }
        }

        // No next player found. Return 0
        return 0;
    }

    /**
     * Move the dealer button to the next player (the next seat with a player in it).
     */
    public void incrementDealerPosition() {
        dealerPosition = findNextPlayer(dealerPosition);
        smallBlindPosition = findNextPlayer(dealerPosition);
        bigBlindPosition = findNextPlayer(smallBlindPosition);
        currentAction = findNextPlayer(bigBlindPosition);
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

/*
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
                    System.out.println("");
                }
 */

            myTable.advanceRound(); // to FLOP
            myTable.advanceRound(); // to TURN
            myTable.advanceRound(); // to RIVER
            myTable.advanceRound(); // to SHOWDOWN
            myTable.advanceRound(); // to CLEAN_UP

            System.out.println("");
            System.out.println("");
            System.out.println(myTable.getTableStateAsJSON("Brandt"));
//           }
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}
